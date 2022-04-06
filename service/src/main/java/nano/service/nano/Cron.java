package nano.service.nano;

import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Date;

@Component
public class Cron implements ApplicationContextAware {

    private static final Logger log = LoggerFactory.getLogger(Cron.class);

    private ApplicationContext context;

    @Scheduled(fixedDelay = 3 * 1000)
    public void polling() {
        var app = this.context.getId();
        var duration = Duration.ofMillis(new Date().getTime() - this.context.getStartupDate());
        var beanCount = this.context.getBeanDefinitionCount();
        log.debug("[{}] started: {} seconds, beans: {}.", app, duration.toSeconds(), beanCount);
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
