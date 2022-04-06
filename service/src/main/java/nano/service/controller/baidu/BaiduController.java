package nano.service.controller.baidu;

import nano.service.security.Authorized;
import nano.service.security.Ticket;
import nano.support.Result;
import nano.service.domain.baidu.TranslationService;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@CrossOrigin(maxAge = 1800)
@RestController
@RequestMapping("/api/baidu")
public class BaiduController {

    private final TranslationService translationService;

    public BaiduController(TranslationService translationService) {
        this.translationService = translationService;
    }

    @Authorized(ticket = Ticket.BAIDU_TRANSLATION)
    @PostMapping("/translate")
    public ResponseEntity<?> translate(@NotNull @RequestBody Map<String, String> payload) {
        var input = payload.get("input");
        var from = payload.get("from");
        var to = payload.get("to");
        var translated = this.translationService.translate(input, from, to);
        return ResponseEntity.ok(Result.of(translated));
    }
}
