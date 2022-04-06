package nano.support.validation;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.Assert;

/**
 * @see Validated
 * @see Validator
 */
public class ValidateInterceptor implements MethodInterceptor {

    private final BeanFactory beanFactory;

    public ValidateInterceptor(@NotNull BeanFactory beanFactory) {
        Assert.notNull(beanFactory, "beanFactory must be not null");
        this.beanFactory = beanFactory;
    }

    @Override
    public Object invoke(@NotNull MethodInvocation invocation) throws Throwable {
        var method = invocation.getMethod();
        var arguments = invocation.getArguments();
        // validate
        var validated = method.getAnnotation(Validated.class);
        Assert.notNull(validated, "validated annotation is null");
        for (var validatorClass : validated.value()) {
            var validator = this.beanFactory.getBean(validatorClass);
            var message = validator.validate(arguments);
            if (message != null) {
                throw new IllegalArgumentException(message);
            }
        }
        return invocation.proceed();
    }
}
