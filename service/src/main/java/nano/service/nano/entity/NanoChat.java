package nano.service.nano.entity;

import java.util.Objects;

/**
 * Telegram chat
 */
public record NanoChat(
        Long id,
        String username,
        String title,
        String firstname,
        String type
) {
}
