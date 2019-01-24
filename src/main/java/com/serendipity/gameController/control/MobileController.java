package com.serendipity.gameController.control;

import com.google.gson.Gson;
import com.serendipity.gameController.model.Player;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MobileController {

    @RequestMapping(value = "/getTest", method = RequestMethod.GET, produces="application/json")
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
    @RequestMapping(value="/registerPlayer", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity registerPlayer() {
//        200 OK if all dandy. Assume nfc_id == player_id
//        400 get a new hackername
        return new ResponseEntity(HttpStatus.OK);
    }

    //    GET /gameInfo { }
    @RequestMapping(value="/gameInfo", method=RequestMethod.GET)
    @ResponseBody
    public String getGameInfo() {
//        { start_time, number_players }
        return "";
    }

    //    POST /joinGame { player_id }
    @RequestMapping(value="/joinGame", method=RequestMethod.POST)
    @ResponseBody
    public String joinGame(@RequestBody String json) {
//        { home_beacon }
        return "";
    }

    //    GET /startInfo
    @RequestMapping(value="/startInfo", method=RequestMethod.GET)
    @ResponseBody
    public String getStartInfo() {
//        { all_players[{id, real_name, hacker_name}] }
        return "";
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