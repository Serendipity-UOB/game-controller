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
        output.put("number_players", 3);
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/joinGameTest", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> joinGame(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long id = input.getLong("player_id");
        JSONObject output = new JSONObject();
        output.put("home_zone_name", "Server Team Is Best");
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/startInfoTest", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> getStartInfo(@RequestBody String json) {
//        String allPlayers = "[{ \"id\": 2, \"real_name\": \"jack jones\", \"code_name\": \"CutieKitten\"}, " +
//                "{ \"id\": 3, \"real_name\": \"tilly woodfield\", \"code_name\": \"PuppyLover\"}, " +
//                "{ \"id\": 4, \"real_name\": \"tom walker\", \"code_name\": \"Cookingking\"}]";
        List<JSONObject> jsonObjects = new ArrayList<>();

        Player jack = new Player("jack jones", "CutieKitten");
        JSONObject jsonJack = new JSONObject();
        jsonJack.put("id", 2);
        jsonJack.put("real_name", jack.getRealName());
        jsonJack.put("code_name", jack.getCodeName());
        jsonObjects.add(jsonJack);

        Player tilly = new Player("tilly woodfield", "PuppyLover");
        JSONObject jsonTilly = new JSONObject();
        jsonTilly.put("id", 3);
        jsonTilly.put("real_name", tilly.getRealName());
        jsonTilly.put("code_name", tilly.getCodeName());
        jsonObjects.add(jsonTilly);

        Player tom = new Player("tom walker", "CookingKing");
        JSONObject jsonTom = new JSONObject();
        jsonTom.put("id", 4);
        jsonTom.put("real_name", tom.getRealName());
        jsonTom.put("code_name", tom.getCodeName());
        jsonObjects.add(jsonTom);

        JSONObject output = new JSONObject();
        output.put("all_players", jsonObjects);
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
        List<Long> nearbyPlayerIds = new ArrayList<>();
        nearbyPlayerIds.add(2l);
        nearbyPlayerIds.add(3l);
        JSONObject output = new JSONObject();
        output.put("nearby_players", nearbyPlayerIds);
        output.put("reputation", 0);
        output.put("position", 1);
        output.put("exchange_pending", 0);
        output.put("exposed_by", 0);
        output.put("req_new_target", false);
//        output.put("mission_description", "Do this mission at Beacon A in 30 Seconds");
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
        p1.put("player_id",3);
        p1.put("amount",20);
        evidence.put(p1);
        JSONObject p2 = new JSONObject();
        p2.put("player_id",4);
        p2.put("amount",20);
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
        p1.put("amount",10);
        evidence.put(p1);
        JSONObject p2 = new JSONObject();
        p2.put("player_id",4);
        p2.put("amount",10);
        evidence.put(p2);
        JSONObject output = new JSONObject();
        output.put("evidence", evidence);
        output.put("success_description", "You recovered evidence on Tom and Nuha’s activities at Beacon C.");
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
        l2.put("message", "Jack<span class = \"intecept\"> intercepted</span> an <span class = \"exchange\">exchange" +
                "</span> between Louis and Tilly!");
        logs.put(l2);

        JSONArray zones = new JSONArray();
        JSONObject z1 = new JSONObject();
        JSONObject c1 = new JSONObject();
        c1.put("red", 255);
        c1.put("green", 167);
        c1.put("blue", 0);
        z1.put("zone_id", 1);
        z1.put("colour", c1);
        z1.put("size", 4);
        zones.put(z1);
        JSONObject z2 = new JSONObject();
        JSONObject c2 = new JSONObject();
        c2.put("red", 255);
        c2.put("green", 0);
        c2.put("blue", 0);
        z2.put("zone_id", 2);
        z2.put("colour", c2);
        z1.put("size", 2);
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
