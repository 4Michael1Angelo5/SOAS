package types;

public record Action(
        int action_id,
        ActionType action_type,
        String target,
        String timestamp) implements DataType {


    @Override
    public int id() {
        return action_id;
    }
}
