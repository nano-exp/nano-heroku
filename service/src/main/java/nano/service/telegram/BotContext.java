package nano.service.telegram;

import nano.support.Json;
import nano.service.nano.model.Bot;
import nano.service.util.JsonPathModule;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyList;
import static nano.support.Sugar.filter;

public class BotContext {

    private final Bot bot;

    private final Map<String, ?> parameters;
    private final JsonPathModule.ReadContext parameterContext;

    private Session session;
    private TelegramService telegramService;

    public BotContext(@NotNull Bot bot, @NotNull Map<String, ?> parameters) {
        this.bot = bot;
        this.parameters = parameters;
        this.parameterContext = JsonPathModule.parse(parameters);
    }

    public Number getChatId() {
        return this.read("$.message.chat.id");
    }

    public Number getMessageFromId() {
        return this.read("$.message.from.id");
    }

    public String getText() {
        return this.read("$.message.text");
    }

    public String getChatType() {
        return this.read("$.message.chat.type");
    }

    public Number getMessageId() {
        return this.read("$.message.message_id");
    }

    public List<String> getUserPrivilegeList() {
        var privilege = this.getSession().getToken().privilege();
        return Json.decodeValueAsList(privilege)
                .stream()
                .map(String::valueOf)
                .distinct()
                .toList();
    }

    public Instant getDate() {
        var timestamp = this.<Number>read("$.message.date");
        Assert.notNull(timestamp, "timestamp is null");
        return Instant.ofEpochSecond(timestamp.longValue());
    }


    public List<String> getCommandList() {
        var entities = this.<List<Map<String, Object>>>read("$.message.entities");
        var commandEntities = filter(entities, it -> "bot_command".equals(it.get("type")));
        if (CollectionUtils.isEmpty(commandEntities)) {
            return emptyList();
        }
        var text = this.getText();
        var commands = new ArrayList<String>();
        for (var entity : commandEntities) {
            var offset = convertToInteger(entity.get("offset"));
            var length = convertToInteger(entity.get("length"));
            if (offset == null || length == null) {
                continue;
            }
            var command = text.substring(offset, offset + length);
            commands.add(command);
        }
        return commands;
    }

    /**
     * @param jsonPath JSON path
     * @param <T>      result type
     * @return null if absent
     */
    public <T> T read(String jsonPath) {
        try {
            return this.parameterContext.read(jsonPath);
        } catch (Exception ex) {
            return null;
        }
    }

    public void sendMessage(String text) {
        var payload = Map.of(
                "chat_id", this.getChatId(),
                "text", text
        );
        this.getTelegramService().sendMessage(this.getBot(), payload);
    }

    public void replyMessage(String text) {
        var payload = Map.of(
                "chat_id", this.getChatId(),
                "reply_to_message_id", this.getMessageId(),
                "text", text
        );
        this.getTelegramService().sendMessage(this.getBot(), payload);
    }

    public void replyPhoto(Resource photo) {
        this.getTelegramService().replyPhoto(this.getBot(), this.getChatId(), this.getMessageId(), photo);
    }

    public String getFileUrl(@NotNull String fileId) {
        var result = this.getTelegramService().getFile(this.getBot(), fileId);
        var filePath = JsonPathModule.<String>read(result, "$.result.file_path");
        Assert.notNull(filePath, "filePath is null");
        return TelegramService.getFileUrl(this.getBot(), filePath);
    }

    private static Integer convertToInteger(Object o) {
        if (o instanceof Integer) {
            return (Integer) o;
        }
        if (o instanceof Number) {
            return ((Number) o).intValue();
        }
        return null;
    }

    public Map<String, ?> getParameters() {
        return parameters;
    }

    public Session getSession() {
        return session;
    }

    public void setSession(Session session) {
        this.session = session;
    }

    public TelegramService getTelegramService() {
        Assert.notNull(this.telegramService, "this.telegramService is null");
        return this.telegramService;
    }

    public void setTelegramService(TelegramService telegramService) {
        this.telegramService = telegramService;
    }

    public Bot getBot() {
        return this.bot;
    }

}
