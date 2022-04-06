package nano.service.controller.telegram;

import nano.service.security.Authorized;
import nano.service.security.Privilege;
import nano.service.telegram.ChatService;
import nano.service.telegram.TelegramService;
import nano.support.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Handle chat requests
 *
 * @see TelegramService
 * @see ChatService
 * @see Authorized
 */
@Authorized(privilege = Privilege.NANO_API)
@CrossOrigin(maxAge = 1800)
@RestController
@RequestMapping("/api/telegram")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @GetMapping("/chat/list")
    public ResponseEntity<?> getChatList() {
        var chatDTOList = this.chatService.getChatList();
        return ResponseEntity.ok(Result.of(chatDTOList));
    }
}
