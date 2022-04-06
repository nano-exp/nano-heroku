package nano.service.controller.nano;

import nano.service.messageing.Exchanges;
import nano.service.nano.NodeJSService;
import nano.service.security.Authorized;
import nano.service.security.Privilege;
import nano.support.Result;
import nano.support.Zx;
import nano.support.validation.Validated;
import nano.service.nano.NanoService;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StopWatch;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;

@CrossOrigin(maxAge = 1800)
@RestController
@RequestMapping("/api/nano")
public class NanoController {

    private final RabbitMessagingTemplate messagingTemplate;

    private final NanoService nanoService;
    private final NodeJSService nodeJSService;

    public NanoController(RabbitMessagingTemplate messagingTemplate,
                          NanoService nanoService,
                          NodeJSService nodeJSService) {
        this.messagingTemplate = messagingTemplate;
        this.nanoService = nanoService;
        this.nodeJSService = nodeJSService;
    }

    @GetMapping(path = "/ping", produces = "text/plain")
    public ResponseEntity<?> ping() {
        return ResponseEntity.ok("pong\n");
    }

    @GetMapping("/system")
    public ResponseEntity<?> system() {
        var nano = this.nanoService.system();
        return ResponseEntity.ok(nano);
    }

    @GetMapping("/beans")
    public ResponseEntity<?> beans(@RequestParam(name = "q", required = false) String q) {
        var beans = this.nanoService.getBeanDefinitionNames(q);
        return ResponseEntity.ok(beans);
    }

    @Validated(NanoMessageValidator.class)
    @PostMapping("/message")
    public ResponseEntity<?> message(@RequestParam("m") String m) {
        this.messagingTemplate.convertAndSend(Exchanges.NANO, "nano", m);
        return ResponseEntity.ok(Result.of("OK"));
    }

    @Authorized(privilege = Privilege.NANO_API)
    @PostMapping("/$")
    public ResponseEntity<?> $(@RequestBody String[] command) {
        return ResponseEntity.ok(new String(Zx.$(command).join(), StandardCharsets.UTF_8));
    }

    @GetMapping("/node/random")
    public ResponseEntity<?> nodeRandom(@RequestParam(name = "q", defaultValue = "1") Integer q) {
        var sw = new StopWatch();
        sw.start();
        var random = Stream.generate(this.nodeJSService::nodeRandom).limit(q).map(bytesToString()).toList();
        sw.stop();
        var taskTimeMillis = sw.getLastTaskTimeMillis();
        var time = Duration.ofMillis(taskTimeMillis);
        return ResponseEntity.ok(Map.of("time", time, "random", random));
    }


    private static Function<byte[], String> bytesToString() {
        return ba -> new String(ba, StandardCharsets.UTF_8);
    }
}
