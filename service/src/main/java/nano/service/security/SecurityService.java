package nano.service.security;

import nano.service.security.modal.TokenDTO;
import nano.service.nano.AppConfig;
import nano.service.nano.entity.NanoToken;
import nano.service.nano.entity.NanoUser;
import nano.service.nano.repository.TokenRepository;
import nano.service.util.JsonPathModule;
import nano.service.util.UserAgentParserModule;
import nano.support.Json;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static nano.service.security.TokenCode.generateUUID;
import static nano.service.security.TokenCode.generateVerificationCode;
import static nano.support.Sugar.*;

/**
 * Security and token service
 *
 * @author cbdyzj
 * @since 2020.8.18
 */
@Service
public class SecurityService {

    private static final Logger log = LoggerFactory.getLogger(SecurityService.class);

    private final AppConfig appConfig;

    private final TokenRepository tokenRepository;

    private final Environment env;

    public SecurityService(AppConfig appConfig, TokenRepository tokenRepository, Environment env) {
        this.appConfig = appConfig;
        this.tokenRepository = tokenRepository;
        this.env = env;
    }

    /**
     * Check nano API Key
     */
    public void checkNanoApiKey(@Nullable String key) {
        if (ObjectUtils.isEmpty(key)) {
            throw new AuthenticationException("Missing API key");
        }
        var apiToken = this.appConfig.nanoApiKey();
        if (!Objects.equals(key, apiToken)) {
            throw new AuthenticationException("Illegal API key");
        }
    }

    /**
     * Check token privilege
     */
    public void checkTokenPrivilege(@Nullable String token, @NotNull List<@NotNull String> privilegeList) {
        if (CollectionUtils.isEmpty(privilegeList)) {
            return;
        }
        authState(StringUtils.hasText(token), "Missing token");
        var nanoToken = this.tokenRepository.queryToken(token);
        authState(nanoToken != null, "Illegal token");
        authState(NanoToken.VALID.equals(nanoToken.status()), "Invalid token");
        var tokenPrivileges = mapToString(Json.decodeValueAsList(nanoToken.privilege()));
        var exists = privilegeList.stream().anyMatch(tokenPrivileges::contains);
        authState(exists, "Insufficient token privilege");
    }

    /**
     * Delete token and log out
     */
    public void deleteTheToken(@NotNull String token) {
        Assert.hasText(token, "Illegal token");
        this.tokenRepository.batchDeleteByToken(List.of(token));
    }

    /**
     * Delete token, token management
     */
    public void deleteSpecificToken(@NotNull String token, @NotNull List<@NotNull Integer> idList) {
        var nanoTokenList = this.tokenRepository.queryTokenList(idList);
        if (CollectionUtils.isEmpty(nanoTokenList)) {
            return;
        }
        var currentToken = this.tokenRepository.queryToken(token);
        Assert.notNull(currentToken, "Illegal token");
        Assert.state(every(nanoTokenList, it -> Objects.equals(currentToken.userId(), it.userId())), "Abnormal operation permission");
        this.tokenRepository.batchDeleteById(map(nanoTokenList, NanoToken::id));
    }

    /**
     * Get the token list by token
     */
    public List<TokenDTO> getAssociatedTokenList(String token) {
        Assert.hasText(token, "Illegal token");
        var nanoTokenList = this.tokenRepository.queryAssociatedTokenList(token);
        return map(nanoTokenList, it -> new TokenDTO(
                it.id(),
                it.name(),
                it.privilege(),
                it.lastActiveTime().toInstant(),
                it.creationTime().toInstant(),
                Objects.equals(token, it.token())
        ));
    }

