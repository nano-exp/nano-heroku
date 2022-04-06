package nano.service.controller.mail;

import nano.service.messageing.Exchanges;
import nano.service.security.AuthenticationInterceptor;
import nano.service.security.Authorized;
import nano.service.security.Privilege;
import nano.support.mail.TextMail;
import nano.support.validation.Validated;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Send mail
 *
 * @see AuthenticationInterceptor
 */
@CrossOrigin(maxAge = 1800)
@RestController
@RequestMapping("/api/mail")
public class MailController {

    private final RabbitMessagingTemplate rabbitMessagingTemplate;

    public MailController(RabbitMessagingTemplate rabbitMessagingTemplate) {
        this.rabbitMessagingTemplate = rabbitMessagingTemplate;
    }

    @Validated(SendTextMailValidator.class)
    @Authorized(privilege = Privilege.NANO_API)
    @PostMapping("/sendTextMail")
    public ResponseEntity<?> sendTextMail(@RequestBody TextMail mail) {
        this.rabbitMessagingTemplate.convertAndSend(Exchanges.MAIL, "text", mail);
        return ResponseEntity.ok("OK");
    }
}
