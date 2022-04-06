package nano.service.nano;

import nano.service.nano.repository.PostgresRepository;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Map;

@Service
public class PostgresService {

    private static final Logger log = LoggerFactory.getLogger(PostgresService.class);

    private final PostgresRepository queryRepository;

    private final JdbcTemplate jdbcTemplate;


    public PostgresService(PostgresRepository queryRepository, JdbcTemplate jdbcTemplate) {
        this.queryRepository = queryRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    public @NotNull String getPostgresVersion() {
        try {
            var mapper = new SingleColumnRowMapper<String>();
            var version = this.jdbcTemplate.query("SELECT VERSION();", mapper);
            return String.join(", ", version);
        } catch (Exception ex) {
            log.warn(ex.getMessage());
            return "?";
        }
    }

    public List<Map<String, Object>> query(@NotNull String sql) {
        Assert.hasText(sql, "SQL must be not empty");
        return this.queryRepository.query(sql);
    }

    public List<Map<String, Object>> queryTable(@NotNull String name) {
        Assert.hasText(name, "Table name must be not empty");
        return this.queryRepository.queryTable(name);
    }
}
