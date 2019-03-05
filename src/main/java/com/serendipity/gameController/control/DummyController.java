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
    public String getGameInfo() {
        JSONObject output = new JSONObject();
        output.put("start_time", LocalTime.now().plus(10, ChronoUnit.SECONDS));
        output.put("number_players", 3);
        return output.toString();
    }

    @RequestMapping(value="/joinGameTest", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> joinGame(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long id = input.getLong("player_id");
        JSONObject output = new JSONObject();
        output.put("home_beacon_major", 0);
        output.put("home_beacon_name", "home");
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/startInfoTest", method=RequestMethod.GET)
    @ResponseBody
    public String getStartInfo() {
        return "{ \"all_players\": [{ \"id\": 2, \"real_name\": \"jack jones\", \"code_name\": \"CutieKitten\"}, " +
                "{ \"id\": 3, \"real_name\": \"tilly woodfield\", \"code_name\": \"PuppyLover\"}, " +
                "{ \"id\": 4, \"real_name\": \"tom walker\", \"code_name\": \"Cookingking\"} ]}";
    }

    @RequestMapping(value="/atHomeBeaconTest", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity atHomeBeacon(@RequestBody String json) {
        JSONObject output = new JSONObject();
        output.put("home", true);
        ResponseEntity<String> response = new ResponseEntity<>(output.toString(), HttpStatus.OK);
        return response;
    }

    @RequestMapping(value="/playerUpdateTest", method=RequestMethod.POST)
    @ResponseBody
    public String playerUpdate(@RequestBody String json) {
        List<Long> nearbyPlayerIds = new ArrayList<>();
        nearbyPlayerIds.add(2l);
        nearbyPlayerIds.add(3l);
        JSONObject output = new JSONObject();
        output.put("nearby_players", nearbyPlayerIds);
        output.put("points", 0);
        output.put("position", 1);
        output.put("exchange_pending", 0);
        output.put("exposed", false);
        output.put("req_new_target", false);
        output.put("mission_description", "");
        output.put("game_over", false);
        return output.toString();
    }

    @RequestMapping(value="/newTargetTest", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public String getNewTarget(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long playerId = input.getLong("player_id");
        Long targetId = 2l;
        JSONObject output = new JSONObject();
        output.put("target_player_id", targetId);
        return output.toString();
    }

    @RequestMapping(value="/exchangeRequestTest", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity exchangeReq(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long interacterId = input.getLong("interacter_id");
        Long interacteeId = input.getLong("interactee_id");
        JSONArray jsonContactIds = input.getJSONArray("contact_ids");
        Long secondaryId = 4l;
        JSONObject output = new JSONObject();
        output.put("primary_id", interacteeId);
        output.put("primary_evidence", 10);
        output.put("secondary_id", secondaryId);
        output.put("secondary_evidence", 20);
        ResponseEntity<String> response = new ResponseEntity<>(output.toString(), HttpStatus.ACCEPTED);
        return response;
    }

    @RequestMapping(value="/exchangeResponseTest", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity exchangeRes(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long playerId = input.getLong("player_id");
        Long exchangerId = input.getLong("exchanger_id");
        int res = input.getInt("response");
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        if (res == 0) {
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else if(res == 1) {
            Long secondaryId = 4l;
            JSONObject output = new JSONObject();
            output.put("primary_id", exchangerId);
            output.put("primary_evidence", 10);
            output.put("secondary_id", secondaryId);
            output.put("secondary_evidence", 20);
            response = new ResponseEntity<>(output.toString(), HttpStatus.ACCEPTED);
        } else if (res == 2){
            response = new ResponseEntity<>(HttpStatus.RESET_CONTENT);
        }
        return response;
    }

    @RequestMapping(value="/exposeTest", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> expose(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long playerId = input.getLong("player_id");
        Long targetId = input.getLong("target_id");
        JSONObject output = new JSONObject();
        output.put("SUCCESS", "Valid expose");
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/interceptTest", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> intercept(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long targetId = input.getLong("target_id");
        Long secondaryId = 4l;
        JSONObject output = new JSONObject();
        output.put("primary_id", targetId);
        output.put("primary_evidence", 30);
        output.put("secondary_id", secondaryId);
        output.put("secondary_evidence", 30);
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

    @RequestMapping(value="/missionUpdateTest", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> missionUpdate() {
        JSONArray rewards = new JSONArray();
        JSONObject r1 = new JSONObject();
        r1.put("player_id",3);
        r1.put("evidence",10);
        rewards.put(r1);
        JSONObject r2 = new JSONObject();
        r2.put("player_id",4);
        r2.put("evidence",10);
        rewards.put(r2);
        JSONObject output = new JSONObject();
        output.put("rewards", rewards);
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
        p1.put("position", 2);
        p2.put("score",1);
        leaderboard.put(p2);
        JSONObject output = new JSONObject();
        output.put("leaderboard", leaderboard);
        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
    }

}
