package nano.service.nano.repository;

import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static nano.support.EntityUtils.slim;

@Repository
public class PostgresRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public PostgresRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> query(@NotNull String sql) {
        var rowMapper = new ColumnMapRowMapper();
        return this.jdbcTemplate.query(slim(sql), rowMapper);
    }

    public List<Map<String, Object>> queryTable(@NotNull String name) {
        var sql = """
                SELECT A.attnum AS num,
                       (SELECT description FROM pg_catalog.pg_description WHERE objoid = A.attrelid AND objsubid = A.attnum) AS description,
                       A.attname AS name,
                       (SELECT typname from pg_type where oid = A.atttypid) AS type,
                       A.atttypmod AS data_type
                FROM pg_catalog.pg_attribute A
                WHERE TRUE
                  AND A.attrelid = (SELECT oid FROM pg_class WHERE relname = :name)
                  AND A.attnum > 0
                  AND NOT A.attisdropped
                ORDER BY A.attnum;
                """;
        var rowMapper = new ColumnMapRowMapper();
        return this.jdbcTemplate.query(slim(sql), Map.of("name", name), rowMapper);
    }
}
