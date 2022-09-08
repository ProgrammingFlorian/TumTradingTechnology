package com.lkws.ttt.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller that serves the frontend.
 */
@Controller
public class FrontendController {

    /**
     * Serves all non-api traffic the frontend,
     * to allow use of urls other than "/" (like "/login", "/dashboard", etc.).<br>
     * To achieve this, matches following requests
     * (with word = [any letters or -], to exclude file endings (.) to still serve static content):
     * <ul>
     * <li>"/"</li>
     * <li>"/[word]"</li>
     * <li>"/[word not exactly "api"]/[anything - including nothing]/[word]"</li>
     * </ul>
     * and serves /index.html<br>
     * <a href="https://stackoverflow.com/questions/47689971/how-to-work-with-react-routers-and-spring-boot-controller">Source</a>
     **/
    @RequestMapping(value = {"/", "/{x:[\\w\\-]+}", "/{x:^(?!api$).*$}/**/{y:[\\w\\-]+}"})
    public String getIndex() {
        return "/index.html";
    }

}