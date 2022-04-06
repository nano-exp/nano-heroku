package nano.support.mail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.Assert;

import javax.mail.MessagingException;
import java.util.function.Predicate;

import static java.util.regex.Pattern.compile;

/**
 * Mail Service
 */
public class MailService {

    /**
     * @see <a href="https://www.rfc-editor.org/rfc/rfc5322.txt">Internet Message Format</a>
     */
    public static final Predicate<String> EMAIL = compile("^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$").asPredicate();

    private static final Logger log = LoggerFactory.getLogger(MailService.class);

    private String fromAddress;

    private JavaMailSender javaMailSender;

    /**
     * Send text mail
     */
    public void sendTextMail(TextMail mail) throws MessagingException {
        Assert.hasText(this.fromAddress, "this.fromAddress is empty");
        Assert.notNull(this.javaMailSender, "this.javaMailSender is null");
        var mailSender = this.javaMailSender;
        // create mail message
        var message = mailSender.createMimeMessage();
        var helper = new MimeMessageHelper(message);
        helper.setFrom(this.fromAddress);
        helper.setTo(mail.to());
        helper.setSubject(mail.subject());
        helper.setText(mail.text());
        mailSender.send(message);
    }

    public void setJavaMailSender(JavaMailSender javaMailSender) {
        log.info("Mail sender set: {}", javaMailSender);
        this.javaMailSender = javaMailSender;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }
}

