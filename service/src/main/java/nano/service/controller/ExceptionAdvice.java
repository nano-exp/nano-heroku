package nano.service.controller;

import nano.support.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception advice
 *
 * @author cbdyzj
 * @see 2020.9.16
 */
@ControllerAdvice
public class ExceptionAdvice {

    private static final Logger log = LoggerFactory.getLogger(ExceptionAdvice.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleException(Exception ex) {
        var cause = NestedExceptionUtils.getMostSpecificCause(ex);
        traceCause(cause);
        var exClass = cause.getClass();
        var message = cause.getMessage();
        if (message == null) {
            message = exClass.getSimpleName();
        }
        if (log.isDebugEnabled()) {
            log.debug(message, cause);
        }
        var status = HttpStatus.OK;
        if (exClass.isAnnotationPresent(ResponseStatus.class)) {
            var responseStatus = exClass.getAnnotation(ResponseStatus.class);
            status = responseStatus.value();
        }
        return ResponseEntity.status(status).body(Result.error(message));
    }

    private static void traceCause(Throwable cause) {
        if (cause instanceof DataAccessException ex) {
            log.warn(ex.getMessage());
        }
    }
}
