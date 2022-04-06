package nano.service.nano.entity;

import lombok.Builder;

import java.sql.Timestamp;

public record NanoTask(
        Integer id,
        String name,
        String description,
        String options,
        String status,
        Timestamp startTime,
        Timestamp endTime,
        String creationOwner,
        Timestamp creationTime
) {
    public static final String CREATED = "CREATED";
    public static final String RUNNING = "RUNNING";
    public static final String DONE = "DONE";
    public static final String ERROR = "ERROR";

    @Builder
    public NanoTask {
    }
}
