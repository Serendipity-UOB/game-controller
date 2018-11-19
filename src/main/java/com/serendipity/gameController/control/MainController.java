package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Player;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @GetMapping(value="/")
    @ResponseBody
    public String home() {
        return "Hello World!";
    }

    public List<Player> initGame(){
        //Randomly assign real names, hacker names, and target
        return new ArrayList<>();
    }


}
