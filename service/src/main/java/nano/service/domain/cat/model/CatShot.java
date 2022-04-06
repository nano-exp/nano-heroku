package nano.service.domain.cat.model;

import lombok.Builder;

public record CatShot(
        String key,
        String url,
        String type
) {
    @Builder
    public CatShot {
    }
}
