package nano.service.controller.nano;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

@Controller
@RequestMapping("/api/view")
public class ViewController {

    @GetMapping("/template")
    public ModelAndView template() {
        var mav = new ModelAndView("template.html");
        mav.addObject("title", "Template");
        mav.addObject("body", """
                <h1>Template</h1>
                """);
        return mav;
    }

    @GetMapping("/redirect")
    public RedirectView redirect(@RequestParam("url") String url) {
        return new RedirectView(url);
    }
}
