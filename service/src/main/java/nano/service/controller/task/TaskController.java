package nano.service.controller.task;

import nano.service.domain.task.TaskService;
import nano.service.security.*;
import nano.service.domain.task.modal.NewTaskDTO;
import nano.support.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(maxAge = 1800)
@RestController
@Authorized(privilege = Privilege.NANO_API, ticket = Ticket.TASK_API)
@RequestMapping("/api/task")
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    public TaskController(TaskService taskService, UserService userService) {
        this.taskService = taskService;
        this.userService = userService;
    }

    @PostMapping("/start")
    public ResponseEntity<?> startTask(@RequestBody NewTaskDTO newTask, @Token String token) {
        var user = userService.getUserByToken(token);
        this.taskService.startExecuteTask(newTask, user);
        return ResponseEntity.ok(Result.empty());
    }
}
