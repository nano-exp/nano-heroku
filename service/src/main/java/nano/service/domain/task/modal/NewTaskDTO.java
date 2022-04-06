package nano.service.domain.task.modal;

import lombok.Builder;

public record NewTaskDTO(
        String name,
        String description,
        String options
) {

    @Builder
    public NewTaskDTO {
    }
}
