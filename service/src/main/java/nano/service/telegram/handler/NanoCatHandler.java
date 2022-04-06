package nano.service.telegram.handler;

import nano.service.domain.cat.CatService;
import nano.service.nano.model.Bot;
import nano.service.security.Privilege;
import nano.service.telegram.BotContext;
import nano.service.telegram.handler.util.MessageMediaUtils;
import nano.support.Onion;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * All Cats
 */
@Component
public class NanoCatHandler implements Onion.Middleware<BotContext> {

    private final CatService catService;

    public NanoCatHandler(CatService catService) {
        this.catService = catService;
    }

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        if (Bot.NANO_CAT.equals(context.getBot().name())) {
            this.saveMedia(context);
        } else {
            next.next();
        }
    }

    private void saveMedia(@NotNull BotContext context) throws IOException {
        if (!context.getUserPrivilegeList().contains(Privilege.NANO_API)) {
            context.replyMessage("No permission");
            return;
        }
        var mediaResource = this.fetchMediaResource(context);
        if (mediaResource == null) {
            context.replyMessage("Can't find media resource");
        } else {
            var key = this.catService.createMedia(mediaResource);
            context.replyMessage("Saved: " + key);
        }
    }

    private Resource fetchMediaResource(@NotNull BotContext context) throws IOException {
        var url = MessageMediaUtils.getStickerUrl(context);
        if (url == null) {
            url = MessageMediaUtils.getPhotoUrl(context);
        }
        if (url == null) {
            url = MessageMediaUtils.getVideoUrl(context);
        }
        if (url == null) {
            url =  MessageMediaUtils.getDocumentUrl(context);
        }
        if (url == null) {
            return null;
        }
        return new UrlResource(url);
    }
}
