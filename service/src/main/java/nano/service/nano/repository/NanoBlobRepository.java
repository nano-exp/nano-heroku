package nano.service.nano.repository;

import nano.service.nano.entity.NanoBlob;
import nano.support.jdbc.SimpleJdbcSelect;
import org.jetbrains.annotations.NotNull;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static nano.support.EntityUtils.slim;

@Repository
public class NanoBlobRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public NanoBlobRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public NanoBlob queryBlob(@NotNull String key) {
        var select = new SimpleJdbcSelect<>(NanoBlob.class)
                .withTableName("nano_blob").whereEqual("key").limit(1);
        return select.usesJdbcTemplate(this.jdbcTemplate).queryOne(Map.of("key", key));
    }

    public void upsertBlob(@NotNull String key, @NotNull String blob) {
        var sql = """
                INSERT INTO nano_blob (key, blob)
                VALUES (:key, :blob)
                ON CONFLICT (key)
                    DO UPDATE SET blob = EXCLUDED.blob;
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("key", key, "blob", blob));
    }

    public void deleteBlob(@NotNull List<@NotNull String> keyList) {
        var sql = """
                DELETE
                FROM nano_blob
                WHERE key IN (:key);
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("key", keyList));
    }
}
