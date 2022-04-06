package nano.service.security;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Token code
 */
public abstract class TokenCode {

    public static final String X_TOKEN = "X-Token";
    public static final String TOKEN = "token";

    public static final String X_TICKET = "X-Ticket";
    public static final String TICKET = "ticket";

    public static final String DESENSITIZED_X_TOKEN = "DESENSITIZED_X_TOKEN";

    /**
     * Generating random verification code
     */
    public static @NotNull String generateVerificationCode() {
        var randomInt = ThreadLocalRandom.current().nextInt(1_000_000);
        // left pad with '0'
        return String.format("%6d", randomInt).replaceAll(" ", "0");
    }

    /**
     * Is verification code
     */
    @Contract(value = "null -> false", pure = true)
    public static boolean isVerificationCode(@Nullable String text) {
        return text != null && text.matches("^[0-9]{6}$");
    }

    /**
     * Generating random token
     */
    public static @NotNull String generateUUID() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * Generate token Digest
     */
    @Contract(value = "null -> null", pure = true)
    public static String desensitizeToken(@Nullable String originalToken) {
        if (originalToken == null) {
            return null;
        }
        return DigestUtils.md5DigestAsHex(originalToken.getBytes(StandardCharsets.UTF_8));
    }
}
