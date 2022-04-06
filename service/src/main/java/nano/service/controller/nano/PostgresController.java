package nano.service.controller.nano;

import nano.service.nano.PostgresService;
import nano.service.security.Authorized;
import nano.service.security.Privilege;
import nano.support.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(maxAge = 1800)
@Authorized(privilege = Privilege.NANO_API)
@RestController
@RequestMapping("/api/postgres")
public class PostgresController {

    private final PostgresService postgresService;

    public PostgresController(PostgresService postgresService) {
        this.postgresService = postgresService;
    }

    @PostMapping("/query")
    public ResponseEntity<?> postgresQuery(@RequestBody String sql) {
        var data = this.postgresService.query(sql);
        return ResponseEntity.ok(Result.of(data));
    }

    @GetMapping("/table")
    public ResponseEntity<?> queryTable(@RequestParam("name") String name) {
        var data = this.postgresService.queryTable(name);
        return ResponseEntity.ok(Result.of(data));
    }
}
