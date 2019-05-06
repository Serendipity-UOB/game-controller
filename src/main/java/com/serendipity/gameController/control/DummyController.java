package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@CrossOrigin
public class DummyController {

    @Autowired
    PlayerServiceImpl playerService;

    @RequestMapping(value="/registerPlayerTest", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> registerPlayer(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        String real = input.getString("real_name");
        String code = input.getString("code_name");
        JSONObject output = new JSONObject();
        output.put("player_id", 1);
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/gameInfoTest", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getGameInfo() {
        JSONObject output = new JSONObject();
        output.put("start_time", LocalTime.now().plus(4, ChronoUnit.SECONDS));
        output.put("number_players", 13);
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/joinGameTest", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> joinGame(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long id = input.getLong("player_id");
        JSONObject output = new JSONObject();
        output.put("home_zone_name", "UN");
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/startInfoTest", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> getStartInfo(@RequestBody String json) {
//        String allPlayers = "[{ \"id\": 2, \"real_name\": \"jack jones\", \"code_name\": \"CutieKitten\"}, " +
//                "{ \"id\": 3, \"real_name\": \"tilly woodfield\", \"code_name\": \"PuppyLover\"}, " +
//                "{ \"id\": 4, \"real_name\": \"tom walker\", \"code_name\": \"Cookingking\"}]";
        List<JSONObject> jsonObjects = new ArrayList<>();

        List<String> names = new ArrayList<String>(Arrays.asList("Jack", "Palvi", "Tom", "Tilly", "Louis", "Nuha",
                "David", "Callum", "James", "Sibela", "Elias", "Myles", "Katie"));
        List<String> codes = new ArrayList<String>(Arrays.asList("Alpha", "Beta", "Charlie", "Delta", "Echo", "Foxtrot",
                "Golf", "Hotel", "India", "Juliett", "Kilo", "Lima", "Mike"));

        for(int i = 0; i < names.size(); i++){
            JSONObject player = new JSONObject();
            player.put("id", i+1);
            player.put("real_name", names.get(i));
            player.put("code_name", codes.get(i));
            jsonObjects.add(player);
        }

        JSONObject output = new JSONObject();
        output.put("all_players", jsonObjects);
        output.put("first_target_id", 4);
        output.put("end_time", LocalTime.now().plusMinutes(1));
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/atHomeBeaconTest", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> atHomeBeacon(@RequestBody String json) {
        JSONObject output = new JSONObject();
        output.put("home", true);
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/playerUpdateTest", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> playerUpdate(@RequestBody String json) {
        List<JSONObject> nearbyPlayers = new ArrayList<>();
        List<JSONObject> farPlayers = new ArrayList<>();

        for(int i = 0; i < 5; i++){
            JSONObject near = new JSONObject();
            near.put("id", i+1);
            near.put("location", 2);
            nearbyPlayers.add(near);
        }
        for(int i = 5; i < 7; i++){
            JSONObject far = new JSONObject();
            far.put("id", i+1);
            far.put("location", 0);
            farPlayers.add(far);
        }
        for(int i = 7; i < 10; i++) {
            JSONObject far = new JSONObject();
            far.put("id", i + 1);
            far.put("location", 1);
            farPlayers.add(far);
        }
        for(int i = 10; i < 11; i++){
            JSONObject far = new JSONObject();
            far.put("id", i+1);
            far.put("location", 3);
            farPlayers.add(far);
        }
        for(int i = 11; i < 13; i++){
            JSONObject far = new JSONObject();
            far.put("id", i+1);
            far.put("location", 4);
            farPlayers.add(far);
        }
        JSONObject output = new JSONObject();
        output.put("location", 2);
        output.put("nearby_players", nearbyPlayers);
        output.put("far_players", farPlayers);
        output.put("reputation", 0);
        output.put("position", 1);
        output.put("exchange_pending", 0);
        output.put("exposed_by", 0);
        output.put("req_new_target", false);
//        output.put("mission_description", "Evidence available on Nuha and Tom.\nGo to Italy.");
        output.put("mission_description", "");
        output.put("game_over", false);
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/newTargetTest", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> getNewTarget(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long playerId = input.getLong("player_id");
        Long targetId = 2l;
        JSONObject output = new JSONObject();
        output.put("target_player_id", targetId);
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/exchangeRequestTest", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> exchangeReq(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long responderId = input.getLong("responder_id");
        Long secondaryId = 4l;
        JSONArray evidence = new JSONArray();
        JSONObject p1 = new JSONObject();
        p1.put("player_id",responderId);
        p1.put("amount",10);
        evidence.put(p1);
        JSONObject p2 = new JSONObject();
        p2.put("player_id",secondaryId);
        p2.put("amount",20);
        evidence.put(p2);
        JSONObject output = new JSONObject();
        output.put("evidence", evidence);
        return new ResponseEntity<>(output.toString(), HttpStatus.ACCEPTED);
    }

    @RequestMapping(value="/exchangeResponseTest", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> exchangeRes(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long responderId = input.getLong("responder_id");

        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        int res = input.getInt("response");
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (res == 0) {
            output.put("time_remaining", LocalTime.now());
            responseStatus = HttpStatus.PARTIAL_CONTENT;
        } else if(res == 1) {
            Long secondaryId = 4l;
            JSONArray evidence = new JSONArray();
            JSONObject p1 = new JSONObject();
            p1.put("player_id",responderId);
            p1.put("amount",10);
            evidence.put(p1);
            JSONObject p2 = new JSONObject();
            p2.put("player_id",secondaryId);
            p2.put("amount",20);
            evidence.put(p2);
            output.put("evidence", evidence);
            responseStatus = HttpStatus.ACCEPTED;
        } else if (res == 2){
            responseStatus = HttpStatus.RESET_CONTENT;
        }
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/exposeTest", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> expose(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long playerId = input.getLong("player_id");
        Long targetId = input.getLong("target_id");
        JSONObject output = new JSONObject();
        output.put("reputation", 10);
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/interceptTest", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> intercept(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long targetId = input.getLong("target_id");
        JSONArray evidence = new JSONArray();
        JSONObject p1 = new JSONObject();
        p1.put("player_id",targetId);
        p1.put("amount",30);
        evidence.put(p1);
        JSONObject p2 = new JSONObject();
        p2.put("player_id",4);
        p2.put("amount",10);
        evidence.put(p2);
        JSONObject output = new JSONObject();
        output.put("evidence", evidence);
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/missionUpdateTest", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> missionUpdate(@RequestBody String json) {
        JSONArray evidence = new JSONArray();
        JSONObject p1 = new JSONObject();
        p1.put("player_id",3);
        p1.put("amount",50);
        evidence.put(p1);
        JSONObject p2 = new JSONObject();
        p2.put("player_id",6);
        p2.put("amount",50);
        evidence.put(p2);
        JSONObject output = new JSONObject();
        output.put("evidence", evidence);
        output.put("success_description", "Reward: Evidence on Nuha and Tom.");
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }


    @RequestMapping(value="/endInfoTest", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> endInfo() {
        JSONArray leaderboard = new JSONArray();
        JSONObject p1 = new JSONObject();
        p1.put("player_id",3);
        p1.put("position", 1);
        p1.put("score",3);
        leaderboard.put(p1);
        JSONObject p2 = new JSONObject();
        p2.put("player_id",4);
        p2.put("position", 2);
        p2.put("score",1);
        leaderboard.put(p2);
        JSONObject output = new JSONObject();
        output.put("leaderboard", leaderboard);
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/spectatorTest", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> spectator() {
        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.OK;

        JSONArray logs = new JSONArray();
        JSONObject l1 = new JSONObject();
        l1.put("time", LocalTime.now());
        l1.put("message", "Jack <span class = \"expose\">exposed</span> Tilly!");
        logs.put(l1);
        JSONObject l2 = new JSONObject();
        l2.put("time", LocalTime.now());
        l2.put("message", "Jack<span class = \"intercept\"> intercepted</span> an <span class = \"exchange\">exchange" +
                "</span> between Louis and Tilly!");
        logs.put(l2);

        JSONArray zones = new JSONArray();
        JSONObject z1 = new JSONObject();
        JSONObject c1 = new JSONObject();
        c1.put("red", 255);
        c1.put("green", 167);
        c1.put("blue", 0);
        z1.put("zone_id", 1);
        z1.put("zone_name", "Italy");
        z1.put("colour", c1);
        z1.put("size", 0.7);
        z1.put("x", 0.2);
        z1.put("y", 0.4);
        zones.put(z1);
        JSONObject z2 = new JSONObject();
        JSONObject c2 = new JSONObject();
        c2.put("red", 255);
        c2.put("green", 0);
        c2.put("blue", 0);
        z2.put("zone_id", 2);
        z2.put("zone_name", "Sweden");
        z2.put("colour", c2);
        z2.put("size", 0.3);
        z2.put("x", 0.7);
        z2.put("y", 0.3);
        zones.put(z2);

        JSONArray leaders = new JSONArray();
        JSONObject p1 = new JSONObject();
        p1.put("position", 1);
        p1.put("real_name", "Jack");
        p1.put("reputation", 60);
        leaders.put(p1);
        JSONObject p2 = new JSONObject();
        p2.put("position", 2);
        p2.put("real_name", "Tilly");
        p2.put("reputation", 30);
        leaders.put(p2);

        output.put("logs", logs);
        output.put("leaderboard", leaders);
        output.put("zones", zones);

        return new ResponseEntity<>(output.toString(), responseStatus);
    }

}
