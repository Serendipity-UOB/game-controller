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

import java.util.*;


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
        //Information about the person you are interacting with
        Information primaryInformation = informationService.getInformationForOwnerAndContact(owner, contact).get();
        informationService.incInteractions(primaryInformation);
        //Information about a random one of their contacts
        List<Information> secondaryInformation = informationService.getAllInformationForOwner(contact);
        List<Player> contacts = new ArrayList<>();
        for (Information information : secondaryInformation) {
            if ((!information.getContact().equals(owner)) && (information.getInteractions() > 0)) {
                contacts.add(information.getContact());
            }
        }
        if (!contacts.isEmpty()) {
            Random random = new Random();
            Player randomContact = contacts.get(random.nextInt(contacts.size()));
            Information randomInfo = informationService.getInformationForOwnerAndContact(owner, randomContact).get();
            informationService.incInteractions(randomInfo);
        }
        return "redirect:/playerHome/"+ownerId;
    }

    @PostMapping(value="/killPlayer")
    public String kill(@ModelAttribute("ownerId") Long ownerId,
                       @ModelAttribute("contactId") Long contactId) {
        Player owner = playerService.getPlayer(ownerId).get();
        Player contact = playerService.getPlayer(contactId).get();
        //Add 1 to killer's kills
        playerService.incKills(owner);
        //Killer gets assigned new random target from other players
        List<Player> otherPlayers = playerService.getAllPlayersExcept(owner);
        Random random = new Random();
        owner.setTarget(otherPlayers.get(random.nextInt(otherPlayers.size())));
        playerService.savePlayer(owner);
        //Killed person gets half their information wiped
        playerService.halfInformation(contact);
        return "redirect:/playerHome/"+ownerId;
    }

    private void init() {
        playerService.createPlayers();
        playerService.assignTargets();
        informationService.initInformation();
    }

}
