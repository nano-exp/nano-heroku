package nano.worker.consumer;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;

/**
 * Handle nano message
 */
public class NanoConsumer {

    private static final Logger log = LoggerFactory.getLogger(NanoConsumer.class);

    @RabbitListener(queuesToDeclare = @Queue("nano"))
    public void consume(@NotNull String message) {
        log.info(message);
    }
}
