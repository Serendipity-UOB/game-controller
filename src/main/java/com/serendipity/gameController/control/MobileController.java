package com.serendipity.gameController.control;

import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.Game;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.service.beaconService.BeaconServiceImpl;
import com.serendipity.gameController.service.exchangeService.ExchangeServiceImpl;
import com.serendipity.gameController.model.Beacon;
import com.serendipity.gameController.service.gameService.GameService;
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
import java.util.Random;
import java.util.Optional;

@Controller
public class MobileController {

    @Autowired
    PlayerServiceImpl playerService;

    @Autowired
    ExchangeServiceImpl exchangeService;

    @Autowired
    BeaconServiceImpl beaconService;

    @Autowired
    GameService gameService;

    private List<Beacon> beacons = new ArrayList<>();

    @RequestMapping(value="/registerPlayer", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> registerPlayer(@RequestBody String json) {
//        receive JSON object and split into variables
        JSONObject input = new JSONObject(json);
        String real = input.getString("real_name");
        String hacker = input.getString("hacker_name");
//        create player to be added to player table
        Player toRegister = new Player(real, hacker);
//        find if the hacker name exists
        Optional<Player> opExists = playerService.getPlayerByHackerName(hacker);
//        create JSON object for response body
        JSONObject output = new JSONObject();
//        set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        if (!(opExists.isPresent())) {
//            add player to player table
            playerService.savePlayer(toRegister);
            Optional<Player> registered = playerService.getPlayerByHackerName(hacker);
            responseStatus = HttpStatus.OK;
            output.put("player_id", registered.get().getId());
        } else { output.put("BAD_REQUEST", "hacker name already exists"); }
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/gameInfo", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getGameInfo(){
        ResponseEntity<String> response;
        JSONObject output = new JSONObject();
        List<Game> games = gameService.getAllGames();
        if (games.isEmpty()) {
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            output.put("start_time", games.get(0).getStartTime());
            output.put("number_players", playerService.countPlayer());
            response = new ResponseEntity<>(output.toString(), HttpStatus.OK);
        }
        // TODO: Consider how we're counting number of players in game
        // TODO: select which game to draw from if we're having multiple games
        return response;
    }

