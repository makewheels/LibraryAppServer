package com.eg.libraryappserver;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @time 2020-01-22 10:38
 */
@Controller
public class RootController {

    @RequestMapping("/")
    public String index(){
        return "index.html";
    }
}
