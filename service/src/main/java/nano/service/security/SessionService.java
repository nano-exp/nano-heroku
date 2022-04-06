package nano.service.security;

import nano.service.nano.entity.NanoChat;
import nano.service.nano.entity.NanoToken;
import nano.service.nano.entity.NanoUser;
import nano.service.telegram.Session;
import nano.service.nano.repository.ChatRepository;
import nano.service.nano.repository.TokenRepository;
import nano.service.nano.repository.UserRepository;
import nano.support.Json;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

@Service
public class SessionService {

    private final ChatRepository chatRepository;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    public SessionService(ChatRepository chatRepository, UserRepository userRepository, TokenRepository tokenRepository) {
        this.chatRepository = chatRepository;
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    public @NotNull Session getSession(@NotNull NanoChat chat, @NotNull NanoUser user) {
        var refreshedChat = this.refreshChat(chat);
        var refreshedUser = this.refreshUser(user);
        var refreshedToken = this.refreshToken(user.id(), chat.id());
        return new Session(refreshedChat, refreshedUser, refreshedToken);
    }

    private NanoChat refreshChat(@NotNull NanoChat chat) {
        var exist = this.chatRepository.queryChat(chat.id());
        if (!Objects.equals(chat, exist)) {
            this.chatRepository.upsertChat(chat);
        }
        return exist;
    }

    private @NotNull NanoUser refreshUser(@NotNull NanoUser user) {
        var exist = this.userRepository.queryUser(user.id());
        if (exist == null) {
            this.userRepository.upsertUser(user);
            return user;
        }
        // copy email
        var refreshedUser = new NanoUser(
                user.id(),
                user.username(),
                user.firstname(),
                user.languageCode(),
                user.isBot(),
                exist.email()
        );
        if (!Objects.equals(refreshedUser, exist)) {
            this.userRepository.upsertUser(refreshedUser);
        }
        return refreshedUser;
    }

    private @NotNull NanoToken refreshToken(@NotNull Long userId, @NotNull Long chatId) {
        var tokenKey = "%s-%s".formatted(userId, chatId);
        var token = this.tokenRepository.queryToken(tokenKey);
        var now = Timestamp.from(Instant.now());
        if (token == null) {
            var newToken = new NanoToken(
                    null,
                    tokenKey,
                    "Telegram",
                    chatId,
                    userId,
                    NanoToken.VALID,
                    Json.encode(List.of(Privilege.BASIC)),
                    now,
                    now
            );
            this.tokenRepository.createToken(newToken);
            return newToken;
        }
        this.tokenRepository.updateLastActiveTime(tokenKey, now);
        return token;
    }
}
