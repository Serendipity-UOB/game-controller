package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.service.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    PlayerServiceImpl playerService;

    @GetMapping(value="/")
    public String home(Model model) {
        List<Player> players = playerService.getAllPlayers();
        model.addAttribute("players", players);
        return "selectPlayer";
    }

    @PostMapping(value="/selectPlayer")
    public String selectPlayer(@ModelAttribute("selectedPlayerId") Long id, Model model) {
        model.addAttribute("player", playerService.getPlayer(id));
        return "redirect:/playerHome";
    }

    @GetMapping(value="/playerHome")
    public String playerHome(@ModelAttribute("player") Player player, Model model) {
        model.addAttribute("player", player);
        return "playerHome";
    }

    public List<Player> initGame(){
        //Randomly assign real names, hacker names, and target
        return new ArrayList<>();
    }


}
