package nano.service.telegram;

import nano.service.telegram.modal.ChatDTO;
import nano.service.nano.entity.NanoChat;
import nano.service.nano.repository.ChatRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.List;

import static nano.support.Sugar.map;

@Service
public class ChatService {

    private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }

    public List<ChatDTO> getChatList() {
        var chatList = this.chatRepository.queryChatList();
        return map(chatList, ChatService::convertToChatDTO);
    }

    private static ChatDTO convertToChatDTO(@NotNull NanoChat chat) {
        return new ChatDTO(
                String.valueOf(chat.id()),
                chat.username(),
                chat.title(),
                chat.firstname(),
                chat.type()
        );
    }
}
