package results;

public sealed interface ExperimentInterface
        permits ExperimentResult, ExperimentResWithOps {


    int getInputSize();

    String getOperation();

    double getAvgTime();

}


