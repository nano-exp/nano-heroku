package nano.service.telegram.handler;

import nano.service.telegram.BotContext;
import nano.support.Onion;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Handling exception
 * <p>
 * Before all middleware
 */
@Component
public class ExceptionHandler implements Onion.Middleware<BotContext> {

    private static final Logger log = LoggerFactory.getLogger(ExceptionHandler.class);

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) {
        try {
            next.next();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            trySendMessage(context, "nano fault: " + ex.getMessage());
        }
    }

    /**
     * Send message to terminal client if possible
     */
    private static void trySendMessage(BotContext context, String text) {
        if (context.getChatId() == null) {
            return;
        }
        try {
            context.sendMessage(text);
        } catch (Exception ex) {
            log.warn(ex.getMessage());
        }
    }
}
