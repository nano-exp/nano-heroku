package nano.service.controller.nano;

import nano.service.nano.repository.KeyValueRepository;
import nano.service.security.Authorized;
import nano.service.security.Privilege;
import nano.support.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(maxAge = 1800)
@RestController
@RequestMapping("/api/kv")
public class KeyValueController {

    private final KeyValueRepository keyValueRepository;

    public KeyValueController(KeyValueRepository keyValueRepository) {
        this.keyValueRepository = keyValueRepository;
    }

    @GetMapping("/{key}")
    public ResponseEntity<?> read(@PathVariable("key") String key) {
        var keyValue = this.keyValueRepository.queryKeyValue(key);
        if (keyValue == null) {
            var error = "key %s not found".formatted(key);
            return ResponseEntity.ok(Result.error(error));
        }
        return ResponseEntity.ok(Result.of(keyValue));
    }

    @Authorized(privilege = Privilege.NANO_API)
    @PostMapping("/{key}")
    public ResponseEntity<?> create(@PathVariable("key") String key,
                                    @RequestBody String value) {
        this.keyValueRepository.createKeyValue(key, value);
        return ResponseEntity.ok(Result.empty());
    }

    @Authorized(privilege = Privilege.NANO_API)
    @PutMapping("/{key}")
    public ResponseEntity<?> update(@PathVariable("key") String key,
                                    @RequestBody String value) {
        this.keyValueRepository.updateKeyValue(key, value);
        return ResponseEntity.ok(Result.empty());
    }

    @Authorized(privilege = Privilege.NANO_API)
    @DeleteMapping("/{key}")
    public ResponseEntity<?> delete(@PathVariable("key") String key) {
        this.keyValueRepository.deleteKeyValue(List.of(key));
        return ResponseEntity.ok(Result.empty());
    }
}
