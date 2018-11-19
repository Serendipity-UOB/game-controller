package com.serendipity.gameController.control;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MainController {

    @GetMapping(value="/")
    @ResponseBody
    public String home() {
        return "Hello World!";
    }


}
