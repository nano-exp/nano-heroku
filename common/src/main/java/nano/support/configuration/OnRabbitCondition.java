package nano.support.configuration;

import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.util.StringUtils;

/**
 * On condition if spring.rabbitmq.addresses is not empty
 *
 * @author cbdyzj
 * @since 2020.9.12
 */
public class OnRabbitCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {
        var property = context.getEnvironment().getProperty("spring.rabbitmq.addresses");
        if (StringUtils.hasText(property)) {
            return ConditionOutcome.match();
        }
        return ConditionOutcome.noMatch("spring.rabbitmq.addresses is empty");

    }
}
