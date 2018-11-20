package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Player;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @GetMapping(value="/")
    public String home(Model model) {
        String name = "Tilly";
        model.addAttribute("name", name);
//        return "index.html";
        return "temp";
    }

    public List<Player> initGame(){
        //Randomly assign real names, hacker names, and target
        return new ArrayList<>();
    }


}
