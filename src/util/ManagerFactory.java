package util;

import manager.telemetry.OperationsManager;
import types.DataType;

public class ManagerFactory <T extends DataType, C extends OperationCountable<T>, M extends OperationsManager<T,M>> {

    public ManagerFactory() {
    }
}
