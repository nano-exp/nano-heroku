package nano.service.security;

import nano.support.Json;
import nano.support.Sugar;
import nano.service.security.modal.UserDTO;
import nano.service.nano.entity.NanoToken;
import nano.service.nano.entity.NanoUser;
import nano.service.nano.repository.TokenRepository;
import nano.service.nano.repository.UserRepository;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collection;
import java.util.List;

import static java.util.Collections.emptyList;
import static nano.support.Sugar.map;

@Service
public class UserService {

    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    public UserService(TokenRepository tokenRepository,
                       UserRepository userRepository) {
        this.tokenRepository = tokenRepository;
        this.userRepository = userRepository;
    }

    /**
     * Get the associated user by token
     */
    public @NotNull UserDTO getUserByToken(@NotNull String token) {
        this.tokenRepository.updateLastActiveTime(token, Timestamp.from(Instant.now()));
        var user = this.userRepository.queryUserByToken(token);
        Assert.notNull(user, "Token is not associated with a user");
        return convertToUserDTO(user);
    }

    public void createOrUpdateUser(NanoUser user) {
        this.userRepository.upsertUser(user);
    }

    public void updateUserEmail(Long userId, String email) {
        this.userRepository.updateUserEmail(userId, email);
    }

    public List<NanoToken> queryUserToken(Long userId) {
        return this.tokenRepository.queryUserTokenList(userId);
    }

    public List<String> getUserPrivilege(Long userId) {
        var nanoTokens = this.queryUserToken(userId);
        if (CollectionUtils.isEmpty(nanoTokens)) {
            return emptyList();
        }
        return nanoTokens.stream()
                .map(NanoToken::privilege)
                .map(Json::decodeValueAsList)
                .map(Sugar::mapToString)
                .flatMap(Collection::stream)
                .distinct()
                .toList();
    }

    public List<UserDTO> getUserList() {
        var userList = this.userRepository.queryUserList();
        return map(userList, UserService::convertToUserDTO);
    }

    private static @NotNull UserDTO convertToUserDTO(@NotNull NanoUser user) {
        return new UserDTO(
                String.valueOf(user.id()),
                user.username(),
                user.firstname()
        );
    }
}
