package nano.service.telegram.handler;

import com.github.houbb.nlp.common.segment.impl.CommonSegments;
import com.github.houbb.pinyin.util.PinyinHelper;
import nano.service.nano.model.Bot;
import nano.service.telegram.BotContext;
import nano.support.LanguageUtils;
import nano.support.Onion;
import nano.service.domain.baidu.TranslationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.Objects;

@Component
public class Nano063Handler implements Onion.Middleware<BotContext> {

    private final TranslationService translationService;

    public Nano063Handler(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        if (Bot.NANO_063.equals(context.getBot().name())) {
            this.toPinyin(context);
        } else {
            next.next();
        }
    }

    private void toPinyin(BotContext context) {
        var text = context.getText();
        if (ObjectUtils.isEmpty(text)) {
            context.sendMessage("Input text is empty, please input");
            return;
        }
        String zhText;
        if (LanguageUtils.containsChinese(text)) {
            zhText = text;
        } else if (LanguageUtils.containsRussian(text)) {
            zhText = this.translationService.translate(text, "ru", "zh");
        } else {
            zhText = this.translationService.translate(text, "en", "zh");
        }
        // merge pinyin
        var textSegments = CommonSegments.simple().segment(zhText);
        var pinyinSegments = PinyinHelper.toPinyin(zhText).split(" ");
        var len = Math.min(textSegments.size(), pinyinSegments.length);
        var sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            var _text = textSegments.get(i);
            var _pinyin = pinyinSegments[i];
            sb.append(_text);
            if (!Objects.equals(_text, _pinyin)) {
                sb.append("（").append(_pinyin).append("）");
            }
        }
        context.replyMessage(sb.toString());
    }
}
