package nano.service.security;

import java.lang.annotation.*;

/**
 * Desensitized token parameter
 *
 * @see TokenCode
 * @see TokenArgumentResolver
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Token {
}
