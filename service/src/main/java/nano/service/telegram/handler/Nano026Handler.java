package nano.service.telegram.handler;

import nano.service.nano.model.Bot;
import nano.service.telegram.BotContext;
import nano.support.LanguageUtils;
import nano.support.Onion;
import nano.service.domain.baidu.TranslationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

/**
 * ZH/EN translation
 */
@Component
public class Nano026Handler implements Onion.Middleware<BotContext> {

    private final TranslationService translationService;

    public Nano026Handler(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        if (Bot.NANO_026.equals(context.getBot().name())) {
            this.translate(context);
        } else {
            next.next();
        }
    }

    private void translate(BotContext context) {
        var text = context.getText();
        if (ObjectUtils.isEmpty(text)) {
            context.sendMessage("The content is empty, please input the text to be translated");
            return;
        }
        String translated;
        if (LanguageUtils.containsChinese(text)) {
            translated = this.translationService.translate(text, "zh", "en");
        } else {
            translated = this.translationService.translate(text, "en", "zh");
        }
        context.replyMessage(translated);
    }
}
