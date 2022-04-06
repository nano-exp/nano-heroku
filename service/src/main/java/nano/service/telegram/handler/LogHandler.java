package nano.service.telegram.handler;

import nano.service.telegram.BotContext;
import nano.support.Json;
import nano.support.Onion;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Write log
 * <p>
 * After {@link ExceptionHandler}
 */
@Component
public class LogHandler implements Onion.Middleware<BotContext> {

    private static final Logger log = LoggerFactory.getLogger(LogHandler.class);

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        var parameters = context.getParameters();
        log.info("parameters: {}", Json.encode(parameters));
        // next
        next.next();
    }
}
