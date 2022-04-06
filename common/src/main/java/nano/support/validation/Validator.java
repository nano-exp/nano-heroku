package nano.support.validation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @see Validated
 * @see ValidateInterceptor
 */
@FunctionalInterface
public interface Validator {

    @Nullable String validate(Object @NotNull ... args);
}
