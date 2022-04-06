package nano.service.domain.object;

import nano.service.nano.entity.KeyValue;
import nano.service.nano.model.NanoObject;
import nano.service.nano.repository.KeyValueRepository;
import nano.service.nano.repository.NanoBlobRepository;
import nano.service.security.TokenCode;
import nano.support.Json;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.util.*;

import static java.util.Collections.synchronizedMap;
import static java.util.Objects.requireNonNull;
import static nano.support.Sugar.map;

@Service
public class ObjectService {

    private static final int DEFAULT_CACHE_LIMIT = 256;

    private final Map<String, NanoObject> objectCache = synchronizedMap(new LinkedHashMap<>(DEFAULT_CACHE_LIMIT, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, NanoObject> eldest) {
            return this.size() > DEFAULT_CACHE_LIMIT;
        }
    });

    private final KeyValueRepository keyValueRepository;
    private final NanoBlobRepository nanoBlobRepository;

    private final String domain;

    public ObjectService(KeyValueRepository keyValueRepository,
                         NanoBlobRepository nanoBlobRepository,
                         @Value("") @NotNull String domain) {
        this.keyValueRepository = keyValueRepository;
        this.nanoBlobRepository = nanoBlobRepository;
        this.domain = domain;
    }

    @Transactional
    public String putObject(@NotNull NanoObject object) {
        var key = this.generateKey();
        var objectKey = "object:%s".formatted(key);
        var objectValue = Map.of(
                "key", requireNonNull(key, "key must not be null"),
                "name", requireNonNull(object.name(), "name must not be null"),
                "size", requireNonNull(object.size(), "size must not be null"),
                "extension", requireNonNull(object.extension(), "extension must not be null"),
                "type", requireNonNull(object.type(), "type must not be null")
        );
        this.keyValueRepository.createKeyValue(objectKey, Json.encode(objectValue));
        this.nanoBlobRepository.upsertBlob(objectKey, Base64.getEncoder().encodeToString(object.data()));
        return key;
    }

    @Transactional
    public List<String> batchPutObject(@NotNull List<@NotNull NanoObject> objectList) {
        if (CollectionUtils.isEmpty(objectList)) {
            return Collections.emptyList();
        }
        return objectList.stream().map(this::putObject).toList();
    }

    @Transactional
    public void batchDropObject(@NotNull List<@NotNull String> keyList) {
        if (CollectionUtils.isEmpty(keyList)) {
            return;
        }
        var internalKeyList = map(keyList, "object:%s"::formatted);
        this.keyValueRepository.deleteKeyValue(internalKeyList);
        this.nanoBlobRepository.deleteBlob(internalKeyList);
    }

    public @NotNull NanoObject getObject(@NotNull String key) {
        return this.objectCache.computeIfAbsent(key, (_key) -> {
            var objectKey = "object:%s".formatted(key);
            var keyValue = this.keyValueRepository.queryKeyValue(objectKey);
            Assert.notNull(keyValue, "object is not exist, key: " + objectKey);
            // Get data
            var nanoBlob = this.nanoBlobRepository.queryBlob(objectKey);
            var encodedData = nanoBlob.blob();
            var data = Base64.getDecoder().decode(encodedData);
            return mapToNanoObject(keyValue, data);
        });
    }

    public @NotNull List<@NotNull NanoObject> getObjectList() {
        var pattern = this.getObjectKeyPattern();
        var keyList = this.keyValueRepository.queryListByPattern(pattern);
        return map(keyList, it -> mapToNanoObject(it, null));
    }

    private @NotNull String getObjectKeyPattern() {
        var domain = this.domain;
        return domain.isBlank() ? "^object:" : "^object:%s-".formatted(domain);
    }

    private String generateKey() {
        return this.domain.isBlank() ? TokenCode.generateUUID() : "%s-%s".formatted(this.domain, TokenCode.generateUUID());
    }

    private static NanoObject mapToNanoObject(@NotNull KeyValue keyValue, byte[] data) {
        var objectJson = keyValue.value();
        var objectMap = Json.decodeValueAsMap(objectJson);
        return NanoObject.builder()
                .key(String.valueOf(objectMap.get("key")))
                .name(String.valueOf(objectMap.get("name")))
                .type(String.valueOf(objectMap.get("type")))
                .size((Number) objectMap.get("size"))
                .data(data)
                .extension(String.valueOf(objectMap.get("extension")))
                .build();
    }
}

