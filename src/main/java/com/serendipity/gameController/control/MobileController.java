package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.service.exchangeService.ExchangeService;
import com.serendipity.gameController.service.exchangeService.ExchangeServiceImpl;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
//import com.sun.xml.internal.bind.v2.TODO;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.google.gson.Gson;

@Controller
public class MobileController {

    @Autowired
    PlayerServiceImpl playerService;

    @Autowired
    ExchangeServiceImpl exchangeService;

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
        Long newTargetId = playerService.newTarget(playerId);
        JSONObject output = new JSONObject();
        output.put("target_player_id", newTargetId);
        return output.toString();
    }

    @RequestMapping(value="/exchange", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity exchange(@RequestBody String json) {
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
        JSONObject input = new JSONObject(json);
        Long interacterId = input.getLong("interacter_id");
        Long interacteeId = input.getLong("interactee_id");
        JSONArray jsonContactIds = input.getJSONArray("contact_ids");
        Player interacter = playerService.getPlayer(interacterId).get();
        Player interactee = playerService.getPlayer(interacteeId).get();
        List<Long> contactIds = new ArrayList<>();
        for (int i = 0; i < jsonContactIds.length(); i++) {
            contactIds.add(jsonContactIds.getJSONObject(i).getLong("contact_id"));
        }
        Optional<Exchange> exchangeOptional = exchangeService.getExchangeByPlayers(interactee, interacter);
        if (exchangeOptional.isPresent()) {
            // TODO: Accept the exchange and return the secondary_id
            Long secondaryId = exchangeService.acceptExchange(exchangeOptional.get());
            JSONObject output = new JSONObject();
            output.put("secondary_id", secondaryId);
            response = new ResponseEntity<>(output.toString(), HttpStatus.OK);
        } else {
            exchangeOptional = exchangeService.getExchangeByPlayers(interacter, interactee);
            if (exchangeOptional.isPresent()) {
                Exchange exchange = exchangeOptional.get();
                if (exchange.isAccepted()) {
                    // TODO: Complete the exchange
                    Random random = new Random();
                    Long contactId = contactIds.get(random.nextInt(contactIds.size()));
                    Player targetPlayerContact = playerService.getPlayer(contactId).get();
                    Long secondaryId = exchangeService.completeExchange(exchange, targetPlayerContact);
                    JSONObject output = new JSONObject();
                    output.put("secondary_id", secondaryId);
                    response = new ResponseEntity<>(output.toString(), HttpStatus.OK);
                } else {
                    // TODO: Poll the exchange
                    response = new ResponseEntity<>(HttpStatus.ACCEPTED);
                }
            } else {
                // TODO: Make a new exchange
                Random random = new Random();
                Long contactId = contactIds.get(random.nextInt(contactIds.size()));
                Player requestPlayerContact = playerService.getPlayer(contactId).get();
                Exchange exchange = new Exchange(interacter, interactee, requestPlayerContact);
                exchangeService.saveExchange(exchange);
                response = new ResponseEntity<>(HttpStatus.CREATED);
            }
        }
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
        JSONArray leaderboard = new JSONArray();
        List<Player> players = playerService.getAllPlayersByScore();
        for (Player player : players) {
            JSONObject playerInfo = new JSONObject();
            playerInfo.put("player_id", player.getId());
            playerInfo.put("score", player.getKills());
            leaderboard.put(playerInfo);
        }
        return leaderboard.toString();
    }

}