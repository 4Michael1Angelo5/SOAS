package results;

public record BenchmarkResult(
        int inputSize,
        String method,
        double avgTime,
        OperationCounts operationCounts){

}
