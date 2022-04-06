package nano.support.mail;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.internet.MimeMessage;

public class MailServiceTests {

    @Test
    public void testSendTextMail() throws Exception {
        // mock
        var mockMailSender = Mockito.mock(JavaMailSender.class);
        var mockMimeMessage = Mockito.mock(MimeMessage.class);
        Mockito.when(mockMailSender.createMimeMessage()).thenReturn(mockMimeMessage);
        // test send
        var mailService = new MailService();
        mailService.setFromAddress("from");
        mailService.setJavaMailSender(mockMailSender);
        var mail = new TextMail("to", "subject", "text");
        mailService.sendTextMail(mail);
        // verify
        Mockito.verify(mockMailSender, Mockito.times(1)).send(mockMimeMessage);
    }
}
