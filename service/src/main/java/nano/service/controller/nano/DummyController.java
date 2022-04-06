package nano.service.controller.nano;

import nano.support.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(maxAge = 1800)
@RestController
@RequestMapping("/api/dummy")
public class DummyController {

    @RequestMapping("/sql/query")
    public ResponseEntity<?> dummySqlQuery(@RequestBody String ignore) {
        return ResponseEntity.ok(Result.of(List.of(
                Map.of("id", 1, "name", "Apple"),
                Map.of("id", 2, "name", "Banana"),
                Map.of("id", 3, "name", "Cherry")
        )));
    }
}
