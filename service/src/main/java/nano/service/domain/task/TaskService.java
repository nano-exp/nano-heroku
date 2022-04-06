package nano.service.domain.task;

import nano.service.domain.task.modal.NewTaskDTO;
import nano.service.nano.entity.NanoTask;
import nano.service.nano.entity.NanoUser;
import nano.service.nano.repository.TaskRepository;
import nano.service.security.modal.UserDTO;
import nano.support.Json;
import nano.support.Task;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

@Service
public class TaskService implements ApplicationContextAware {

    private final TaskRepository taskRepository;

    private ApplicationContext context;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void startExecuteTask(NewTaskDTO newTask, UserDTO user) {
        var id = this.createTask(newTask, user);
        this.executeTask(id);
    }

    public List<NanoTask> queryTaskList() {
        return this.taskRepository.queryTaskList();
    }

    private Integer createTask(NewTaskDTO newTask, UserDTO user) {
        var nanoTask = NanoTask.builder()
                .name(newTask.name())
                .description(newTask.description())
                .options(newTask.options())
                .creationOwner(user.username())
                .status(NanoTask.CREATED)
                .creationTime(Timestamp.from(Instant.now()))
                .build();
        return this.taskRepository.createTask(nanoTask);
    }

    private void executeTask(Integer id) {
        var nanoTask = this.taskRepository.queryTask(id);
        Assert.notNull(nanoTask, "Task must be not null");
        Assert.notNull(nanoTask.name(), "Task name must be not null");
        Assert.notNull(nanoTask.options(), "Task options must be not null");
        var task = this.context.getBean(nanoTask.name(), Task.class);
        try {
            this.taskRepository.updateTaskStartStatus(id);
            task.execute(Json.decodeValueAsMap(nanoTask.options()));
            this.taskRepository.updateTaskEndStatus(id);
        } catch (Exception ex) {
            this.taskRepository.updateTaskErrorStatus(id);
            throw ex;
        }
    }

    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) {
        this.context = applicationContext;
    }
}
