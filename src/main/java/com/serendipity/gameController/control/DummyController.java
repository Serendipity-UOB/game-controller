package com.serendipity.gameController.control;

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

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Controller
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
        output.put("start_time", LocalTime.now().plus(10, ChronoUnit.SECONDS));
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
        String allPlayers = "{ \"all_players\": [{ \"id\": 2, \"real_name\": \"jack jones\", \"code_name\": \"CutieKitten\"}, " +
                "{ \"id\": 3, \"real_name\": \"tilly woodfield\", \"code_name\": \"PuppyLover\"}, " +
                "{ \"id\": 4, \"real_name\": \"tom walker\", \"code_name\": \"Cookingking\"} ]}";
        JSONObject output = new JSONObject();
        output.put("all_players", allPlayers);
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
        output.put("mission_description", "Do this mission at Beacon <b>A</b> in <b>30</b> seconds");
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

}
