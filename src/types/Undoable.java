package types;

public sealed interface Undoable extends DataType
        permits UndoRecord, Action  {

}
