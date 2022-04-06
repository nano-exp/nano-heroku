package nano.service.nano.entity;

import nano.service.security.Privilege;

import java.sql.Timestamp;
import java.util.Objects;

/**
 * Nano token
 */
public record NanoToken(
        Integer id,
        String token,
        String name,
        Number chatId,
        Number userId,
        String status,
        String privilege,
        Timestamp lastActiveTime,
        Timestamp creationTime
) {

    public static final String VALID = "VALID";
    public static final String INVALID = "INVALID";
    public static final String VERIFYING = "VERIFYING";

    public static final String VERIFYING_TIMEOUT = "VERIFYING_TIMEOUT";
    public static final String VERIFIED = "VERIFIED";

    public static String verifyingStatus(String username, String verificationCode) {
        return "%s:%s:%s".formatted(VERIFYING, username, verificationCode);
    }
}
