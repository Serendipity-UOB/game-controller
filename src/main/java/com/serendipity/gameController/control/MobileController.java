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

        // Unpack JSON and choose secondary contact

        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.FORBIDDEN);
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
        Player contact = playerService.getRandomContact(contactIds);

        // Check for existing exchanges between these two players

        Optional<Exchange> exchangeOptional1 = exchangeService.getExchangeByPlayers(interacter, interactee);
        Optional<Exchange> exchangeOptional2 = exchangeService.getExchangeByPlayers(interactee, interacter);
        boolean activeExchange1 = exchangeService.existsActiveExchangeByPlayers(interacter, interactee);
        boolean activeExchange2 = exchangeService.existsActiveExchangeByPlayers(interactee, interacter);

        // Use cases

        if (activeExchange1) {
            Exchange exchange1 = exchangeOptional1.get();
            if (exchange1.isAccepted()) {
            // The other player has accepted your request, complete the exchange
                Long secondaryId = exchangeService.completeExchange(exchange1);
                JSONObject output = new JSONObject();
                output.put("secondary_id", secondaryId);
                response = new ResponseEntity<>(output.toString(), HttpStatus.OK);
            } else  {
            // The other player hasn't accepted your request yet
                if (exchangeService.isExpired(exchange1)) {
                // If expired, fail request and 'complete' exchange
                    Long ignore = exchangeService.completeExchange(exchange1);
                    response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
                } else {
                // Keep polling
                    response = new ResponseEntity<>(HttpStatus.ACCEPTED);
                }
            }
        } else if (activeExchange2) {
            Exchange exchange2 = exchangeOptional2.get();
            if (exchange2.isAccepted()) {
                response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            } else {
            // They have already requested an exchange with you, you haven't accepted yet, accept
                if (exchangeService.isExpired(exchange2)) {
                    exchangeService.createExchange(interacter, interactee, contact);
                    response = new ResponseEntity<>(HttpStatus.CREATED);
                } else {
                    Long secondaryId = exchangeService.acceptExchange(exchange2, contact);
                    JSONObject output = new JSONObject();
                    output.put("secondary_id", secondaryId);
                    response = new ResponseEntity<>(output.toString(), HttpStatus.OK);
                }
            }
        } else {
            exchangeService.createExchange(interacter, interactee, contact);
            response = new ResponseEntity<>(HttpStatus.CREATED);
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