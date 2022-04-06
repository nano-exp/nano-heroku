package nano.service.controller.telegram;

import nano.service.security.Authorized;
import nano.service.security.Privilege;
import nano.service.security.SecurityService;
import nano.service.telegram.BotHandler;
import nano.service.telegram.TelegramService;
import nano.support.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Handle Telegram requests
 *
 * @see TelegramService
 */
@CrossOrigin(maxAge = 1800)
@RestController
@RequestMapping("/api/telegram")
public class TelegramController {

    private final BotHandler botHandler;
    private final TelegramService telegramService;
    private final SecurityService securityService;

    public TelegramController(BotHandler botHandler,
                              TelegramService telegramService,
                              SecurityService securityService) {
        this.botHandler = botHandler;
        this.telegramService = telegramService;
        this.securityService = securityService;
    }

    @PostMapping("/webhook/{bot}/{key}")
    public ResponseEntity<?> webhook(@PathVariable("bot") String botName, @PathVariable("key") String key,
                                     @RequestBody Map<String, ?> parameterMap) throws Exception {
        // check key
        this.securityService.checkNanoApiKey(key);
        // handle request
        this.botHandler.handleAsync(botName, parameterMap);
        // always return ok
        return ResponseEntity.ok().build();
    }

    @Authorized(privilege = Privilege.NANO_API)
    @PostMapping("/setWebhook")
    public ResponseEntity<?> setWebhook() {
        var result = this.telegramService.setWebhook();
        return ResponseEntity.ok(Result.of(result));
    }
}
