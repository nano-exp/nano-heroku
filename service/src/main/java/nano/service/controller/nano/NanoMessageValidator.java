package nano.service.controller.nano;

import nano.support.Arguments;
import nano.support.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * @see NanoController#message
 */
@Component
public class NanoMessageValidator implements Validator {

    @Override
    public @Nullable String validate(Object @NotNull ... args) {
        var arguments = new Arguments(args);
        var m = arguments.get(0, String.class);
        if (ObjectUtils.isEmpty(m)) {
            return "Illegal message body";
        }
        return null;
    }
}
