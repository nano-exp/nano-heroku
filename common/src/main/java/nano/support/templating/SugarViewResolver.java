package nano.support.templating;

import nano.support.Sugar;
import org.jetbrains.annotations.NotNull;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * @author cbdyzj
 * @see Sugar#render
 * @since 2020.9.22
 */
public class SugarViewResolver extends AbstractTemplateViewResolver {

    private String charset = "utf8";

    public SugarViewResolver() {
        this.setViewClass(this.requiredViewClass());
    }

    @NotNull
    @Override
    protected Class<?> requiredViewClass() {
        return SugarView.class;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    @NotNull
    @Override
    protected AbstractUrlBasedView buildView(@NotNull String viewName) throws Exception {
        var view = (SugarView) super.buildView(viewName);
        view.setCharset(this.charset);
        return view;
    }
}
