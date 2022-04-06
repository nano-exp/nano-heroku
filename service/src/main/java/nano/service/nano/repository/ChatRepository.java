package nano.service.nano.repository;

import nano.service.nano.entity.NanoChat;
import nano.support.jdbc.SimpleJdbcSelect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

import static nano.support.EntityUtils.slim;

@Repository
public class ChatRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ChatRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public @Nullable NanoChat queryChat(@NotNull Long id) {
        var select = new SimpleJdbcSelect<>(NanoChat.class)
                .withTableName("nano_chat").whereEqual("id").limit(1);
        var paramMap = Map.of("id", id);
        return select.usesJdbcTemplate(this.jdbcTemplate).queryOne(paramMap);
    }

    public @NotNull List<NanoChat> queryChatList() {
        var select = new SimpleJdbcSelect<>(NanoChat.class)
                .withTableName("nano_chat");
        return select.usesJdbcTemplate(this.jdbcTemplate).query();
    }

    public void upsertChat(@NotNull NanoChat nanoChat) {
        var sql = """
                INSERT INTO nano_chat (id, username, title, firstname, type)
                VALUES (:id, :username, :title, :firstname, :type)
                ON CONFLICT (id)
                    DO UPDATE SET username  = EXCLUDED.username,
                                  title     = EXCLUDED.title,
                                  firstname = EXCLUDED.firstname,
                                  type      = EXCLUDED.type;
                """;
        var paramSource = new BeanPropertySqlParameterSource(nanoChat);
        this.jdbcTemplate.update(slim(sql), paramSource);
    }
}
