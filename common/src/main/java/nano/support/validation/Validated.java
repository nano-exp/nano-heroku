package nano.support.validation;

import java.lang.annotation.*;

/**
 * @see Validator
 * @see ValidateInterceptor
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Validated {

    Class<? extends Validator>[] value() default {};
}
