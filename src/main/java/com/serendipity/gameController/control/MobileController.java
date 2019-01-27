package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Game;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.model.Beacon;
import com.serendipity.gameController.service.beaconService.BeaconService;
import com.serendipity.gameController.service.beaconService.BeaconServiceImpl;
import com.serendipity.gameController.service.gameService.GameService;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.util.Random;
import java.util.Optional;
import com.google.gson.Gson;


@Controller
public class MobileController {

    @Autowired
    PlayerServiceImpl playerService;

    @Autowired
    BeaconServiceImpl beaconService;

    @Autowired
    GameService gameService;

    private List<Long> beacons = new ArrayList<>();

    @RequestMapping(value = "/getTest", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public String getTest() {
        JSONObject object = new JSONObject("{'aa':'bb'}");
        return object.toString();
    }

    @RequestMapping(value="/postTest", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public String postTest(@RequestBody String json) {
        JSONObject object = new JSONObject(json);
        return object.toString();
    }

    //    POST /registerPlayer { real_name, hacker_name }
    @RequestMapping(value="/registerPlayer", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> registerPlayer(@RequestBody String json) {
//        receive JSON object and split into variables
        JSONObject input = new JSONObject(json);
        String real = input.getString("real_name");
        String hacker = input.getString("hacker_name");
//        create player to be added to player table
        Player toRegister = new Player(real, hacker);
//        find if the hacker name exists
//        will return null if not
        Player exists = playerService.getPlayerByHackerName(hacker);
//        create JSON object for response body
        JSONObject output = new JSONObject();
//        set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        if (exists == null) {
//            add player to player table
            playerService.savePlayer(toRegister);
            Player registered = playerService.getPlayerByHackerName(hacker);
            responseStatus = HttpStatus.OK;
            output.put("player_id", registered.getId());
        } else { output.put("BAD_REQUEST", "hacker name already exists"); }
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    //    GET /gameInfo { }
    @RequestMapping(value="/gameInfo", method=RequestMethod.GET)
    @ResponseBody
    public String getGameInfo() {
//         create JSON object for return
        JSONObject output = new JSONObject();
//         set JSON values
        long time;
        time = Instant.now().getEpochSecond() + 10;
//        Optional<Game> opGame = gameService.getGame(game_id);
//        if (opGame.isPresent()) { time = opGame.get().getStartTime(); }
        output.put("start_time", time);
//        count number of players in table for number of players in game
        output.put("number_players", playerService.countPlayer());
//        TODO: Draw time value from game table (Initialised by admin)
//        TODO: Consider how we're counting number of players in game
        return output.toString();
    }

    //    POST /joinGame { player_id }
    @RequestMapping(value="/joinGame", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> joinGame(@RequestBody String json) {
//        receive JSON object
        JSONObject input = new JSONObject(json);
//        create JSON object for response body
        JSONObject output = new JSONObject();
//        set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
//        fetch player making request
        Long id = input.getLong("player_id");
        Optional<Player> opPlayer = playerService.getPlayer(id);
//        ensure optional has a value
        if(opPlayer.isPresent()) {
            Player player = opPlayer.get();
//            re-Initialise set of beacons if empty
            if (beacons.isEmpty()) {
                for (long i = 1; i < beaconService.countBeacons() + 1; i++) {
                    beacons.add(i);
                }
            }
            if (beacons.isEmpty()) { output.put("BAD_REQUEST", "No beacons in beacon table"); }
            else {
                Optional<Beacon> opBeacon;
//                randomly take from beacon list using random number
                Random randNum = new Random();
                int n = randNum.nextInt(beacons.size());
                if (player.getHomeBeacon() == -1) {
                    long index = beacons.get(n);
//                    get beacon randomly chosen from beacon table
                    opBeacon = beaconService.getBeacon(index);
                } else {
                    opBeacon = beaconService.getBeaconByMinor(player.getHomeBeacon());
                }
//                ensure optional has a value
                if (opBeacon.isPresent()) {
//                    unpack optional object
                    Beacon beacon = opBeacon.get();
                    int minor = beacon.getMinor();
                    String name = beacon.getName();
//                    set JSON values
                    output.put("home_beacon_minor", minor);
                    output.put("home_beacon_name", name);
//                    set status value
                    responseStatus = HttpStatus.OK;
                    if (player.getHomeBeacon() == -1) {
//                    assign home beacon to player
                        playerService.assignHome(player, minor);
//                    remove index selected
                        beacons.remove(n);
                    }
                } else { output.put("BAD_REQUEST", "Couldn't find beacon to allocate"); }
            }
        } else { output.put("BAD_REQUEST", "Couldn't find player id given"); }
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    //    GET /startInfo
    @RequestMapping(value="/startInfo", method=RequestMethod.GET)
    @ResponseBody
    public String getStartInfo() {
//        playerService.createPlayers();
//        create JSON object for response
        JSONObject output = new JSONObject();
        output.put("all_players", playerService.getAllPlayersStartInfo());
        return output.toString();
    }

    @RequestMapping(value="/playerUpdate", method=RequestMethod.POST)
    @ResponseBody
    public String playerUpdate(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long playerId = input.getLong("player_id");
        JSONArray beacons = input.getJSONArray("beacons");
        // TODO: Find closest beacon
        // TODO: Find players who are 'nearby'
        List<Long> nearbyPlayerIds = new ArrayList<>();
        nearbyPlayerIds.add(0l);
        nearbyPlayerIds.add(1l);
        JSONObject output = new JSONObject();
        output.put("nearby_players", nearbyPlayerIds);
        int kills = 3;
        output.put("points", kills);
        int position = 1;
        output.put("position", position);
        int takenDown = 0;
        output.put("taken_down", takenDown);
        int reqNewTarget = 0;
        output.put("req_new_target", reqNewTarget);
        return output.toString();
    }

    @RequestMapping(value="/newTarget", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public String getNewTarget(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long playerId = input.getLong("player_id");
        // TODO: Assign target to player
        Long targetId = 0l;
        JSONObject output = new JSONObject();
        output.put("target_player_id", targetId);
        return output.toString();
    }

    @RequestMapping(value="/exchange", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity exchange(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long interacterId = input.getLong("interacter_id");
        Long interacteeId = input.getLong("interactee_id");
        // TODO: Exchange or error
        Long secondaryId = 0l;
        JSONObject output = new JSONObject();
        output.put("secondary_id", secondaryId);
        ResponseEntity<String> response = new ResponseEntity<>(output.toString(), HttpStatus.OK);
        return response;
    }

    //    POST /takeDown { player_id, target_id }
    @RequestMapping(value="/takeDown", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> takeDown(@RequestBody String json) {
//        receive JSON object
        JSONObject input = new JSONObject(json);
        Long playerId = input.getLong("player_id");
        Long targetId = input.getLong("target_id");
//        create JSON object for response body
        JSONObject output = new JSONObject();
//        set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
//        fetch player making request and target given
        Optional<Player> opPlayer = playerService.getPlayer(playerId);
        Optional<Player> opTarget = playerService.getPlayer(targetId);
//        ensure optionals have a value
        if(opPlayer.isPresent() && opTarget.isPresent()) {
//            unpack optional objects
            Player player = opPlayer.get();
            Player target = opTarget.get();
//            ensure given target matches player's assign target and they haven't been taken down
            if(player.getTarget().getId().equals(target.getId())) {
                if(!player.isTakenDown() && !player.isReturnHome()) {
//                    set targets returnHome attribute
                    player.setReturnHome(true);
                    playerService.savePlayer(player);
//                    set targets takenDown attribute
                    target.setTakenDown(true);
                    playerService.savePlayer(target);
//                    set output elements
                    responseStatus = HttpStatus.OK;
                    output.put("SUCCESS", "Valid take down");
                } else { output.put("BAD_REQUEST", "Player has been taken down or player must return home"); }
            } else { output.put("BAD_REQUEST", "Target Id given doesn't match player's assigned Target"); }
        } else { output.put("BAD_REQUEST", "Couldn't find player or target id given"); }
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/endInfo", method=RequestMethod.GET)
    @ResponseBody
    public String endInfo() {
        // TODO: Get all players, sorted by kills
        List<Player> players = new ArrayList<>();
        players.add(new Player("Tilly","Headshot"));
        players.add(new Player("Tom","Cutiekitten"));
        String output = new Gson().toJson(players);
        return output;
    }

}