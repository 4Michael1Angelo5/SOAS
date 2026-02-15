package simulator;

import benchmark.BenchmarkRunner;
import loader.DataLoader;
import manager.RosterManager;
import manager.TransactionFeed;
import manager.UndoManager;
import results.Results;
import types.*;
import util.ArrayStack;
import util.ArrayStore;
import util.DataContainer;
import util.LinkedQueue;

import java.io.IOException;
import java.util.HashSet;
import java.util.function.Supplier;
import java.util.logging.Logger;


public class Simulator {

    // logger color formatting
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RESET = "\u001B[0m";

    // logger
    private static final Logger logger = Logger.getLogger(Results.class.getName());

    //Boilerplate to get logger to look good.
    static {
        // attempt to use logging properties file
        try (var is = Results.class.getClassLoader().getResourceAsStream("logging.properties")) {
            if (is != null) {
                java.util.logging.LogManager.getLogManager().readConfiguration(is);
            } else {
                // Fallback: Manually set the format if file isn't found
                System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");
            }
        } catch (Exception e) {
            System.setProperty("java.util.logging.SimpleFormatter.format", "%5$s%n");
        }
    }

    //=============================  CSV Data ==================================

    // roster
    public static final String ROSTER5000 = "data/seahawks_roster_5000.csv";

    // transaction
    public static final String TRANS5000 = "data/seahawks_transactions_5000.csv";

    //undo actions
    public static final String UNDO5000 = "data/seahawks_undo_actions_5000.csv";

    // Fan Requests
    public static final String FAN50 = "data/seahawks_fan_queue_5000.csv";

    //============================ Suppliers ====================================

    // array with player data
    public static final Supplier<DataContainer<Player>>
            SUPPLIER_ARRAY_PLAYER = ()-> new ArrayStore<>(Player.class,64);

    // array for transactions
    public static final Supplier<DataContainer<Transaction>>
            SUPPLIER_ARRAY_TRANS = ()-> new ArrayStore<>(Transaction.class,64);

    // queue for list of actions to perform (FIFO)
    public static final Supplier<DataContainer<Action>>
            SUPPLIER_QUEUE_UNDO = LinkedQueue::new;

    // stack for list of actions to undo (LIFO)
    public static final Supplier<DataContainer<Action>>
            SUPPLIER_STACK_UNDO = ()-> new ArrayStack<>(Action.class);

    //============================ Managers ====================================

    public static final RosterManager RM = new RosterManager(SUPPLIER_ARRAY_PLAYER);
    public static final TransactionFeed TM = new TransactionFeed(SUPPLIER_ARRAY_TRANS);
    public static final UndoManager UM = new UndoManager(SUPPLIER_STACK_UNDO);

    //============================= Loaders ===============================
    DataLoader<Action> actionsLoader = new DataLoader<>(Action.class, SUPPLIER_QUEUE_UNDO);

    //=============BackStore for actions performed/undo =========================

    public HashSet<Player> rosterBefore = new HashSet<>();
    public HashSet<Transaction> transactionsBefore = new HashSet<>();

    public LinkedQueue<Action> actionsToPerform = new LinkedQueue<>();
    public ArrayStack<UndoRecord> actionsToUndo = new ArrayStack<>(UndoRecord.class);

    public ArrayStore<Action> actionRequestsUnfulfilled = new ArrayStore<>(Action.class, 64);

    private final static int SAMPLE_SIZE = 6000;

    private int numRequests;
    private int rosterSize;
    private int transactionSize;

    private int successfulRosterUndos;
    private int successfulTransactionUndos;

    public Simulator() {
        super();
    }

    private void dispatchAction(Action theAction) {
        switch(theAction.action_type()) {
            case ADD_PLAYER -> {
                Player newPlayer = new Player(
                        SAMPLE_SIZE + theAction.id(),
                        theAction.target(),
                        "some position",
                        88,
                        99);
                int indexOfAddedPlayer = RM.getData().size();
                RM.addPlayer(newPlayer); // adds at end of roster
                UndoRecord theActionToUndo = new UndoRecord(theAction, newPlayer,indexOfAddedPlayer);
                actionsToUndo.push(theActionToUndo);
                UM.addData(theAction);
            }
            case REMOVE_PLAYER -> {

                int idx = RM.findByName(theAction.target());
                if (idx < 0) {
                    actionRequestsUnfulfilled.add(theAction);
                    return;
                }

                Player playerRemoved = RM.getPlayerData().removeAt(idx);
                UndoRecord theActionToUndo = new UndoRecord(theAction, playerRemoved ,idx);
                actionsToUndo.push(theActionToUndo);
                UM.addData(theAction);

            }
            case ADD_TRANSACTION -> {
                Transaction newTransaction = new Transaction(
                        SAMPLE_SIZE + theAction.id(),
                        "Some Transaction",
                        theAction.target(),
                        "some time");

                int indexOfAddedTransaction = TM.getData().size();
                TM.addTransactionRear(newTransaction);
                UndoRecord theActionToUndo = new UndoRecord(theAction, newTransaction, indexOfAddedTransaction);
                actionsToUndo.push(theActionToUndo);
                UM.addData(theAction);
            }
            case REMOVE_TRANSACTION -> {
                int idx = TM.getTransactionData().findBy((t)->t.player().equals(theAction.target()));
                if (idx < 0) {
                    actionRequestsUnfulfilled.add(theAction);
                    return;
                }
                Transaction transactionRemoved = TM.getTransactionData().removeAt(idx);
                UndoRecord theActionToUndo = new UndoRecord(theAction, transactionRemoved, idx);
                actionsToUndo.push(theActionToUndo);
                UM.addData(theAction);
            }
            case UPDATE_STATS -> {
                int idx = RM.findByName(theAction.target());
                if (idx < 0) {
                    actionRequestsUnfulfilled.add(theAction);
                    return;
                }
                Player prevPlayer = RM.getPlayerData().get(idx);
                Player newPlayer = new Player(
                        prevPlayer.player_id(),
                        prevPlayer.name(),
                        prevPlayer.position(),
                        prevPlayer.jersey(),
                        prevPlayer.yards() + 500 // update yards

                );
                UndoRecord theActionToUndo = new UndoRecord(theAction, prevPlayer, idx);
                actionsToUndo.push(theActionToUndo);
                RM.getPlayerData().set(idx, newPlayer);
                UM.addData(theAction);
            }
        }
    }

