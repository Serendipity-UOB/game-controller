package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Beacon;
import com.serendipity.gameController.model.Game;
import com.serendipity.gameController.model.Zone;
import com.serendipity.gameController.service.beaconService.BeaconServiceImpl;
import com.serendipity.gameController.service.exchangeService.ExchangeServiceImpl;
import com.serendipity.gameController.service.gameService.GameServiceImpl;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import com.serendipity.gameController.service.zoneService.ZoneServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.transaction.Transactional;
import java.time.LocalTime;
import java.util.Iterator;
import java.util.List;

@Controller
public class WebController {

    @Autowired
    PlayerServiceImpl playerService;

    @Autowired
    BeaconServiceImpl beaconService;

    @Autowired
    GameServiceImpl gameService;

    @Autowired
    ExchangeServiceImpl exchangeService;

    @Autowired
    ZoneServiceImpl zoneService;

    @GetMapping(value="/")
    public String home(Model model) {
        model.addAttribute("beacons", beaconService.getAllBeacons());
        model.addAttribute("zones", zoneService.getAllZones());
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
    public String initBeacon(@ModelAttribute("beacon_identifier") String identifier,
                             @ModelAttribute("beacon_major") int major,
                             @ModelAttribute("beacon_minor") int minor,
                             @ModelAttribute("beacon_zone") Zone zone) {
        Beacon beacon = new Beacon(major, minor, identifier, zone);
        beaconService.saveBeacon(beacon);
        return "redirect:/";
    }

    @Transactional
    @PostMapping(value="/delBeacon")
    public String delBeacon(@ModelAttribute("beacon_id") Long id) {
        // TODO: Remove from zone's beacons, save zone
        Beacon beacon = beaconService.getBeaconById(id).get();
        zoneService.removeBeaconFromZone(beacon);
        beaconService.deleteBeaconById(id);
        return "redirect:/";
    }

    @PostMapping(value="/initZone")
    public String initZone(@ModelAttribute("zone_name") String name) {
        Zone zone = new Zone(name);
        zoneService.saveZone(zone);
        return "redirect:/";
    }

    @Transactional
    @PostMapping(value="delZone")
    public String delZone(@ModelAttribute("zone_id") Long id) {
        Zone zone = zoneService.getZoneById(id);
        List<Beacon> beacons = zone.getBeacons();
        for (Iterator<Beacon> it = beacons.iterator(); it.hasNext();) {
            Beacon beacon = it.next();
            it.remove();
            beaconService.deleteBeaconById(beacon.getId());
        }
        zoneService.deleteZone(zone);
        return "redirect:/";
    }

    private void resetTables() {
        exchangeService.deleteAllExchanges();
        playerService.deletePlayers();
//        TODO: Are we having multiple games, if so do we index which game to delete
        gameService.deleteAllGames();
    }

}
