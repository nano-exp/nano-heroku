package nano.service.telegram.handler;

import nano.service.domain.mediawiki.MoeWikiService;
import nano.service.domain.mediawiki.WikipediaService;
import nano.service.nano.model.Bot;
import nano.service.telegram.BotContext;
import nano.support.Onion;
import nano.service.domain.baidu.BaiduWikiService;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

/**
 * Encyclopedia
 */
@Component
public class Nano100Handler implements Onion.Middleware<BotContext> {

    private final MoeWikiService moeWikiService;
    private final WikipediaService wikipediaService;
    private final BaiduWikiService baiduWikiService;

    private final List<Function<String, CompletableFuture<String>>> fetcherList = new ArrayList<>();

    public Nano100Handler(MoeWikiService moeWikiService, WikipediaService wikipediaService, BaiduWikiService baiduWikiService) {
        this.moeWikiService = moeWikiService;
        this.wikipediaService = wikipediaService;
        this.baiduWikiService = baiduWikiService;
        // add fetchers
        this.fetcherList.add(this::fetchWikipedia);
        this.fetcherList.add(this::fetchMoeWiki);
        this.fetcherList.add(this::fetchBaiduEncyclopedia);
    }

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {

        if (Bot.NANO_100.equals(context.getBot().name())) {
            this.fetchAndSendExtract(context);
        } else {
            next.next();
        }
    }

    private void fetchAndSendExtract(BotContext context) {
        var text = context.getText();
        if (ObjectUtils.isEmpty(text)) {
            context.sendMessage("The title is empty, please input title");
            return;
        }
        boolean[] done = {false};
        this.fetcherList.stream()
                .map(fetcher -> fetcher.apply(text))
                .forEachOrdered(future -> {
                    if (done[0]) {
                        future.cancel(false);
                        return;
                    }
                    var extract = future.join();
                    if (extract != null) {
                        done[0] = true;
                        CompletableFuture.runAsync(() -> replyMessageWithoutPreview(context, extract));
                    }
                });
        if (!done[0]) {
            context.replyMessage("nano did not find: " + text);
        }
    }

    private CompletableFuture<String> fetchWikipedia(String title) {
        return CompletableFuture.supplyAsync(() -> {
            for (var language : List.of("zh", "en", "ja")) {
                var extract = this.wikipediaService.fetchWiki(title, language);
                if (extract != null) {
                    return extract;
                }
            }
            return null;
        });
    }

    private CompletableFuture<String> fetchMoeWiki(String title) {
        return CompletableFuture.supplyAsync(() -> this.moeWikiService.fetchWiki(title, "zh"));
    }

    private CompletableFuture<String> fetchBaiduEncyclopedia(String title) {
        return CompletableFuture.supplyAsync(() -> this.baiduWikiService.fetchWiki(title));
    }

    private static void replyMessageWithoutPreview(BotContext context, String text) {
        var payload = Map.of(
                "chat_id", context.getChatId(),
                "reply_to_message_id", context.getMessageId(),
                "disable_web_page_preview", true,
                "text", text
        );
        context.getTelegramService().sendMessage(context.getBot(), payload);
    }
}
