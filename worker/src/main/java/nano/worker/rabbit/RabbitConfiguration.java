package nano.worker.rabbit;

import nano.support.configuration.ConditionalOnRabbit;
import nano.support.mail.MailService;
import nano.worker.consumer.MailConsumer;
import nano.worker.consumer.NanoConsumer;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;

/**
 * Config Rabbit beans
 *
 * @author cbdyzj
 * @since 2020.9.13
 */
@ConditionalOnRabbit
@Configuration(proxyBeanMethods = false)
public class RabbitConfiguration {

    /**
     * nano message consumer
     */
    @Bean
    public NanoConsumer nanoConsumer() {
        return new NanoConsumer();
    }

    /**
     * For consuming sending mail message
     */
    @Bean
    @ConditionalOnProperty("spring.mail.host")
    public MailConsumer mailConsumer() {
        return new MailConsumer();
    }

    @Bean
    @ConditionalOnProperty("spring.mail.host")
    public MailService mailService(JavaMailSender javaMailSender, @Value("${spring.mail.username:}") String fromAddress) {
        var mailService = new MailService();
        mailService.setJavaMailSender(javaMailSender);
        mailService.setFromAddress(fromAddress);
        return mailService;
    }

    /**
     * Convert message to JSON
     *
     * @see org.springframework.amqp.support.converter.SimpleMessageConverter
     */
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

}
