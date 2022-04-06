package nano.service.controller.security;

import nano.service.security.Authorized;
import nano.service.security.Privilege;
import nano.service.security.Token;
import nano.service.security.UserService;
import nano.support.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(maxAge = 1800)
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Authorized(privilege = Privilege.BASIC)
    @GetMapping("/user")
    public ResponseEntity<?> getUser(@Token String token) {
        var userDTO = this.userService.getUserByToken(token);
        return ResponseEntity.ok(Result.of(userDTO));
    }

    @Authorized(privilege = Privilege.NANO_API)
    @GetMapping("/list")
    public ResponseEntity<?> getUserList() {
        var userDTOList = this.userService.getUserList();
        return ResponseEntity.ok(Result.of(userDTOList));
    }
}
