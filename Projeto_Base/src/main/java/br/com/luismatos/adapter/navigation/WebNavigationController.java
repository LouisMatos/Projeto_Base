package br.com.luismatos.adapter.navigation;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WebNavigationController {

    @GetMapping(Routes.Web.INDEX)
    public String index() {
        return "redirect:" + Routes.Web.LOGIN;
    }

    @GetMapping(Routes.Web.LOGIN)
    public String login() {
        return Routes.Views.LOGIN;
    }

    @GetMapping(Routes.Web.PRINCIPAL)
    public String principal() {
        return Routes.Views.PRINCIPAL;
    }
}
