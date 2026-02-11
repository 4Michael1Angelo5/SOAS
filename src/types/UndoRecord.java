package types;

public record UndoRecord(
        Action action, // enum
        DataType previousState,
        Integer index
) implements Undoable {}
