package nano.service.security;

import java.lang.annotation.*;

/**
 * Authentication with nano token
 *
 * @author cbdyzj
 * @see SecurityService
 * @see AuthenticationInterceptor
 * @see Privilege
 * @see Ticket
 * @since 2020.9.20
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Authorized {

    String[] privilege() default {};

    String[] ticket() default {};
}
