package nano.service.telegram.handler;

import nano.service.nano.model.Bot;
import nano.service.domain.scripting.Scripting;
import nano.service.telegram.BotContext;
import nano.support.Onion;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * Evaluate JavaScript
 */
@Component
public class Nano262Handler implements Onion.Middleware<BotContext> {

    private final Scripting scripting;

    public Nano262Handler(Scripting scripting) {
        this.scripting = scripting;
    }

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        if (Bot.NANO_262.equals(context.getBot().name())) {
            this.evalScript(context);
        } else {
            next.next();
        }
    }

    private void evalScript(BotContext context) {
        var text = context.getText();
        if (ObjectUtils.isEmpty(text)) {
            context.sendMessage("The script is empty");
            return;
        }
        var result = this.scripting.eval(text);
        if (ObjectUtils.isEmpty(result)) {
            return;
        }
        // Text of the message to be sent, 1-4096 characters after entities parsing
        if (result.length() > 4096) {
            context.sendMessage("Evaluated result is too long");
            return;
        }
        context.replyMessage(result);
    }
}
