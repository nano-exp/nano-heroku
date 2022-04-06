package nano.service.telegram.handler;

import nano.service.nano.model.Bot;
import nano.service.security.Privilege;
import nano.service.telegram.BotContext;
import nano.support.Onion;
import nano.support.Zx;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.nio.charset.StandardCharsets;

@Component
public class Nano000Handler implements Onion.Middleware<BotContext> {

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        if (Bot.NANO_000.equals(context.getBot().name())) {
            if (!context.getUserPrivilegeList().contains(Privilege.NANO_API)) {
                context.replyMessage("No permission");
                return;
            }
            var text = context.getText();
            if (ObjectUtils.isEmpty(text)) {
                context.replyMessage("Command is empty");
                return;
            }
            var s = Zx.$("bash", "-c", text);
            context.replyMessage(new String(s.join(), StandardCharsets.UTF_8));
        } else {
            next.next();
        }
    }
}
