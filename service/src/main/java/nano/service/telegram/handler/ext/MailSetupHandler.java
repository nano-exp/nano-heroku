package nano.service.telegram.handler.ext;

import nano.service.security.Privilege;
import nano.service.security.UserService;
import nano.service.telegram.BotContext;
import nano.support.Onion;
import nano.service.nano.model.Bot;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import static nano.support.mail.MailService.EMAIL;

/**
 * Mail setup handler
 */
@Component
public class MailSetupHandler implements Onion.Middleware<BotContext> {

    private final UserService userService;

    public MailSetupHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        var text = context.getText();
        var bot = context.getBot();
        if (Bot.NANO.equals(bot.name()) && isSetMailCommand(text)) {
            this.trySetMailAddress(context);
        } else {
            next.next();
        }
    }

    private void trySetMailAddress(BotContext context) {
        var user = context.getSession().getUser();
        if (!context.getUserPrivilegeList().contains(Privilege.MAIL)) {
            context.replyMessage("No mail service permission");
            return;
        }
        var mailAddress = getMailAddress(context.getText());
        if (!EMAIL.test(mailAddress)) {
            context.replyMessage("Illegal mail format");
            return;
        }
        this.userService.updateUserEmail(user.id(), mailAddress);
        context.replyMessage("Succeed");
    }

    private static boolean isSetMailCommand(String text) {
        int len = "/setmail ".length();
        if (ObjectUtils.isEmpty(text) || text.length() < len) {
            return false;
        }
        return "/setmail ".equalsIgnoreCase(text.substring(0, len));
    }

    private static String getMailAddress(String text) {
        return text.substring("/setmail ".length());
    }
}
