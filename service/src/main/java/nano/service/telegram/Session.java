package nano.service.telegram;

import nano.service.nano.entity.NanoChat;
import nano.service.nano.entity.NanoToken;
import nano.service.nano.entity.NanoUser;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Session {

    private final Map<String, Object> attributes = new HashMap<>();

    private final NanoChat chat;
    private final NanoUser user;
    private final NanoToken token;

    public Session(@NotNull NanoChat chat, @NotNull NanoUser user, @NotNull NanoToken token) {
        this.chat = chat;
        this.user = user;
        this.token = token;
    }

    public <T> @Nullable T getAttribute(@NotNull String key, @NotNull Class<T> clazz) {
        var attribute = this.getAttribute(key);
        return clazz.cast(attribute);
    }

    public @Nullable Object getAttribute(@NotNull String key) {
        return this.attributes.get(key);
    }

    public void putAttribute(@NotNull String key, String value) {
        this.attributes.put(key, value);
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public @NotNull NanoChat getChat() {
        return this.chat;
    }

    public @NotNull NanoUser getUser() {
        return this.user;
    }

    public @NotNull NanoToken getToken() {
        return this.token;
    }
}
