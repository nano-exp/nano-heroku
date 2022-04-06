package nano.service.security;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

import static nano.service.security.TokenCode.*;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final SecurityService securityService;

    public AuthenticationInterceptor(SecurityService securityService) {
        this.securityService = securityService;
    }

    @Override
    public boolean preHandle(@NotNull HttpServletRequest request,
                             @NotNull HttpServletResponse response,
                             @NotNull Object handler) {
        if (!(handler instanceof HandlerMethod handlerMethod)) {
            return true;
        }
        var authorized = handlerMethod.getMethodAnnotation(Authorized.class);
        if (authorized == null) {
            authorized = handlerMethod.getBeanType().getAnnotation(Authorized.class);
        }
        if (authorized == null) {
            return true;
        }
        var checkerList = new ArrayList<Runnable>();
        if (authorized.privilege().length > 0) {
            checkerList.add(this.tokenChecker(request, List.of(authorized.privilege())));
        }
        if (authorized.ticket().length > 0) {
            checkerList.add(this.ticketChecker(request, List.of(authorized.ticket())));
        }
        if (checkerList.isEmpty()) {
            return true;
        }
        var errors = new ArrayList<String>();
        for (var checker : checkerList) {
            try {
                checker.run();
            } catch (Exception ex) {
                errors.add(ex.getMessage());
            }
        }
        if (errors.size() == checkerList.size()) {
            throw new AuthenticationException(String.join(", ", errors));
        }
        return true;
    }

    private Runnable tokenChecker(@NotNull HttpServletRequest request, List<String> privilegeList) {
        return () -> {
            var token = getToken(request);
            var desensitizedToken = desensitizeToken(token);
            this.securityService.checkTokenPrivilege(desensitizedToken, privilegeList);
            request.setAttribute(DESENSITIZED_X_TOKEN, desensitizedToken);
        };
    }

    private Runnable ticketChecker(@NotNull HttpServletRequest request, List<String> ticketNameList) {
        return () -> this.securityService.checkTicketPermission(getTicket(request), ticketNameList);
    }

    private static @Nullable String getToken(@NotNull HttpServletRequest request) {
        var token = request.getHeader(X_TOKEN);
        if (token == null) {
            token = request.getParameter(TOKEN);
        }
        return token;
    }

    private static @Nullable String getTicket(@NotNull HttpServletRequest request) {
        var ticket = request.getHeader(X_TICKET);
        if (ticket == null) {
            ticket = request.getParameter(TICKET);
        }
        return ticket;
    }
}
