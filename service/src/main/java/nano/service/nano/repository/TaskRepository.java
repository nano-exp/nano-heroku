package nano.service.nano.repository;

import nano.service.nano.entity.NanoTask;
import nano.support.jdbc.SimpleJdbcSelect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static nano.support.EntityUtils.slim;

@Repository
public class TaskRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TaskRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public @Nullable NanoTask queryTask(Integer id) {
        var select = new SimpleJdbcSelect<>(NanoTask.class)
                .withTableName("nano_task").whereEqual("id");
        return select.usesJdbcTemplate(this.jdbcTemplate).queryOne(Map.of("id", id));
    }

    public @NotNull List<@NotNull NanoTask> queryTaskList() {
        var select = new SimpleJdbcSelect<>(NanoTask.class).withTableName("nano_task");
        return select.usesJdbcTemplate(this.jdbcTemplate).query();
    }

    public Integer createTask(@NotNull NanoTask nanoTask) {
        var sql = """
                INSERT INTO nano_task (name, description, options, status, creation_owner, creation_time)
                VALUES (:name, :description, :options::JSONB, :status, :creationOwner, :creationTime);
                """;
        var paramSource = new BeanPropertySqlParameterSource(nanoTask);
        var kh = new GeneratedKeyHolder();
        this.jdbcTemplate.update(slim(sql), paramSource, kh);
        return ((Number) Objects.requireNonNull(kh.getKeys()).get("id")).intValue();
    }

    public void updateTaskStartStatus(@NotNull Integer id) {
        var sql = """
                UPDATE nano_task
                SET start_time = :startTime,
                    status     = :status
                WHERE id = :id;
                """;
        var paramMap = Map.of(
                "id", id,
                "startTime", Timestamp.from(Instant.now()),
                "status", NanoTask.RUNNING
        );
        this.jdbcTemplate.update(slim(sql), paramMap);
    }

    public void updateTaskEndStatus(@NotNull Integer id) {
        var sql = """
                UPDATE nano_task
                SET end_time = :endTime,
                    status   = :status
                WHERE id = :id;
                """;
        var paramMap = Map.of(
                "id", id,
                "endTime", Timestamp.from(Instant.now()),
                "status", NanoTask.DONE
        );
        this.jdbcTemplate.update(slim(sql), paramMap);
    }

    public void updateTaskErrorStatus(@NotNull Integer id) {
        var sql = """
                UPDATE nano_task
                SET end_time = :endTime,
                    status   = :status
                WHERE id = :id;
                """;
        var paramMap = Map.of(
                "id", id,
                "endTime", Timestamp.from(Instant.now()),
                "status", NanoTask.ERROR
        );
        this.jdbcTemplate.update(slim(sql), paramMap);
    }
}
