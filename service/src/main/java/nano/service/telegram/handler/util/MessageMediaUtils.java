package nano.service.telegram.handler.util;

import nano.service.telegram.BotContext;

public abstract class MessageMediaUtils {

    public static String getStickerUrl(BotContext context) {
        var fileId = context.<String>read("$.message.sticker.file_id");
        if (fileId == null) {
            return null;
        }
        return context.getFileUrl(fileId);
    }

    public static String getPhotoUrl(BotContext context) {
        var fileId = context.<String>read("$.message.photo[-1].file_id");
        if (fileId == null) {
            return null;
        }
        return context.getFileUrl(fileId);
    }

    public static String getVideoUrl(BotContext context) {
        var fileId = context.<String>read("$.message.video.file_id");
        if (fileId == null) {
            return null;
        }
        return context.getFileUrl(fileId);
    }

    public static String getDocumentUrl(BotContext context) {
        var fileId = context.<String>read("$.message.document.file_id");
        if (fileId == null) {
            return null;
        }
        return context.getFileUrl(fileId);
    }
}
