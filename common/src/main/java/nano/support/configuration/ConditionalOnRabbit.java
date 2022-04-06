package nano.support.configuration;

import org.springframework.context.annotation.Conditional;

import java.lang.annotation.*;

/**
 * On condition if spring.rabbitmq.addresses is not empty
 *
 * @see OnRabbitCondition
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Documented
@Conditional(OnRabbitCondition.class)
public @interface ConditionalOnRabbit {
}
