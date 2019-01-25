package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.model.Beacon;
import com.serendipity.gameController.service.beaconService.BeaconService;
import com.serendipity.gameController.service.beaconService.BeaconServiceImpl;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import com.sun.xml.internal.bind.v2.TODO;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.time.Instant;
import java.util.Random;
import java.util.Optional;
import com.google.gson.Gson;

import javax.xml.ws.Response;

@Controller
public class MobileController {

    @Autowired
    PlayerServiceImpl playerService;

    @Autowired
    BeaconServiceImpl beaconService;

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

    //    POST /registerPlayer { real_name, hacker_name, nfc_id }
    @RequestMapping(value="/registerPlayer", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity registerPlayer(@RequestBody String json) {
//        receive JSON object and split into variables
        JSONObject input = new JSONObject(json);
        String real = input.getString("real_name");
        String hacker = input.getString("hacker_name");
        Long nfc = input.getLong("nfc_id");
//        create player to be added to player table
        Player toRegister = new Player(real, hacker, nfc);
//        find if the hacker name exists
//        will return null if not
        Player exists = playerService.findHackerName(hacker);
//        variable to hold http response code
        ResponseEntity response;
        if (exists == null) {
//            add player to player table
            playerService.savePlayer(toRegister);
            response = new ResponseEntity(HttpStatus.OK);
        }
        else { response = new ResponseEntity(HttpStatus.BAD_REQUEST); }

        return response;
    }

    //    GET /gameInfo { }
    @RequestMapping(value="/gameInfo", method=RequestMethod.GET)
    @ResponseBody
    public String getGameInfo() {
//         create JSON object for return
        JSONObject output = new JSONObject();
//         set JSON values
        output.put("start_time", Instant.now().getEpochSecond() + 10);
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
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
//        fetch player making request
        Long id = input.getLong("player_id");
        Optional<Player> opPlayer = playerService.getPlayer(id);
//        ensure optional has a value
        if(opPlayer.isPresent()) {
            Player player = opPlayer.get();
//            re-Initialise set of beacons
            if (beacons.isEmpty()) {
                for(long i = 1; i < beaconService.countBeacons() + 1; i++) {
                    beacons.add(i);
                }
            }
            if(player.getHomeBeacon() == -1) {
//                generate randomly take from beacon list using random number
                Random randNum = new Random();
                int n = randNum.nextInt(beacons.size());
                long index = beacons.get(n);
//                get beacon randomly chosen from beacon table
                Optional<Beacon> opBeacon = beaconService.getBeacon(index);
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
//                    assign home beacon to player
                    playerService.assignHome(player, minor);
//                    remove index selected
                    beacons.remove(n);
                }
                else {
//                  TODO: Error
                }
            }
            else {
//                TODO: Error
            }
        }
        else {
//          TODO: Error
        }
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    //    GET /startInfo
    @RequestMapping(value="/startInfo", method=RequestMethod.GET)
    @ResponseBody
    public String getStartInfo() {
        List<Player> ret = new ArrayList<>();
        ret.add(new Player("Jack", "Cutiekitten"));
        ret.add(new Player("Tilly", "Puppylover"));
        ret.add(new Player("Tom", "Cookingking"));
        String output = new Gson().toJson(ret);
        JSONObject obj = new JSONObject();
        obj.put("all_players", output);
//        TODO
//        draw list of players from player table
        return obj.toString();
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

    @RequestMapping(value="/takeDown", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity takeDown(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long playerId = input.getLong("player_id");
        Long targetId = input.getLong("target_id");
        // TODO: Takedown or error
        return new ResponseEntity(HttpStatus.OK);
        // 400 Bad Request to specify “Not your target” or “Insufficient intel”
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