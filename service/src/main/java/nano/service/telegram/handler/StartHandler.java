package nano.service.telegram.handler;

import nano.service.nano.model.Bot;
import nano.service.telegram.BotContext;
import nano.support.Onion;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Start
 * <p>
 * After {@link AuthenticationHandler}
 */
@Component
public class StartHandler implements Onion.Middleware<BotContext> {

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        var commands = context.getCommandList();
        var bot = context.getBot();
        if (commands.contains("/start") || commands.contains("/start@" + bot.username())) {
            replyStartMessage(context);
        } else {
            next.next();
        }
    }

    private static void replyStartMessage(BotContext context) {
        var bot = context.getBot();
        switch (bot.name()) {
            case Bot.NANO -> replyNanoStart(context);
            case Bot.NANO_026 -> context.replyMessage("ZH/EN translation");
            case Bot.NANO_100 -> context.replyMessage("Encyclopedia");
            case Bot.NANO_233 -> context.replyMessage("Get sticker");
            case Bot.NANO_262 -> context.replyMessage("Evaluate JavaScript");
        }
    }

    private static void replyNanoStart(BotContext context) {
        var text = """
                <b>nano</b>
                                
                <a href="https://t.me/nano_026_bot">nano-026</a> - ZH/EN translation
                <a href="https://t.me/nano_100_bot">nano-100</a> - Encyclopedia
                <a href="https://t.me/nano_233_bot">nano-233</a> - Get sticker
                <a href="https://t.me/nano_262_bot">nano-262</a> - Evaluate JavaScript
                """;
        var payload = Map.of(
                "chat_id", context.getChatId(),
                "reply_to_message_id", context.getMessageId(),
                "parse_mode", "HTML",
                "disable_web_page_preview", true,
                "text", text
        );
        context.getTelegramService().sendMessage(context.getBot(), payload);
    }
}
