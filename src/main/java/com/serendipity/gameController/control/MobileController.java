package com.serendipity.gameController.control;

import com.serendipity.gameController.model.*;
import com.serendipity.gameController.service.beaconService.BeaconServiceImpl;
import com.serendipity.gameController.service.exchangeService.ExchangeServiceImpl;
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
import java.util.*;

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

    @RequestMapping(value="/registerPlayer", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> registerPlayer(@RequestBody String json) {
        ResponseEntity<String> response;
        JSONObject input = new JSONObject(json);
        String realName = input.getString("real_name");
        String codeName = input.getString("code_name");
        Optional<Game> optionalNextGame = gameService.getNextGame();
        if (!optionalNextGame.isPresent()) {
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else if (!playerService.isValidRealNameAndCodeName(realName, codeName)) {
            response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } else {
            Player player = new Player(realName, codeName);
            playerService.savePlayer(player);
            JSONObject output = new JSONObject();
            output.put("player_id", player.getId());
            response = new ResponseEntity<>(output.toString(), HttpStatus.OK);
        }
        return response;
    }

    @RequestMapping(value="/gameInfo", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getGameInfo(){
        ResponseEntity<String> response;
        JSONObject output = new JSONObject();
        Optional<Game> optionalNextGame = gameService.getNextGame();
        if (!optionalNextGame.isPresent()) {
            response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            Game nextGame = optionalNextGame.get();
            output.put("start_time", nextGame.getStartTime());
            output.put("number_players", playerService.countAllPlayers());
            response = new ResponseEntity<>(output.toString(), HttpStatus.OK);
        }
        return response;
    }

    @RequestMapping(value="/joinGame", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> joinGame(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        JSONObject output = new JSONObject();
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        Long id = input.getLong("player_id");
        Optional<Player> opPlayer = playerService.getPlayer(id);
        if(opPlayer.isPresent()) {
            Player player = opPlayer.get();

            Map<Integer, Integer> sumOfMajors = beaconService.sumBeacons();
            List<Beacon> beacons = beaconService.getAllBeacons();

            if (beacons.isEmpty()) { output.put("BAD_REQUEST", "No beacons in beacon table");
            } else {
                Beacon beacon = new Beacon();
                if (player.getHomeBeacon() == -1) {
                    int minAllocation = Integer.MAX_VALUE;
                    for(Beacon b : beacons) {
                        if (sumOfMajors.get(b.getMajor()) < minAllocation) {
                            minAllocation = sumOfMajors.get(b.getMajor());
                            beacon = b;
                        }
                    }
                } else {
                    beacon = beaconService.getBeaconByMajor(player.getHomeBeacon()).get(0);
                }
                int major = beacon.getMajor();
                String name = beacon.getName();
                output.put("home_beacon_major", major);
                output.put("home_beacon_name", name);
                responseStatus = HttpStatus.OK;
                if (player.getHomeBeacon() == -1) {
                    playerService.assignHome(player, major);
                }
            }
        } else {
            output.put("BAD_REQUEST", "Couldn't find player id given");
        }
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/startInfo", method=RequestMethod.GET)
    @ResponseBody
    public String getStartInfo() {
        JSONObject output = new JSONObject();
        output.put("all_players", playerService.getAllPlayersStartInfo());
        return output.toString();
    }

    @RequestMapping(value="/atHomeBeacon", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity atHomeBeacon(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        Long playerId = input.getLong("player_id");
        Player player = playerService.getPlayer(playerId).get();
        JSONArray beacons = input.getJSONArray("beacons");
        int closestBeaconMajor = beaconService.getClosestBeaconMajor(playerId, beacons);
        player.setNearestBeaconMajor(closestBeaconMajor);
        playerService.savePlayer(player);
        int homeBeacon = player.getHomeBeacon();
        JSONObject output = new JSONObject();
        if (closestBeaconMajor == homeBeacon) {
            output.put("home", true);
        } else {
            output.put("home", false);
        }
        ResponseEntity<String> response = new ResponseEntity<>(output.toString(), HttpStatus.OK);
        return response;
    }

    @RequestMapping(value="/playerUpdate", method=RequestMethod.POST)
    @ResponseBody
    public String playerUpdate(@RequestBody String json) {

        // Setup json
        JSONObject input = new JSONObject(json);
        Long playerId = input.getLong("player_id");
        JSONArray beacons = input.getJSONArray("beacons");
        JSONObject output = new JSONObject();

        // Deal with beacon/zone
        int closestBeaconMajor = beaconService.getClosestBeaconMajor(playerId, beacons);
        Player player = playerService.getPlayer(playerId).get();
        player.setNearestBeaconMajor(closestBeaconMajor);
        playerService.savePlayer(player);

        // Nearby players
        List<Long> nearbyPlayerIds = playerService.getNearbyPlayerIds(player);
        output.put("nearby_players", nearbyPlayerIds);

        // Reputation
        output.put("reputation", player.getReputation());

        // Position
        output.put("position", playerService.getLeaderboardPosition(player));

        // Exposed
        if (player.isExposed()) {
            output.put("exposed", true);
            player.setExposed(false);
            playerService.savePlayer(player);
        } else {
            output.put("exposed", false);
        }

        // Return home
        // TODO: Make 'returnHome' clearer, what does this actually mean?
        if (player.isReturnHome()) {
            output.put("req_new_target", true);
            player.setReturnHome(false);
            playerService.savePlayer(player);
        } else {
            output.put("req_new_target", false);
        }

        // Game over
        // TODO: Review when add ability to attach players to games,
        // TODO: and pass in the game that the current player is a part of, instead of games.get(0)
        List<Game> games = gameService.getAllGamesByStartTimeAsc();
        if (gameService.isGameOver(games.get(0))) {
            output.put("game_over", true);
        } else {
            output.put("game_over", false);
        }

        // Mission
        // TODO: Implement
        output.put("mission_description", "");

        // Exchange pending
        // TODO: Implement
        output.put("exchange_pending", 0);

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

    @RequestMapping(value="/exchangeRequest", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity exchangeRequest(@RequestBody String json) {
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // JSON
        JSONObject input = new JSONObject(json);
        Long requesterId = input.getLong("requester_id");
        Long responderId = input.getLong("responder_id");
        JSONArray jsonContactIds = input.getJSONArray("contact_ids");
        Player requester = playerService.getPlayer(requesterId).get();
        Player responder = playerService.getPlayer(responderId).get();

        // Exchange functionality
        Optional<Exchange> optionalExchange = exchangeService.getMostRecentExchangeFromPlayer(requester);
        if (optionalExchange.isPresent()) {
            Exchange exchange = optionalExchange.get();
            if (exchange.getResponsePlayer() == responder) {
                if (exchangeService.isExpired(exchange)) {
                    response = new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
                } else {
                    if (exchange.getResponse().equals(ExchangeResponse.ACCEPTED)) {
                        JSONArray jsonEvidences = new JSONArray();
                        for (Evidence evidence : exchange.getResponseEvidence()) {
                            JSONObject jsonEvidence = new JSONObject();
                            jsonEvidence.put("player_id", evidence.getPlayer().getId());
                            jsonEvidence.put("amount", evidence.getAmount());
                            jsonEvidences.put(jsonEvidence);
                        }
                        JSONObject output = new JSONObject();
                        output.put("evidence", jsonEvidences);
                        response = new ResponseEntity<>(output.toString(), HttpStatus.ACCEPTED);
                    } else if (exchange.getResponse().equals(ExchangeResponse.REJECTED)) {
                        response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    } else if (exchange.getResponse().equals(ExchangeResponse.WAITING)) {
                        response = new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
                    }
                }
            } else {
                response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            Exchange exchange = new Exchange(requester, responder);
            exchangeService.saveExchange(exchange);
            response = new ResponseEntity<>(HttpStatus.CREATED);
        }
        return response;
    }

    @RequestMapping(value="/exchangeResponse", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity exchangeResponse(@RequestBody String json) {
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
        JSONObject input = new JSONObject(json);
        return response;
    }

//    @RequestMapping(value="/exchange", method=RequestMethod.POST)
//    @ResponseBody
//    public ResponseEntity exchange(@RequestBody String json) {
//
//        // Unpack JSON and choose secondary contact
//
//        ResponseEntity<String> response;
//        JSONObject input = new JSONObject(json);
//        Long interacterId = input.getLong("interacter_id");
//        Long interacteeId = input.getLong("interactee_id");
//        JSONArray jsonContactIds = input.getJSONArray("contact_ids");
//        Player interacter = playerService.getPlayer(interacterId).get();
//        Player interactee = playerService.getPlayer(interacteeId).get();
//
//        // Get player contact
//
//        Long contactId = 0l;
//        if (jsonContactIds.length() == 0) {
//            contactId = 0l;
//        } else {
//            List<Long> contactIds = new ArrayList<>();
//            for (int i = 0; i < jsonContactIds.length(); i++) {
//                Long id = jsonContactIds.getJSONObject(i).getLong("contact_id");
//                if (id != interacteeId) contactIds.add(id);
//            }
//            if (contactIds.size() != 0) {
//                Random random = new Random();
//                contactId = contactIds.get(random.nextInt(contactIds.size()));
//            }
//        }
//
//        // Check for existing exchanges between these two players
//
//        Optional<Exchange> exchangeOptional1 = exchangeService.getExchangeByPlayers(interacter, interactee);
//        Optional<Exchange> exchangeOptional2 = exchangeService.getExchangeByPlayers(interactee, interacter);
//        boolean activeExchange1 = exchangeService.existsActiveExchangeByPlayers(interacter, interactee);
//        boolean activeExchange2 = exchangeService.existsActiveExchangeByPlayers(interactee, interacter);
//
//        // Use cases
//
//        if (activeExchange1) {
//            Exchange exchange1 = exchangeOptional1.get();
//            if (exchange1.isAccepted()) {
//            // The other player has accepted your request, complete the exchange
//                Long secondaryId = exchangeService.completeExchange(exchange1);
//                JSONObject output = new JSONObject();
//                output.put("secondary_id", secondaryId);
//                response = new ResponseEntity<>(output.toString(), HttpStatus.OK);
//            } else  {
//            // The other player hasn't accepted your request yet
//                if (exchangeService.isExpired(exchange1)) {
//                // If expired, fail request and 'complete' exchange
//                    Long ignore = exchangeService.completeExchange(exchange1);
//                    response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//                } else {
//                // Keep polling
//                    response = new ResponseEntity<>(HttpStatus.ACCEPTED);
//                }
//            }
//        } else if (activeExchange2) {
//            Exchange exchange2 = exchangeOptional2.get();
//            if (exchange2.isAccepted()) {
//                response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//            } else {
//            // They have already requested an exchange with you, you haven't accepted yet, accept
//                if (exchangeService.isExpired(exchange2)) {
//                    exchangeService.createExchange(interacter, interactee, contactId);
//                    response = new ResponseEntity<>(HttpStatus.CREATED);
//                } else {
//                    Long secondaryId = exchangeService.acceptExchange(exchange2, contactId);
//                    JSONObject output = new JSONObject();
//                    output.put("secondary_id", secondaryId);
//                    response = new ResponseEntity<>(output.toString(), HttpStatus.OK);
//                }
//            }
//        } else {
//            exchangeService.createExchange(interacter, interactee, contactId);
//            response = new ResponseEntity<>(HttpStatus.CREATED);
//        }
//        return response;
//    }

    @RequestMapping(value="/expose", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> expose(@RequestBody String json) {
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
//            ensure given target matches player's assign target and they haven't been exposed
            if(player.getTarget().getId().equals(target.getId())) {
                if(!player.isExposed() && !player.isReturnHome()) {
//                    increment rep for player
                    playerService.incrementReputation(player, 1);
//                    set other players with the same targets returnHome attribute
//                    assume player is locked to getNewTarget by app
                    List<Player> players = playerService.getAllPlayersByTarget(target);
                    for (Player p : players) {
                        if (!(p.getId().equals(player.getId()))){
                            p.setReturnHome(true);
                            playerService.savePlayer(p);
                        }
                    }
//                    set targets exposed attribute
                    target.setExposed(true);
                    playerService.savePlayer(target);
//                    set output elements
                    responseStatus = HttpStatus.OK;
                    output.put("SUCCESS", "Valid expose");
                } else { output.put("BAD_REQUEST", "Player has been exposed or player must return home"); }
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
            playerInfo.put("score", player.getReputation());
            leaderboard.put(playerInfo);
        }
        JSONObject output = new JSONObject();
        output.put("leaderboard", leaderboard);
        return output.toString();
    }

}