package nano.service.controller.hls;

import nano.service.domain.hls.HlsService;
import nano.support.Result;
import nano.support.templating.Views;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.RedirectView;

import java.util.Map;

@CrossOrigin(maxAge = 1800)
@Controller
@RequestMapping("/api/hls")
public class HlsController {

    private final HlsService hlsService;

    public HlsController(HlsService hlsService) {
        this.hlsService = hlsService;
    }

    @GetMapping({"/{name}", "/{name}.m3u8"})
    public View redirectToHlsUrl(@PathVariable("name") String name) {
        var url = this.hlsService.getHlsUrl(name);
        return url != null ? new RedirectView(url) : Views.getView404();
    }

    @ResponseBody
    @GetMapping({"/list"})
    public ResponseEntity<?> getHlsList() {
        var hlsList = this.hlsService.getChannels()
                .stream()
                .map((it) -> Map.of(
                        "name", it,
                        "pathname", "/api/hls/%s.m3u8".formatted(it)
                ))
                .toList();
        return ResponseEntity.ok(Result.of(hlsList));
    }
}
