package nano.service.nano.repository;

import nano.service.nano.entity.NanoToken;
import nano.support.jdbc.SimpleJdbcSelect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

import static nano.support.EntityUtils.slim;

@Repository
public class TokenRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TokenRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public @Nullable NanoToken queryToken(@NotNull String token) {
        var select = new SimpleJdbcSelect<>(NanoToken.class)
                .withTableName("nano_token").whereEqual("token").limit(1);
        return select.usesJdbcTemplate(this.jdbcTemplate).queryOne(Map.of("token", token));
    }

    public @Nullable NanoToken queryTokenIfValid(@NotNull String token) {
        var select = new SimpleJdbcSelect<>(NanoToken.class)
                .withTableName("nano_token").whereEqual("token", "status").limit(1);
        var paramMap = Map.of("token", token, "status", NanoToken.VALID);
        return select.usesJdbcTemplate(this.jdbcTemplate).queryOne(paramMap);
    }

    public @NotNull List<NanoToken> queryTokenList(@NotNull List<@NotNull Integer> idList) {
        var select = new SimpleJdbcSelect<>(NanoToken.class)
                .withTableName("nano_token").whereIn("id");
        return select.usesJdbcTemplate(this.jdbcTemplate).query(Map.of("id", idList));
    }

    public @NotNull List<NanoToken> queryAssociatedTokenList(@NotNull String token) {
        var select = new SimpleJdbcSelect<>(NanoToken.class)
                .withTableName("nano_token").whereClause("""
                        WHERE status = 'VALID'
                          AND user_id = (
                            SELECT nt.user_id
                            FROM nano_token nt
                            WHERE nt.status = 'VALID'
                              AND nt.token = :token
                        )
                        """);
        return select.usesJdbcTemplate(this.jdbcTemplate).query(Map.of("token", token));
    }

    public @NotNull List<NanoToken> queryVerifyingToken(@NotNull String username, @NotNull String verificationCode) {
        var select = new SimpleJdbcSelect<>(NanoToken.class)
                .withTableName("nano_token").whereEqual("status");
        var status = NanoToken.verifyingStatus(username, verificationCode);
        return select.usesJdbcTemplate(this.jdbcTemplate).query(Map.of("status", status));
    }

    public @NotNull List<String> queryVerifyingTimeoutToken() {
        var sql = """
                SELECT token
                FROM nano_token
                WHERE status LIKE 'VERIFYING%'
                  AND creation_time + '360 sec' < NOW();
                """;
        var rowMapper = new SingleColumnRowMapper<>(String.class);
        return this.jdbcTemplate.query(slim(sql), rowMapper);
    }

    public void createToken(@NotNull NanoToken token) {
        var sql = """
                INSERT INTO nano_token (token, name, chat_id, user_id, status, privilege,
                                        last_active_time, creation_time)
                VALUES (:token, :name, :chatId, :userId, :status, :privilege::JSONB,
                        :lastActiveTime, :lastActiveTime);
                """;
        var paramSource = new BeanPropertySqlParameterSource(token);
        this.jdbcTemplate.update(slim(sql), paramSource);
    }

    public void updateToken(@NotNull NanoToken token) {
        var sql = """
                UPDATE nano_token
                SET chat_id          = :chatId,
                    user_id          = :userId,
                    status           = :status,
                    privilege        = :privilege::JSONB,
                    last_active_time = :lastActiveTime
                WHERE token = :token;
                """;
        var paramSource = new BeanPropertySqlParameterSource(token);
        this.jdbcTemplate.update(slim(sql), paramSource);
    }

    public void updateLastActiveTime(@NotNull String token, @NotNull Timestamp lastActiveTime) {
        var sql = """
                UPDATE nano_token
                SET last_active_time = :lastActiveTime
                WHERE token = :token;
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("token", token, "lastActiveTime", lastActiveTime));
    }

    public void batchDeleteById(@NotNull List<@NotNull Integer> idList) {
        var sql = """
                DELETE
                FROM nano_token
                WHERE id IN (:idList);
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("idList", idList));
    }

    public void batchDeleteByToken(@NotNull List<@NotNull String> tokenList) {
        var sql = """
                DELETE
                FROM nano_token
                WHERE token IN (:tokenList);
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("tokenList", tokenList));
    }

    public @NotNull List<NanoToken> queryUserTokenList(@NotNull Long userId) {
        var select = new SimpleJdbcSelect<>(NanoToken.class)
                .withTableName("nano_token").whereEqual("user_id").limit(1);
        return select.usesJdbcTemplate(this.jdbcTemplate).query(Map.of("userId", userId));
    }
}
