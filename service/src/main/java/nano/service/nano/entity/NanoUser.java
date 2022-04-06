package nano.service.nano.entity;

/**
 * Telegram user
 */
public record NanoUser(
        Long id,
        String username,
        String firstname,
        String languageCode,
        Boolean isBot,
        String email
) {
}
