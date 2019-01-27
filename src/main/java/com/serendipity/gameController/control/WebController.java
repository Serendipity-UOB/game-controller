package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Beacon;
import com.serendipity.gameController.model.Game;
import com.serendipity.gameController.service.beaconService.BeaconService;
import com.serendipity.gameController.service.exchangeService.ExchangeService;
import com.serendipity.gameController.service.gameService.GameService;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalTime;

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
    public String home(Model model) {
        model.addAttribute("beacons", beaconService.getAllBeacons());
        model.addAttribute("games", gameService.getAllGames());
        return "admin";
    }

    @PostMapping(value="/initGame")
    public String initGame(@ModelAttribute("start_time") String startTime) {
//        reset player and exchange tables
        resetTables();
        LocalTime start = LocalTime.parse(startTime);
//        get start time and save new game
        Game game = new Game(start);
        gameService.saveGame(game);
        return "redirect:/";
    }

    @PostMapping(value="/initBeacon")
    public String initBeacon(@ModelAttribute("beacon_name") String beacon_name,
                           @ModelAttribute("beacon_minor") int beacon_minor) {
//        receive beacon attributes and construct beacon to add to beacon table
        Beacon beacon = new Beacon(beacon_minor, beacon_name);
        beaconService.saveBeacon(beacon);
        return "redirect:/";
    }

    @PostMapping(value="/delBeacons")
    public String delBeacons() {
        beaconService.deleteBeacons();
        return "redirect:/";
    }

    @Transactional
    @PostMapping(value="/delBeacon")
    public String delBeacon(@ModelAttribute("beacon_id") long beacon_id) {
        beaconService.deleteBeaconById(beacon_id);
        return "redirect:/";
    }

    private void resetTables() {
        playerService.deletePlayers();
//        TODO: Are we having multiple games, if so do we index which game to delete
        gameService.deleteGames();
        exchangeService.deleteExchanges();
    }

}
