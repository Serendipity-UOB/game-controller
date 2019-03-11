package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Beacon;
import com.serendipity.gameController.model.Game;
import com.serendipity.gameController.model.Zone;
import com.serendipity.gameController.service.beaconService.BeaconServiceImpl;
import com.serendipity.gameController.service.evidenceService.EvidenceServiceImpl;
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

    @Autowired
    EvidenceServiceImpl evidenceService;

    @GetMapping(value="/")
    public String home(Model model) {
        model.addAttribute("beacons", beaconService.getAllBeacons());
        model.addAttribute("zones", zoneService.getAllZones());
        model.addAttribute("games", gameService.getAllGames());
        return "admin";
    }

    @PostMapping(value="/setupTestGame")
    public String setupTestGame() {
        resetTables();
        Zone zoneA = new Zone("Zone A");
        Zone zoneB = new Zone("Zone B");
        Zone zoneC = new Zone("Zone C");
        zoneService.saveZone(zoneA);
        zoneService.saveZone(zoneB);
        zoneService.saveZone(zoneC);
        Beacon beaconA1 = new Beacon(1, 1, "Beacon A1", zoneA);
        Beacon beaconA2 = new Beacon(1, 2, "Beacon A2", zoneA);
        Beacon beaconB1 = new Beacon(2, 1, "Beacon B1", zoneB);
        Beacon beaconB2 = new Beacon(2, 2, "Beacon B2", zoneB);
        Beacon beaconC1 = new Beacon(3, 1, "Beacon C1", zoneC);
        Beacon beaconC2 = new Beacon(3, 2, "Beacon C2", zoneC);
        beaconService.saveBeacon(beaconA1);
        beaconService.saveBeacon(beaconA2);
        beaconService.saveBeacon(beaconB1);
        beaconService.saveBeacon(beaconB2);
        beaconService.saveBeacon(beaconC1);
        beaconService.saveBeacon(beaconC2);
        Game game = new Game(LocalTime.now().plusMinutes(1));
        gameService.saveGame(game);
        return "redirect:/";
    }

    @PostMapping(value="/resetAll")
    public String resetAll() {
        resetTables();
        return "redirect:/";
    }

    @PostMapping(value="/initGame")
    public String initGame(@ModelAttribute("start_time") String startTime) {
//        reset player and exchange tables
//        resetTables();
        gameService.deleteAllGames();
        LocalTime start = LocalTime.parse(startTime);
//        get start time and save new game
        Game game = new Game(start);
        gameService.saveGame(game);
        return "redirect:/";
    }

    @PostMapping(value="/initGameFixed")
    public String initGameFixed() {
//        resetTables();
        gameService.deleteAllGames();
        LocalTime start = LocalTime.now().plusMinutes(1);
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
        evidenceService.deleteAllEvidence();
        exchangeService.deleteAllExchanges();
        playerService.deletePlayers();
        beaconService.deleteAllBeacons();
        zoneService.deleteAllZones();
        gameService.deleteAllGames();
    }

}
