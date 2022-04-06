package nano.worker.consumer;

import nano.support.Json;
import nano.support.mail.MailService;
import nano.support.mail.TextMail;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;

import javax.mail.MessagingException;

/**
 * Handle mail message
 */
public class MailConsumer {

    private static final Logger log = LoggerFactory.getLogger(MailConsumer.class);

    private MailService mailService;

    @RabbitListener(queuesToDeclare = @Queue("mail.text"))
    public void consume(@NotNull TextMail mail) throws MessagingException {
        log.info("send mail: {}", Json.encode(mail));
        this.mailService.sendTextMail(mail);
    }

    @Autowired
    public void setMailService(@NotNull MailService mailService) {
        this.mailService = mailService;
    }
}
