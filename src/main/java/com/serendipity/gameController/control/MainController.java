package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.service.PlayerService;
import com.serendipity.gameController.service.PlayerServiceImpl;
import org.aspectj.apache.bcel.util.Play;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.*;

@Controller
public class MainController {

    @Autowired
    PlayerServiceImpl playerService;

    @GetMapping(value="/")
    public String home() {
        return "redirect:/selectPlayer";
    }

    @GetMapping(value="/selectPlayer")
    public String home(Model model) {
        List<Player> players = playerService.getAllPlayers();
        model.addAttribute("players", players);
        return "selectPlayer";
    }

    @GetMapping(value="/initGame")
    public String initGame(){
        //TODO: Shuffle hacker names
        //TODO: Assign targets
        //TODO: Init information maps
        //Redirect to select player page
        return "redirect:/";
    }

    @GetMapping(value="/playerHome/{id}")
    public String playerHome(@PathVariable("id") Long id, Model model) {
        Optional<Player> playerOptional = playerService.getPlayer(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            model.addAttribute("player", player);
            String targetHackerName = "";
//            String targetHackerName = player.getTarget().getHackerName();
            model.addAttribute("targetHackerName", targetHackerName);
        }
        return "playerHome";
    }




}
