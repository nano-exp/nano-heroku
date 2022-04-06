package nano.service.domain.cat;

import nano.service.domain.cat.model.CatShot;
import nano.service.domain.object.ObjectService;
import nano.service.nano.AppConfig;
import nano.service.nano.model.NanoObject;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static nano.support.MoreContentType.getContentType;
import static org.springframework.util.StringUtils.getFilenameExtension;

@Service
public class CatService {

    private final AppConfig appConfig;
    private final ObjectService objectService;

    public CatService(AppConfig appConfig, CatObjectService catObjectService) {
        this.appConfig = appConfig;
        this.objectService = catObjectService;
    }

    public String createMedia(Resource image) throws IOException {
        var object = createMediaObject(image);
        return this.objectService.putObject(object);
    }

    public CatShot getCatShot(String exclude) {
        var list = this.objectService.getObjectList();
        list = new ArrayList<>(list);
        Collections.shuffle(list);
        var object = list.stream()
                .filter(it -> it.type().startsWith("image") || it.type().startsWith("video"))
                .filter(it -> !it.key().equals(exclude))
                .filter(it -> it.key().startsWith("cat-"))
                .findAny()
                .orElseThrow();
        return CatShot.builder()
                .key(object.key())
                .url(this.appConfig.withNanoApi("/api/object/-/%s".formatted(object.key())))
                .type(object.type())
                .build();
    }

    private static @NotNull NanoObject createMediaObject(@NotNull Resource resource) throws IOException {
        try (var is = resource.getInputStream()) {
            var bytes = is.readAllBytes();
            var filename = resource.getFilename();
            return NanoObject.builder()
                    .name(filename)
                    .data(bytes)
                    .size(bytes.length)
                    .extension(getFilenameExtension(filename))
                    .type(getContentType(filename))
                    .build();
        }
    }
}
