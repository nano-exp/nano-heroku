package nano.support.templating;

import org.springframework.web.servlet.View;

public abstract class Views {

    public static View getView404() {
        return (model, request, response) -> {
            //
            response.sendError(404, "404 Not Found");
        };
    }
}
