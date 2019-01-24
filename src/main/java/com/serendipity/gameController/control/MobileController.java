package com.serendipity.gameController.control;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MobileController {

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
    @RequestMapping(value="/registerPlayer", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity registerPlayer() {
//        200 OK if all dandy. Assume nfc_id == player_id
//        400 get a new hackerna
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
    @RequestMapping(value="/endInfo", method=RequestMethod.GET)
    @ResponseBody
    public String endInfo() {
//        { leaderboard[{player_id, player_name, score}] }
        return "";
    }

}