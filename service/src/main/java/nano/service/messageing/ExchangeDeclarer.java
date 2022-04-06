package nano.service.messageing;

import nano.service.ServiceApplication;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import javax.annotation.PostConstruct;
import java.util.stream.Stream;

/**
 * Declare exchanges on rabbit property set
 *
 * @see Exchanges
 * @see ServiceApplication#exchangeDeclarer
 */
public class ExchangeDeclarer {

    private AmqpAdmin amqpAdmin;

    @PostConstruct
    public void declareExchange() {
        Assert.notNull(this.amqpAdmin, "this.amqpAdmin is null");
        Stream.of(Exchanges.NANO, Exchanges.MAIL)
                .map(DirectExchange::new)
                .forEach(this.amqpAdmin::declareExchange);
    }

    @Autowired
    public void setAmqpAdmin(@NotNull AmqpAdmin amqpAdmin) {
        this.amqpAdmin = amqpAdmin;
    }
}
