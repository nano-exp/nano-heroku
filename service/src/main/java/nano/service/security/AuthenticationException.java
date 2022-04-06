package nano.service.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Reply HTTP status 403 and reason
 */
@ResponseStatus(HttpStatus.FORBIDDEN)
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }
}
