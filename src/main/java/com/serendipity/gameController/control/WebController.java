package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Beacon;
import com.serendipity.gameController.model.Game;
import com.serendipity.gameController.service.beaconService.BeaconService;
import com.serendipity.gameController.service.exchangeService.ExchangeService;
import com.serendipity.gameController.service.gameService.GameService;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.util.Optional;

@Controller
public class WebController {

    @Autowired
    PlayerServiceImpl playerService;

    @Autowired
    BeaconService beaconService;

    @Autowired
    GameService gameService;

    @Autowired
    ExchangeService exchangeService;

    @GetMapping(value="/")
    @ResponseBody
    public String home() {
        return "Hello World";
    }

//    @GetMapping(value="/")
//    public String home() {
//        return "redirect:/selectPlayer";
//    }

    @PostMapping(value="/initGame")
    public void initGame(@ModelAttribute("startTime") LocalTime startTime) {
//        reset player and exchange tables
        resetTables();
//        get start time and save new game
        Game game = new Game(startTime);
        gameService.saveGame(game);
    }

    @PostMapping(value="/initBeacon")
    public void initBeacon(@ModelAttribute("name") String name,
                           @ModelAttribute("minor") int minor) {
//        receive beacon attributes and construct beacon to add to beacon table
        Beacon beacon = new Beacon(minor, name);
        beaconService.saveBeacon(beacon);
    }

    @PostMapping(value="/delBeacons")
    public void delBeacons() {
        beaconService.deleteBeacons();
    }

    @PostMapping(value="/delBeacon")
    public void delBeacon(@ModelAttribute("beacon_id") long beacon_id) {
        beaconService.deleteBeaconById(beacon_id);
    }

    private void resetTables() {
        playerService.deletePlayers();
//        TODO: Are we having multiple games, if so do we index which game to delete
        gameService.deleteGames();
        exchangeService.deleteExchanges();
    }
//
//    @GetMapping(value="/selectPlayer")
//    public String home(Model model) {
//        List<Player> players = playerService.getAllPlayers();
//        model.addAttribute("players", players);
//        return "selectPlayer";
//    }
//
//    @GetMapping(value="/playerHome/{id}")
//    public String playerHome(@PathVariable("id") Long id, Model model) {
//        Optional<Player> playerOptional = playerService.getPlayer(id);
//        if (playerOptional.isPresent()) {
//            Player player = playerOptional.get();
//            model.addAttribute("player", player);
//            model.addAttribute("informations", informationService.getAllInformationForOwner(player));
//        }
//        else {
//            //TODO Error
//        }
//        return "playerHome";
//    }
//
//    @PostMapping(value="/interact")
//    public String interact(@ModelAttribute("ownerId") Long ownerId,
//                           @ModelAttribute("contactId") Long contactId) {
//        Optional<Player> owner = playerService.getPlayer(ownerId);
//        Optional<Player> contact = playerService.getPlayer(contactId);
//        if (owner.isPresent() && contact.isPresent()) {
//            interact(owner.get(), contact.get());
//        }
//        else {
//            // TODO Error
//        }
//        return "redirect:/playerHome/"+ownerId;
//    }
//
//    @PostMapping(value="/killPlayer")
//    public String kill(@ModelAttribute("ownerId") Long ownerId,
//                       @ModelAttribute("contactId") Long contactId) {
//        Optional<Player> owner = playerService.getPlayer(ownerId);
//        Optional<Player> contact = playerService.getPlayer(contactId);
//        // TODO check they can still kill them
//        if (owner.isPresent() && contact.isPresent()) {
//            //Add 1 to killer's kills
//            playerService.incKills(owner.get());
//            //Killer gets assigned new target from other players
//            newTarget(owner.get(), contact.get());
//            //Killed person gets half their information wiped
//            playerService.halfInformation(contact.get());
//        }
//        return "redirect:/playerHome/"+ownerId;
//    }
//
//    private void interact(Player owner, Player contact) {
//        //Information about the person you are interacting with
//        Optional<Information> primaryInformation = informationService.getInformationForOwnerAndContact(owner, contact);
//        if (primaryInformation.isPresent()) {
//            informationService.incInteractions(primaryInformation.get());
//        }
//        else {
//            // TODO Error
//        }
//        //Information about a random one of their contacts
//        List<Information> secondaryInformation = informationService.getAllInformationForOwner(contact);
//        List<Player> contacts = new ArrayList<>();
//        for (Information information : secondaryInformation) {
//            if ((!information.getContact().equals(owner)) && (information.getInteractions() > 0)) {
//                contacts.add(information.getContact());
//            }
//        }
//        if (!contacts.isEmpty()) {
//            List<Player> potentialInformation = new ArrayList<>();
//            // Create a list of player id's weighted by kills & intel to randomly choose over
//            for (Player player : contacts) {
//                int playerWeight = playerService.getPlayerWeight(player);
//                potentialInformation.add(player);
//                for (int i = 0; i < playerWeight; i++) {
//                    potentialInformation.add(player);
//                }
//            }
//            // Select random player for secondary information
//            Random random = new Random();
//            Player randomContact = potentialInformation.get(random.nextInt(potentialInformation.size()));
//            Optional<Information> randomInfo = informationService.getInformationForOwnerAndContact(owner, randomContact);
//            if (randomInfo.isPresent()) {
//                informationService.incInteractions(randomInfo.get());
//            }
//            else {
//                // TODO Error
//            }
//        }
//    }
//
//    private void newTarget(Player player, Player oldTarget) {
//        //Make list of all player ids, weighted by kills & intel
//        List<Player> targets = new ArrayList<>();
//        List<Player> otherPlayers = playerService.getAllPlayersExcept(player);
//        for (Player p : otherPlayers) {
//            if (!p.equals(oldTarget)) {
//                int playerWeight = playerService.getPlayerWeight(p);
//                targets.add(p);
//                for (int i = 0; i < playerWeight; i++) {
//                    targets.add(p);
//                }
//            }
//        }
//        //Choose random player from that list
//        Random random = new Random();
//        player.setTarget(targets.get(random.nextInt(targets.size())));
//        playerService.savePlayer(player);
//    }

}
