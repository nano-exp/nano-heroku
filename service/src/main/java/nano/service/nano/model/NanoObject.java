package nano.service.nano.model;

import lombok.Builder;

public record NanoObject(
        String key,
        String name,
        String type,
        Number size,
        byte[] data,
        String extension
) {

    @Builder
    public NanoObject {
    }

}
