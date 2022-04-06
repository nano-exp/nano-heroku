package nano.service.telegram;

import nano.service.nano.AppConfig;
import nano.service.telegram.handler.*;
import nano.service.telegram.handler.ext.MailHandler;
import nano.service.telegram.handler.ext.MailSetupHandler;
import nano.support.Onion;
import nano.support.Onion.Middleware;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.Map;

@Component
public class BotHandler implements ApplicationContextAware {

    private final Onion<BotContext> onion = new Onion<>();

    private ApplicationContext context;

    @PostConstruct
    public void init() {
        this.useMiddleware(ExceptionHandler.class);
        this.useMiddleware(LogHandler.class);
        this.useMiddleware(SessionInitializeHandler.class);
        this.useMiddleware(AuthenticationHandler.class);
        this.useMiddleware(StartHandler.class);
        this.useMiddleware(Nano000Handler.class);
        this.useMiddleware(Nano026Handler.class);
        this.useMiddleware(Nano063Handler.class);
        this.useMiddleware(Nano100Handler.class);
        this.useMiddleware(Nano233Handler.class);
        this.useMiddleware(Nano262Handler.class);
        this.useMiddleware(NanoCatHandler.class);
        this.useMiddleware(VerificationHandler.class);
        this.useMiddleware(MailSetupHandler.class);
        this.useMiddleware(MailHandler.class);
    }

    private void useMiddleware(Class<? extends Middleware<BotContext>> clazz) {
        this.onion.use(this.context.getBean(clazz));
    }

    @Async
    public void handleAsync(@NotNull String botName, Map<String, ?> parameters) throws Exception {
        this.internalHandle(this.buildContext(botName, parameters));
    }

    public void handle(@NotNull String botName, Map<String, ?> parameters) throws Exception {
        this.internalHandle(this.buildContext(botName, parameters));
    }

    /**
     * handle context
     */
    private void internalHandle(@NotNull BotContext context) throws Exception {
        this.onion.handle(context);
    }

    /**
     * build context
     */
    private @NotNull BotContext buildContext(@NotNull String botName, Map<String, ?> parameters) {
        var ctx = this.context;
        var bot = ctx.getBean(AppConfig.class).getBot(botName);
        Assert.notNull(bot, "No matching Bot found");
        var context = new BotContext(bot, parameters);
        // build context
        var telegramService = ctx.getBean(TelegramService.class);
        context.setTelegramService(telegramService);
        return context;
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