    /**
     * Create the token in validation
     * Do not save the original token, save the desensitized token
     */
    public @NotNull Map<String, String> createVerifyingToken(@NotNull String username, String ua) {
        var originalToken = generateUUID();
        var tokenKey = TokenCode.desensitizeToken(originalToken);
        var name = this.parseUserAgent(ua);
        var verificationCode = generateVerificationCode();
        var status = NanoToken.verifyingStatus(username, verificationCode);
        var now = Timestamp.from(Instant.now());
        var privilege = Json.encode(List.of(Privilege.BASIC));
        var token = new NanoToken(
                null,
                tokenKey,
                name,
                null,
                null,
                status,
                privilege,
                now,
                now
        );
        this.tokenRepository.createToken(token);
        return Map.of("token", originalToken, "verificationCode", verificationCode);
    }

    /**
     * Check token verification status
     */
    public @NotNull Map<String, String> getTokenVerification(@NotNull String token) {
        var nanoToken = this.tokenRepository.queryToken(token);
        Assert.state(nanoToken != null, "Token not found");
        var status = nanoToken.status();
        Assert.hasText(status, "Token status requires not empty");
        var result = new HashMap<String, String>();
        switch (status) {
            case NanoToken.VALID -> result.put("verifying", "done");
            case NanoToken.INVALID -> throw new IllegalStateException("Token is invalid");
            default -> {
                // verifying
                if (status.startsWith(NanoToken.VERIFYING)) {
                    if (verifyingTimeout(nanoToken)) {
                        result.put("verifying", "timeout");
                    } else {
                        result.put("verifying", "pending");
                    }
                } else {
                    throw new IllegalStateException("Illegal token status");
                }
            }
        }
        return result;
    }

    /**
     * Verify token
     */
    public Map<NanoToken, String> verifyToken(NanoUser user, NanoToken telegramToken, String verificationCode) {
        var nanoTokenList = this.tokenRepository.queryVerifyingToken(user.username(), verificationCode);
        var result = new HashMap<NanoToken, String>();
        var now = Timestamp.from(Instant.now());
        forEach(nanoTokenList, it -> {
            var timeout = verifyingTimeout(it);
            var token = new NanoToken(
                    it.id(),
                    it.token(),
                    it.name(),
                    it.chatId(),
                    user.id(),
                    timeout ? NanoToken.INVALID : NanoToken.VALID,
                    telegramToken.privilege(),
                    now,
                    it.creationTime()
            );
            if (!timeout) {
                this.tokenRepository.updateToken(token);
            }
            result.put(token, timeout ? NanoToken.VERIFYING_TIMEOUT : NanoToken.VERIFIED);
        });
        return result;
    }

    /**
     * Resolve user agent
     */
    private @NotNull String parseUserAgent(@Nullable String ua) {
        try {
            Assert.hasText(ua, "Illegal user agent");
            var client = UserAgentParserModule.parseToString(ua);
            var context = JsonPathModule.parse(client);
            var userAgentFamily = context.<String>read("$.user_agent.family");
            var osFamily = context.<String>read("$.os.family");
            return "Website, %s on %s".formatted(userAgentFamily, osFamily);
        } catch (Exception ex) {
            if (log.isDebugEnabled()) {
                log.debug(ex.getMessage(), ex);
            }
            return "Unknown";
        }
    }

    /**
     * Check ticket permission
     */
    public void checkTicketPermission(@Nullable String ticket, @NotNull List<@NotNull String> ticketNameList) {
        if (CollectionUtils.isEmpty(ticketNameList)) {
            return;
        }
        var ticketValid = ticketNameList.stream().map(this.env::getProperty).anyMatch(it -> Objects.equals(ticket, it));
        authState(ticketValid, "Insufficient ticket permission");
    }

    /**
     * Verification timeout, 5 minutes timeout
     */
    private static boolean verifyingTimeout(NanoToken nanoToken) {
        var creationTime = nanoToken.creationTime();
        var now = Timestamp.from(Instant.now().minusSeconds(300));
        return now.after(creationTime);
    }

    public static void authState(boolean expression, @Nls String message) {
        if (!expression) {
            throw new AuthenticationException(message);
        }
    }
}
