package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import com.sun.xml.internal.bind.v2.TODO;
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
import com.google.gson.Gson;

@Controller
public class MobileController {

    @Autowired
    PlayerServiceImpl playerService;

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
        JSONObject input = new JSONObject(json);
        String real = input.getString("real_name");
        String hacker = input.getString("hacker_name");
        Long nfc = input.getLong("nfc_id");
//        TODO
//        add player to player table
//        200 OK if all dandy. Assume nfc_id == player_id
//        400 get a new hackername
        return new ResponseEntity(HttpStatus.OK);
    }

    //    GET /gameInfo { }
    @RequestMapping(value="/gameInfo", method=RequestMethod.GET)
    @ResponseBody
    public String getGameInfo() {
        JSONObject output = new JSONObject();
        output.put("start_time", LocalTime.now().plus(10, ChronoUnit.SECONDS));
        output.put("number_players", 0);
//        TODO
//        Draw values from game table
        return output.toString();
    }

    //    POST /joinGame { player_id }
    @RequestMapping(value="/joinGame", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public String joinGame(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long id = input.getLong("player_id");

        JSONObject output = new JSONObject();
        output.put("home_beacon_minor", 0);
        output.put("home_beacon_name", "home");
//        TODO
//        deal with player id
//        assignment of home beacon
//        fetch mapping of minor to name
        return output.toString();
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
//        TODO
//        draw list of players from player table
        return output;
    }

    //    POST /playerUpdate { player_id, beacons[{beacon_minor, rssi}] }
    @RequestMapping(value="/playerUpdate", method=RequestMethod.POST)
    @ResponseBody
    public String playerUpdate(@RequestBody String json) {
//        { nearby_players[], state{[points], position}, [update[]] }
        return "";
    }

    //    POST /newTarget { player_id }
    @RequestMapping(value="/newTarget", method=RequestMethod.POST)
    @ResponseBody
    public String getNewTarget(@RequestBody String json) {
//        { target_player_id }
        return "";
    }

    //    POST /exchange { interacter_id, interactee_id }
    @RequestMapping(value="/exchange", method=RequestMethod.POST)
    @ResponseBody
    public String exchange(@RequestBody String json) {
//        200 OK {secondary_id} or 100 Continue
        return "";
    }

    //    POST /takeDown { player_id, target_id }
    @RequestMapping(value="/takeDown", method=RequestMethod.POST)
    @ResponseBody
    public String takeDown(@RequestBody String json) {
//        200 OK or 400 Bad Requestt
//        400 needs to specify “Not your target” or “Insufficient intel”
        return "";
    }

    //    GET /endInfo
    @RequestMapping(value=" /endInfo", method=RequestMethod.GET)
    @ResponseBody
    public String endInfo() {
//        { leaderboard[{player_id, player_name, score}] }

        return "";
    }

}