package results.orchestration;

import results.BenchmarkResult;

public interface Orchestraction {

    void setUpForAdd();

    void setUpForRemove();

    void validateStateBeforeAddTest();

    void validateStateBeforeRemoveTest();

    BenchmarkResult testAddNTimes(String theOperationName);

    BenchmarkResult testRemoveNTimes(String theOperationName);
}
