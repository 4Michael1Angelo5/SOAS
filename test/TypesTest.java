import org.junit.jupiter.api.Test;
import types.*;

import static org.junit.jupiter.api.Assertions.*;

public class TypesTest {

    // Action Tests
    @Test
    public void testValidAction() {
        Action action = new Action(1, ActionType.ADD_PLAYER, "player123", "2024-02-14");
        assertEquals(1, action.action_id());
        assertEquals(ActionType.ADD_PLAYER, action.action_type());
        assertEquals("player123", action.target());
        assertEquals("2024-02-14", action.timestamp());
    }

    @Test
    public void testActionNullActionType() {
        assertThrows(NullPointerException.class, () ->
                new Action(1, null, "player123", "2024-02-14")
        );
    }

    @Test
    public void testActionNullTarget() {
        assertThrows(NullPointerException.class, () ->
                new Action(1, ActionType.ADD_PLAYER, null, "2024-02-14")
        );
    }

    @Test
    public void testActionBlankTarget() {
        assertThrows(IllegalArgumentException.class, () ->
                new Action(1, ActionType.ADD_PLAYER, "   ", "2024-02-14")
        );
    }

    @Test
    public void testActionNullTimestamp() {
        assertThrows(NullPointerException.class, () ->
                new Action(1, ActionType.ADD_PLAYER, "player123", null)
        );
    }

    // Drill Tests
    @Test
    public void testValidDrill() {
        Drill drill = new Drill(1, "Passing Drill", 5);
        assertEquals(1, drill.drill_id());
        assertEquals("Passing Drill", drill.name());
        assertEquals(5, drill.urgency());
    }

    @Test
    public void testDrillNullName() {
        assertThrows(NullPointerException.class, () ->
                new Drill(1, null, 5)
        );
    }

    @Test
    public void testDrillBlankName() {
        assertThrows(IllegalArgumentException.class, () ->
                new Drill(1, "   ", 5)
        );
    }

    @Test
    public void testDrillNegativeId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Drill(-1, "Passing Drill", 5)
        );
    }

    @Test
    public void testDrillNegativeUrgency() {
        assertThrows(IllegalArgumentException.class, () ->
                new Drill(1, "Passing Drill", -1)
        );
    }

    // FanRequest Tests
    @Test
    public void testValidFanRequest() {
        FanRequest fan = new FanRequest(1, "John Doe", "Concessions", "12:30");
        assertEquals(1, fan.fan_id());
        assertEquals("John Doe", fan.name());
        assertEquals("Concessions", fan.service_type());
        assertEquals("12:30", fan.arrival_time());
    }

    @Test
    public void testFanRequestNullName() {
        assertThrows(NullPointerException.class, () ->
                new FanRequest(1, null, "Concessions", "12:30")
        );
    }

    @Test
    public void testFanRequestBlankServiceType() {
        assertThrows(IllegalArgumentException.class, () ->
                new FanRequest(1, "John Doe", "   ", "12:30")
        );
    }

    @Test
    public void testFanRequestNegativeId() {
        assertThrows(IllegalArgumentException.class, () ->
                new FanRequest(-1, "John Doe", "Concessions", "12:30")
        );
    }

    // Player Tests
    @Test
    public void testValidPlayer() {
        Player player = new Player(1, "Russell Wilson", "QB", 3, 1500);
        assertEquals(1, player.player_id());
        assertEquals("Russell Wilson", player.name());
        assertEquals("QB", player.position());
        assertEquals(3, player.jersey());
        assertEquals(1500, player.yards());
    }

    @Test
    public void testPlayerNullName() {
        assertThrows(NullPointerException.class, () ->
                new Player(1, null, "QB", 3, 1500)
        );
    }

    @Test
    public void testPlayerBlankPosition() {
        assertThrows(IllegalArgumentException.class, () ->
                new Player(1, "Russell Wilson", "   ", 3, 1500)
        );
    }

    @Test
    public void testPlayerNegativeId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Player(-1, "Russell Wilson", "QB", 3, 1500)
        );
    }

    @Test
    public void testPlayerInvalidJerseyTooHigh() {
        assertThrows(IllegalArgumentException.class, () ->
                new Player(1, "Russell Wilson", "QB", 100, 1500)
        );
    }

    @Test
    public void testPlayerInvalidJerseyNegative() {
        assertThrows(IllegalArgumentException.class, () ->
                new Player(1, "Russell Wilson", "QB", -1, 1500)
        );
    }

    @Test
    public void testPlayerNegativeYards() {
        assertThrows(IllegalArgumentException.class, () ->
                new Player(1, "Russell Wilson", "QB", 3, -100)
        );
    }

    // Transaction Tests
    @Test
    public void testValidTransaction() {
        Transaction trans = new Transaction(1, "TRADE", "Russell Wilson", "2024-02-14");
        assertEquals(1, trans.trans_id());
        assertEquals("TRADE", trans.type());
        assertEquals("Russell Wilson", trans.player());
        assertEquals("2024-02-14", trans.timestamp());
    }

    @Test
    public void testTransactionNullType() {
        assertThrows(NullPointerException.class, () ->
                new Transaction(1, null, "Russell Wilson", "2024-02-14")
        );
    }

    @Test
    public void testTransactionBlankPlayer() {
        assertThrows(IllegalArgumentException.class, () ->
                new Transaction(1, "TRADE", "   ", "2024-02-14")
        );
    }

    @Test
    public void testTransactionNegativeId() {
        assertThrows(IllegalArgumentException.class, () ->
                new Transaction(-1, "TRADE", "Russell Wilson", "2024-02-14")
        );
    }

    // UndoRecord Tests
    @Test
    public void testValidUndoRecord() {
        Action action = new Action(1, ActionType.ADD_PLAYER, "player123", "2024-02-14");
        Player player = new Player(1, "Test Player", "QB", 12, 100);
        UndoRecord undo = new UndoRecord(action, player, 0);

        assertEquals(action, undo.action());
        assertEquals(player, undo.previousState());
        assertEquals(0, undo.index());
    }

    @Test
    public void testUndoRecordNullAction() {
        Player player = new Player(1, "Test Player", "QB", 12, 100);
        assertThrows(NullPointerException.class, () ->
                new UndoRecord(null, player, 0)
        );
    }

    @Test
    public void testUndoRecordNullPreviousState() {
        Action action = new Action(1, ActionType.ADD_PLAYER, "player123", "2024-02-14");
        assertThrows(NullPointerException.class, () ->
                new UndoRecord(action, null, 0)
        );
    }

    @Test
    public void testUndoRecordNullIndex() {
        Action action = new Action(1, ActionType.ADD_PLAYER, "player123", "2024-02-14");
        Player player = new Player(1, "Test Player", "QB", 12, 100);
        assertThrows(NullPointerException.class, () ->
                new UndoRecord(action, player, null)
        );
    }

    @Test
    public void testUndoRecordNegativeIndex() {
        Action action = new Action(1, ActionType.ADD_PLAYER, "player123", "2024-02-14");
        Player player = new Player(1, "Test Player", "QB", 12, 100);
        assertThrows(IllegalArgumentException.class, () ->
                new UndoRecord(action, player, -1)
        );
    }
}