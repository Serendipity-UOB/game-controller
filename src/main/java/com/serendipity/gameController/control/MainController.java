package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.PlayerRepository;
import com.serendipity.gameController.service.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
public class MainController {

    @Autowired
    PlayerServiceImpl playerService;

    @GetMapping(value="/")
    public String home(Model model) {
        assignTargets();
        String name = "Tilly";
        model.addAttribute("name", name);
        return "homepage";
    }


    private void assignTargets(){
        List<Player> ps = playerService.getAllPlayers();
        shufflePlayers(ps);
        for (int i = 0; i < ps.size(); i++) {
            System.out.println(ps.get(i));
        }

    }

    private List<Player> shufflePlayers(List<Player> players) {
        List<Player> copy = players;
        while(!anyEqual(players, copy)) {
            Collections.shuffle(players);
        }
        return players;
    }

    private boolean anyEqual(List<Player> players1, List<Player> players2) {
        boolean anyEqual = false;
        for (int i = 0; i < players1.size(); i++) {
            if (players1.get(i).getId() == players2.get(i).getId()) {
                anyEqual = true;
                break;
            }
        }
        return anyEqual;
    }


}