    public <T extends DataType> void dispatchActionToUndo(UndoRecord theActionToUndo) {
        switch (theActionToUndo.action().action_type()) {
            case ADD_PLAYER -> {
                RM.removeAt(theActionToUndo.index());
                successfulRosterUndos++;
            }
            case REMOVE_PLAYER -> {
                RM.addPlayer((Player) theActionToUndo.previousState());
                successfulRosterUndos++;
            }
            case ADD_TRANSACTION -> {
                TM.removeAt(theActionToUndo.index());
                successfulTransactionUndos++;
            }
            case REMOVE_TRANSACTION -> {
                TM.addTransactionRear((Transaction) theActionToUndo.previousState());
                successfulTransactionUndos++;
            }
            case UPDATE_STATS -> {
                RM.getData().set(theActionToUndo.index(), (Player) theActionToUndo.previousState());
                successfulRosterUndos++;
            }
        }

    }

    public void runSimulation() throws IOException {

        //1a)
        // load csv data to the managers to initialize them.
        RM.loadPlayerData(ROSTER5000);
        TM.loadTransactionData(TRANS5000);


        //1b)
        // keep track of previous state before mutation:
        for (Player player : RM.getData()) {
            rosterBefore.add(player);
        }

        for (Transaction transaction: TM.getTransactionData()) {
            transactionsBefore.add(transaction);
        }


        //2)
        // load csv from undo actions to create a list of actions to perform
        // that will eventually be undone by the UndoManager
        actionsToPerform = (LinkedQueue<Action>) actionsLoader.loadData(UNDO5000);

        numRequests = actionsToPerform.size();


        //3)
        // process each action
        // and record previous state
        while (!actionsToPerform.isEmpty()) {
            Action actionToPerform = actionsToPerform.dequeue();
            dispatchAction(actionToPerform);
        }

        //4)
        // undo the actions performed
        while(!actionsToUndo.isEmpty()) {
            UndoRecord theActionToUndo = actionsToUndo.pop();
            dispatchActionToUndo(theActionToUndo);
        }

        //5)
        // validate current state of roster manager and transaction manager
        // matches initial state.
        if (isValidateRosterState()) {
            logger.info(
                    "The simulation processed " + numRequests + " requests.\n" +
                            "The simulation was able to carry out " +
                            (numRequests - actionRequestsUnfulfilled.size()) +
                            " actions and their corresponding inverse operation.\n" +
                            "The simulation successfully undid " +
                            successfulRosterUndos + " roster actions and " +
                            successfulTransactionUndos + " transactions."
            );
        }else {
            logger.warning("The simulation encountered an error.");
        }

    }

    private boolean isValidateRosterState() {

        DataContainer<Player> rosterAfter = RM.getPlayerData();
        DataContainer<Transaction> transactionsAfter = TM.getTransactionData();


        if (rosterAfter.size() != rosterBefore.size()) {
            logger.info("Something went wrong the roster before and the roster after do not have the same size");
        }
        if (transactionsAfter.size() != transactionsBefore.size()) {
            logger.info("Something went wrong the transaction before and the transaction after do not have the same size");
        }

        if (successfulRosterUndos + successfulTransactionUndos + actionRequestsUnfulfilled.size() != numRequests) {
            return false;
        }

        // passes

        for (Player player : rosterAfter) {
            if (!rosterBefore.contains(player)) {
                // Find the version of this player that exists in the 'Before' set by ID
                Player original = rosterBefore.stream()
                        .filter(p -> p.player_id() == player.player_id())
                        .findFirst()
                        .orElse(null);

                logger.info("Mismatch found for Player ID: " + player.player_id());
                logger.info("State After Undo:  " + player);
                logger.info("Original State:    " + original);
                return false;
            } else {
                rosterBefore.remove(player);
            }
        }

        for (Transaction trans : transactionsAfter) {
            if (!transactionsBefore.contains(trans)) {
                logger.info("not found, in transactions");
                return false;
            }else {
                transactionsBefore.remove(trans);

            }

        }

        return rosterBefore.isEmpty() && transactionsBefore.isEmpty();
    }

    public static void main(String[] args) throws IOException{
        Simulator sim = new Simulator();

        BenchmarkRunner br = new BenchmarkRunner();

        double res = br.runSpeedTest(1,()-> {
            try {
                sim.runSimulation();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        logger.info("The total time taken to run the simulations was: " + String.valueOf(res));

    }
}
