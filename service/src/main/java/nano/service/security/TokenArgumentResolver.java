package nano.service.security;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import static nano.service.security.TokenCode.*;
import static org.springframework.web.context.request.RequestAttributes.SCOPE_REQUEST;

/**
 * Resolve token argument
 *
 * @see Token
 * @see TokenCode
 */
@Component
public class TokenArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(@NotNull MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Token.class);
    }

    @Override
    public String resolveArgument(@NotNull MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  @NotNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        var desensitizedToken = (String) webRequest.getAttribute(DESENSITIZED_X_TOKEN, SCOPE_REQUEST);
        if (desensitizedToken == null) {
            var token = webRequest.getHeader(X_TOKEN);
            Assert.notNull(token, "Missing token");
            desensitizedToken = desensitizeToken(token);
        }
        return desensitizedToken;
    }
}
