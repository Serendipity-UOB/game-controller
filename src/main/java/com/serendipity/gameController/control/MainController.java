package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Information;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.service.informationService.InformationService;
import com.serendipity.gameController.service.informationService.InformationServiceImpl;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;


@Controller
public class MainController {

    @Autowired
    PlayerServiceImpl playerService;

    @Autowired
    InformationServiceImpl informationService;

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

    @GetMapping(value="/playerHome/{id}")
    public String playerHome(@PathVariable("id") Long id, Model model) {
        Optional<Player> playerOptional = playerService.getPlayer(id);
        if (playerOptional.isPresent()) {
            Player player = playerOptional.get();
            model.addAttribute("player", player);
            model.addAttribute("informations", informationService.getAllInformationForOwner(player));
        } else {
            //TODO Error
        }
        return "playerHome";
    }

    @GetMapping(value="/initGame")
    public String initGame(){
        init();
        return "redirect:/";
    }

    @PostMapping(value="/interact")
    public String interact(@ModelAttribute("ownerId") Long ownerId,
                           @ModelAttribute("contactId") Long contactId) {
        Player owner = playerService.getPlayer(ownerId).get();
        Player contact = playerService.getPlayer(contactId).get();
        Information information = informationService.getInformationForOwnerAndContact(owner, contact).get();
        informationService.incInteractions(information);
        return "redirect:/playerHome/"+ownerId;
    }

    private void init() {
        //TODO: Shuffle hacker names
        //TODO: Init information maps
        assign();
        initInformation();
    }

    private void initInformation() {
        for (Player player : playerService.getAllPlayers()) {
            for (Player otherPlayer : playerService.getAllPlayersExcept(player)) {
                //Add an information entry
                Information information = new Information(player, otherPlayer, 0);
                informationService.saveInformation(information);
            }
        }
    }

    private void assign(){
        List<Player> players = playerService.getAllPlayers();
        List<Player> unassigned = playerService.getAllPlayers();
        Random random = new Random();
        for (Player player : players) {
            //Get the list of players that are unassigned and not me
            List<Player> chooseFrom = new ArrayList<>();
            for (Player p : unassigned) {
                if (!(p.equals(player))) {
                    chooseFrom.add(p);
                }
            }
            //Check to see if chooseFrom is empty
            if (chooseFrom.isEmpty()) {
                //Swap the last person's target with someone who doesn't have them as a target
                //Try swapping with the first person's target
                if (!(players.get(0).equals(player))) {
                    //Then swap their targets
                    players.get(0).setTarget(player);
                    player.setTarget(players.get(0));

                } else {
                    players.get(1).setTarget(player);
                    player.setTarget(players.get(1));
                }
            } else {
                //Pick random from chooseFrom
                int i = random.nextInt(chooseFrom.size());
                Player target = chooseFrom.get(i);
                player.setTarget(target);
                //Reset unassigned
                unassigned.remove(target);
            }
        }
        //Update the database
        for (Player player : players) {
            playerService.savePlayer(player);
        }

    }

}
