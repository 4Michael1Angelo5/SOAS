package results;

import util.ArrayStore;

public class ExperimentResults {

    ArrayStore<BenchmarkResult> results = new ArrayStore<>(BenchmarkResult.class);

    public ExperimentResults(){
        super();
    }

    public void addExperimentResult(BenchmarkResult theResult) {
        results.add(theResult);
    }

    public ArrayStore<BenchmarkResult> getResults() {
        return results;
    }
}
