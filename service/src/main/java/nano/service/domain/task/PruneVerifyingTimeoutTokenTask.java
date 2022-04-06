package nano.service.domain.task;

import nano.service.nano.Cron;
import nano.service.nano.repository.TokenRepository;
import nano.support.Task;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Map;

@Component("pruneVerifyingTimeoutTokenTask")
public class PruneVerifyingTimeoutTokenTask implements Task {

    private static final Logger log = LoggerFactory.getLogger(Cron.class);

    private final TokenRepository tokenRepository;

    public PruneVerifyingTimeoutTokenTask(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    public void execute(@NotNull Map<String, ?> options) {
        var count = this.pruneVerifyingTimeoutToken();
        log.info("Prune verifying timeout token: {}", count);
    }

    /**
     * Prune verifying timeout token
     *
     * @return prune count
     */
    public int pruneVerifyingTimeoutToken() {
        var tokenList = this.tokenRepository.queryVerifyingTimeoutToken();
        int count = 0;
        if (!CollectionUtils.isEmpty(tokenList)) {
            count = tokenList.size();
            this.tokenRepository.batchDeleteByToken(tokenList);
        }
        return count;
    }

}
