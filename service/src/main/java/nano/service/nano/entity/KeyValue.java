package nano.service.nano.entity;

import java.sql.Timestamp;

public record KeyValue(
        Integer id,
        String key,
        String value,
        Timestamp lastUpdatedTime,
        Timestamp creationTime
) {
}
