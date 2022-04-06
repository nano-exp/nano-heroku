package nano.service.controller.mail;

import nano.support.Arguments;
import nano.support.mail.TextMail;
import nano.support.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

@Component
public class SendTextMailValidator implements Validator {

    @Override
    public @Nullable String validate(Object @NotNull ... args) {
        var arguments = new Arguments(args);
        var textMail = arguments.get(0, TextMail.class);
        Assert.notNull(textMail, "textMail is null");
        if (ObjectUtils.isEmpty(textMail.subject())) {
            return "TextMail \"subject\" is empty";
        }
        if (ObjectUtils.isEmpty(textMail.to())) {
            return "TextMail \"to\" is empty";
        }
        if (ObjectUtils.isEmpty(textMail.text())) {
            return "TextMail \"text\" is empty";
        }
        return null;
    }
}
