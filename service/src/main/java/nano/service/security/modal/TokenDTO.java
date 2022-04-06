package nano.service.security.modal;

import java.time.Instant;

public record TokenDTO(
         Integer id,
         String name,
         String privilege,
         Instant lastActiveTime,
         Instant creationTime,
         Boolean current
) {
}
