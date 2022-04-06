package nano.service.nano.repository;

import nano.service.nano.entity.NanoUser;
import nano.support.jdbc.SimpleJdbcSelect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.DataClassRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

import static nano.support.EntityUtils.slim;

@Repository
public class UserRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public UserRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public @Nullable NanoUser queryUser(@NotNull Long id) {
        var select = new SimpleJdbcSelect<>(NanoUser.class)
                .withTableName("nano_user").whereEqual("id").limit(1);
        var paramMap = Map.of("id", id);
        return select.usesJdbcTemplate(this.jdbcTemplate).queryOne(paramMap);
    }

    public @NotNull List<NanoUser> queryUserList() {
        var select = new SimpleJdbcSelect<>(NanoUser.class)
                .withTableName("nano_user");
        return select.usesJdbcTemplate(this.jdbcTemplate).query();
    }

    public void upsertUser(@NotNull NanoUser nanoUser) {
        var sql = """
                INSERT INTO nano_user (id, username, firstname, is_bot, language_code, email)
                VALUES (:id, :username, :firstname, :isBot, :languageCode, :email)
                ON CONFLICT (id)
                    DO UPDATE SET username      = EXCLUDED.username,
                                  firstname     = EXCLUDED.firstname,
                                  is_bot        = EXCLUDED.is_bot,
                                  language_code = EXCLUDED.language_code,
                                  email         = EXCLUDED.email;
                """;
        var paramSource = new BeanPropertySqlParameterSource(nanoUser);
        this.jdbcTemplate.update(slim(sql), paramSource);
    }

    public @Nullable NanoUser queryUserByToken(@NotNull String token) {
        var sql = """
                SELECT nu.id            AS id,
                       nu.firstname     AS firstname,
                       nu.username      AS username,
                       nu.language_code AS language_code,
                       nu.is_bot        AS is_bot,
                       nu.email         AS email
                FROM nano_token nt
                         JOIN nano_user nu on nt.user_id = nu.id
                WHERE nt.status = 'VALID'
                  AND nt.token = :token;
                """;
        var rowMapper = new DataClassRowMapper<>(NanoUser.class);
        var paramMap = Map.of("token", token);
        var userList = this.jdbcTemplate.query(slim(sql), paramMap, rowMapper);
        return CollectionUtils.firstElement(userList);
    }

    public void updateUserEmail(Long id, String email) {
        var sql = """
                UPDATE nano_user
                SET email = :email
                WHERE id = :id;
                """;
        this.jdbcTemplate.update(slim(sql), Map.of("id", id, "email", email));
    }
}
