package nano.service.controller.cat;

import nano.service.domain.cat.CatService;
import nano.support.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(maxAge = 1800)
@RestController
@RequestMapping("/api/cat")
public class CatController {

    private final CatService catService;

    public CatController(CatService catService) {
        this.catService = catService;
    }

    @GetMapping("/shot")
    public ResponseEntity<?> getCatShot(@RequestParam(name = "exclude", defaultValue = "") String exclude) {
        var catShot = this.catService.getCatShot(exclude);
        return ResponseEntity.ok(Result.of(catShot));
    }
}
