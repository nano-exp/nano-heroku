package nano.service.controller.nano;

import nano.service.domain.object.ObjectService;
import nano.service.nano.model.NanoObject;
import nano.service.security.Authorized;
import nano.service.security.Privilege;
import nano.support.Result;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;

import static nano.support.Sugar.map;

@CrossOrigin(maxAge = 1800)
@RestController
@RequestMapping("/api/object")
public class ObjectController {

    private final ObjectService objectService;

    public ObjectController(ObjectService objectService) {
        this.objectService = objectService;
    }

    @Authorized(privilege = Privilege.NANO_API)
    @GetMapping("/list")
    public ResponseEntity<?> getObjectList() {
        var list = this.objectService.getObjectList();
        return ResponseEntity.ok(Result.of(list));
    }

    @GetMapping("/-/{key}")
    public ResponseEntity<byte[]> getObject(@PathVariable("key") String key) {
        var object = this.objectService.getObject(key);
        var mediaType = MediaType.parseMediaType(object.type());
        var data = object.data();
        return ResponseEntity.ok().contentType(mediaType).body(data);
    }

    @Authorized(privilege = Privilege.NANO_API)
    @PostMapping("/drop")
    public ResponseEntity<?> dropObject(@RequestBody List<String> keyList) {
        this.objectService.batchDropObject(keyList);
        return ResponseEntity.ok(Result.empty());
    }

    @Authorized(privilege = Privilege.NANO_API)
    @PostMapping("/put")
    public ResponseEntity<?> putObject(@RequestParam("file") MultipartFile... fileList) {
        var objectList = map(List.of(fileList), ObjectController::convertToObject);
        var keyList = this.objectService.batchPutObject(objectList);
        return ResponseEntity.ok(Result.of(keyList));
    }

    private static @NotNull NanoObject convertToObject(@NotNull MultipartFile file) {
        try {
            var filename = file.getOriginalFilename();
            var contentType = file.getContentType();
            var size = file.getSize();
            var data = file.getBytes();
            var extension = StringUtils.getFilenameExtension(filename);
            if (ObjectUtils.isEmpty(extension)) {
                extension = "";
            } else {
                extension = "." + extension;
            }
            return new NanoObject(null, filename, contentType, size, data, extension);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
