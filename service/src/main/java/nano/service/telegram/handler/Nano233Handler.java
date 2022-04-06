package nano.service.telegram.handler;

import nano.service.nano.model.Bot;
import nano.service.telegram.BotContext;
import nano.service.telegram.handler.util.MessageMediaUtils;
import nano.support.Onion;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Get sticker
 */
@Component
public class Nano233Handler implements Onion.Middleware<BotContext> {

    @Override
    public void via(@NotNull BotContext context, Onion.@NotNull Next next) throws Exception {
        if (Bot.NANO_233.equals(context.getBot().name())) {
            this.getSticker(context);
        } else {
            next.next();
        }
    }

    /**
     * convert sticker to jpeg, then reply
     */
    private void getSticker(BotContext context) throws Exception {
        var webpUrl = MessageMediaUtils.getStickerUrl(context);
        if (webpUrl == null) {
            context.sendMessage("The sticker missing");
            return;
        }
        var pngFilePath = convertWebpToJpeg(webpUrl);
        if (pngFilePath == null) {
            context.sendMessage("The sticker is not supported");
            return;
        }
        context.replyPhoto(new FileSystemResource(pngFilePath));
    }

    private static @Nullable Path convertWebpToJpeg(@NotNull String webpUrl) throws Exception {
        var bufferedImage = ImageIO.read(new URL(webpUrl));
        if (bufferedImage == null) {
            return null;
        }
        var tempFilePath = Files.createTempFile("convert_webp_to_png", "tmp");
        var tempFile = tempFilePath.toFile();
        tempFile.deleteOnExit();
        ImageIO.write(bufferedImage, "png", tempFile);
        return tempFilePath;
    }

}