    @RequestMapping(value="/joinGame", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> joinGame(@RequestBody String json) {
//        receive JSON object
        JSONObject input = new JSONObject(json);
//        create JSON object for response body
        JSONObject output = new JSONObject();
//        set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
//        fetch player making request
        Long id = input.getLong("player_id");
        Optional<Player> opPlayer = playerService.getPlayer(id);
//        ensure optional has a value
        if(opPlayer.isPresent()) {
            Player player = opPlayer.get();
//            re-Initialise set of beacons if empty
            if (beacons.isEmpty()) {
                beacons = beaconService.getAllBeacons();
            }
            if (beacons.isEmpty()) { output.put("BAD_REQUEST", "No beacons in beacon table"); }
            else {
                Beacon beacon;
//                randomly take from beacon list using random number
                Random randNum = new Random();
                int n = randNum.nextInt(beacons.size());

                if (player.getHomeBeacon() == -1) {
//                    get beacon randomly chosen from beacon table
                    beacon = beacons.get(n);
                } else {
//                    if beacon has already been assigned then fetch it
                    beacon = beaconService.getBeaconByMajor(player.getHomeBeacon()).get(0);
                }
//                if (beacon.equals(null)) {
                int major = beacon.getMajor();
                String name = beacon.getName();
//                    set JSON values
                output.put("home_beacon_major", major);
                output.put("home_beacon_name", name);
//                    set status value
                responseStatus = HttpStatus.OK;
                if (player.getHomeBeacon() == -1) {
//                    assign home beacon to player
                    playerService.assignHome(player, major);
//                    remove index selected
                    beacons.remove(n);
                }
            }
        } else { output.put("BAD_REQUEST", "Couldn't find player id given"); }
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/startInfo", method=RequestMethod.GET)
    @ResponseBody
    public String getStartInfo() {
        JSONObject output = new JSONObject();
        output.put("all_players", playerService.getAllPlayersStartInfo());
        return output.toString();
    }

    @RequestMapping(value="/playerUpdate", method=RequestMethod.POST)
    @ResponseBody
    public String playerUpdate(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long playerId = input.getLong("player_id");
        Player player = playerService.getPlayer(playerId).get();
        JSONArray beacons = input.getJSONArray("beacons");
        int closestBeaconMajor = beaconService.getClosestBeaconMajor(playerId, beacons);
        int homeBeacon = player.getHomeBeacon();
        player.setNearestBeaconMajor(closestBeaconMajor);
        playerService.savePlayer(player);
        List<Long> nearbyPlayerIds = playerService.getNearbyPlayerIds(player, closestBeaconMajor);
        JSONObject output = new JSONObject();
        output.put("nearby_players", nearbyPlayerIds);
        output.put("points", player.getKills());
        output.put("position", playerService.getLeaderboardPosition(player));
        if (player.isTakenDown()) {
            output.put("taken_down", 1);
            player.setTakenDown(false);
            playerService.savePlayer(player);
        } else {
            output.put("taken_down", 0);
        }
        if (player.isReturnHome()) {
            output.put("req_new_target", 1);
            player.setReturnHome(false);
            playerService.savePlayer(player);
        } else {
            output.put("req_new_target", 0);
        }
        if (closestBeaconMajor == homeBeacon) {
            output.put("home", true);
        } else {
            output.put("home", false);
        }
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

        ResponseEntity<String> response;
        JSONObject input = new JSONObject(json);
        Long interacterId = input.getLong("interacter_id");
        Long interacteeId = input.getLong("interactee_id");
        JSONArray jsonContactIds = input.getJSONArray("contact_ids");
        Player interacter = playerService.getPlayer(interacterId).get();
        Player interactee = playerService.getPlayer(interacteeId).get();

        // Get player contact

        Long contactId = 0l;
        if (jsonContactIds.length() == 0) {
            contactId = 0l;
        } else {
            List<Long> contactIds = new ArrayList<>();
            for (int i = 0; i < jsonContactIds.length(); i++) {
                Long id = jsonContactIds.getJSONObject(i).getLong("contact_id");
                if (id != interacteeId) contactIds.add(id);
            }
            if (contactIds.size() != 0) {
                Random random = new Random();
                contactId = contactIds.get(random.nextInt(contactIds.size()));
            }
        }

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
                    exchangeService.createExchange(interacter, interactee, contactId);
                    response = new ResponseEntity<>(HttpStatus.CREATED);
                } else {
                    Long secondaryId = exchangeService.acceptExchange(exchange2, contactId);
                    JSONObject output = new JSONObject();
                    output.put("secondary_id", secondaryId);
                    response = new ResponseEntity<>(output.toString(), HttpStatus.OK);
                }
            }
        } else {
            exchangeService.createExchange(interacter, interactee, contactId);
            response = new ResponseEntity<>(HttpStatus.CREATED);
        }
        return response;
    }

    @RequestMapping(value="/takeDown", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> takeDown(@RequestBody String json) {
//        receive JSON object
        JSONObject input = new JSONObject(json);
        Long playerId = input.getLong("player_id");
        Long targetId = input.getLong("target_id");
//        create JSON object for response body
        JSONObject output = new JSONObject();
//        set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
//        fetch player making request and target given
        Optional<Player> opPlayer = playerService.getPlayer(playerId);
        Optional<Player> opTarget = playerService.getPlayer(targetId);
//        ensure optionals have a value
        if(opPlayer.isPresent() && opTarget.isPresent()) {
//            unpack optional objects
            Player player = opPlayer.get();
            Player target = opTarget.get();
//            ensure given target matches player's assign target and they haven't been taken down
            if(player.getTarget().getId().equals(target.getId())) {
                if(!player.isTakenDown() && !player.isReturnHome()) {
//                    increment kill count for player
                    playerService.incrementKills(player, 1);
//                    set other players with the same targets returnHome attribute
//                    assume player is locked to getNewTarget by app
                    List<Player> players = playerService.getAllPlayersByTarget(target);
                    System.out.println("Get all players by target successful");
                    for (Player p : players) {
                        if (!(p.getId().equals(player.getId()))){
                            p.setReturnHome(true);
                            playerService.savePlayer(p);
                        }
                        System.out.println("Here");
                    }
                    System.out.println("Return home flags sucessful");
//                    set targets takenDown attribute
                    target.setTakenDown(true);
                    playerService.savePlayer(target);
//                    set output elements
                    responseStatus = HttpStatus.OK;
                    output.put("SUCCESS", "Valid take down");
                } else { output.put("BAD_REQUEST", "Player has been taken down or player must return home"); }
            } else { output.put("BAD_REQUEST", "Target Id given doesn't match player's assigned Target"); }
        } else { output.put("BAD_REQUEST", "Couldn't find player or target id given"); }
        return new ResponseEntity<>(output.toString(), responseStatus);
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
        JSONObject output = new JSONObject();
        output.put("leaderboard", leaderboard);
        return output.toString();
    }

}