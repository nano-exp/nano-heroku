package nano.service;

import nano.service.messageing.ExchangeDeclarer;
import nano.service.nano.AppConfig;
import nano.service.nano.model.Bot;
import nano.service.security.AuthenticationInterceptor;
import nano.service.security.Authorized;
import nano.service.security.Token;
import nano.service.security.TokenArgumentResolver;
import nano.support.Json;
import nano.support.configuration.ConditionalOnRabbit;
import nano.support.mail.MailService;
import nano.support.templating.SugarViewResolver;
import nano.support.validation.ValidateInterceptor;
import nano.support.validation.Validated;
import nano.support.validation.Validator;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.annotation.AnnotationMatchingPointcut;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service Application
 *
 * @author cbdyzj
 * @since 2020.8.16
 */
@EnableAsync
@EnableScheduling
@SpringBootApplication(proxyBeanMethods = false)
public class ServiceApplication implements ApplicationContextAware, WebMvcConfigurer {

    private ApplicationContext context;

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

    /**
     * App config
     */
    @Bean
    public AppConfig appConfig(@Qualifier("configVars") Map<String, ?> configVars) {
        var appConfig = AppConfig.builder()
                .nanoApi(configVars.get("nano-heroku").toString())
                .nanoApiKey(configVars.get("nano-heroku-key").toString())
                .baiduTranslationAppId(configVars.get("baidu-translation-app-id").toString())
                .baiduTranslationSecretKey(configVars.get("baidu-translation-secret-key").toString())
                .bots(new HashMap<>())
                .build();
        if (configVars.get("bots") instanceof Map<?, ?> bots) {
            bots.forEach((key, value) -> appConfig.bots().put(key.toString(), Json.convertValue(value, Bot.class)));
        }
        return appConfig;
    }

    /**
     * config vars
     */
    @Bean
    @ConfigurationProperties("nano")
    public Map<String, ?> configVars() {
        return new HashMap<>();
    }

    /**
     * Templating
     *
     * @see nano.support.Sugar#render
     */
    @Bean
    public SugarViewResolver sugarViewResolver() {
        var resolver = new SugarViewResolver();
        resolver.setPrefix("classpath:/templates/");
        return resolver;
    }

    /**
     * Validating  advisor
     *
     * @see Validated
     * @see Validator
     * @see ValidateInterceptor
     */
    @Bean
    public DefaultPointcutAdvisor validatePointcutAdvisor() {
        // advisor
        var advisor = new DefaultPointcutAdvisor();
        advisor.setPointcut(AnnotationMatchingPointcut.forMethodAnnotation(Validated.class));
        advisor.setAdvice(new ValidateInterceptor(this.context));
        return advisor;
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
     * Declare exchanges on rabbit property set
     */
    @Bean
    @ConditionalOnRabbit
    public ExchangeDeclarer exchangeDeclarer() {
        return new ExchangeDeclarer();
    }

    /**
     * Convert message to JSON
     */
    @Bean
    @ConditionalOnRabbit
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * Add token argument resolver
     *
     * @see Token
     * @see TokenArgumentResolver
     */
    @Override
    public void addArgumentResolvers(@NotNull List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(this.context.getBean(TokenArgumentResolver.class));
    }

    /**
     * Authentication interceptor for API
     *
     * @see Authorized
     */
    @Override
    public void addInterceptors(@NotNull InterceptorRegistry registry) {
        registry.addInterceptor(this.context.getBean(AuthenticationInterceptor.class)).addPathPatterns("/api/**");
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        this.context = applicationContext;
    }


}
