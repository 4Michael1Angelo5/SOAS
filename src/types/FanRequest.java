package types;

import java.util.Objects;

public record FanRequest(
        int fan_id,
        String name,
        String service_type,
        String arrival_time) implements DataType {

    public FanRequest(int fan_id, String name, String service_type, String arrival_time) {
        this.fan_id = fan_id;
        this.name = name;
        this.service_type = service_type;
        this.arrival_time = arrival_time;
        validate();
    }

    @Override
    public int id() {
        return fan_id;
    }

    @Override
    public void validate() {
        Objects.requireNonNull(name, "name cannot be null");
        Objects.requireNonNull(service_type, "service_type cannot be null");
        Objects.requireNonNull(arrival_time, "arrival_time cannot be null");

        if (name.isBlank()) {
            throw new IllegalArgumentException("Fan name cannot be blank");
        }

        if (service_type.isBlank()) {
            throw new IllegalArgumentException("Service type cannot be blank");
        }

        if (arrival_time.isBlank()) {
            throw new IllegalArgumentException("Arrival time cannot be blank");
        }

        if (fan_id < 0) {
            throw new IllegalArgumentException("Fan ID cannot be negative");
        }
    }
}