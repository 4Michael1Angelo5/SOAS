package types;

public record FanRequest(
        int fan_id,
        String name,
        String service_type,
        String arrival_time) implements DataType {

    @Override
    public int id() {
        return fan_id;
    }
}
