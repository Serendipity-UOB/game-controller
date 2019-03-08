package com.serendipity.gameController.control;

import com.serendipity.gameController.model.*;
import com.serendipity.gameController.service.beaconService.BeaconServiceImpl;
import com.serendipity.gameController.service.evidenceService.EvidenceServiceImpl;
import com.serendipity.gameController.service.exchangeService.ExchangeServiceImpl;
import com.serendipity.gameController.service.gameService.GameServiceImpl;
import com.serendipity.gameController.service.missionService.MissionServiceImpl;
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
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
    GameServiceImpl gameService;

    @Autowired
    EvidenceServiceImpl evidenceService;

    @Autowired
    MissionServiceImpl missionService;

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

    @RequestMapping(value="/startInfo", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> getStartInfo(@RequestBody String json) {
//        Read in request body
        JSONObject input = new JSONObject(json);
        Long playerId = input.getLong("player_id");
//        Create JSON object for response body
        JSONObject output = new JSONObject();
//        set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
//        Ensure player exists
        Optional<Player> opPlayer = playerService.getPlayer(playerId);
        if(opPlayer.isPresent()) {
//            Get two random players to gain intel on from mission
            Player player = opPlayer.get();
            List<Player> players = playerService.getAllPlayersExcept(player);
            Random random = new Random();
//            Ensure 1 other player exists
            if(players.size() > 0) {
                Player target1 = players.get(random.nextInt(players.size()));
                players.remove(target1);
//                Ensure 2 other players exist
                if (players.size() > 0) {
                    Player target2 = players.get(random.nextInt(players.size()));
//                    Ensure game exists
//                    TODO: Deal with multiple game instances
                    Optional<Game> opGame = gameService.getNextGame();
                    if(opGame.isPresent()) {
                        Game game = opGame.get();

//                        Find length of game in seconds
                        String datePattern = "HH:mm:ss";
                        DateTimeFormatter df = DateTimeFormatter.ofPattern(datePattern);
                        LocalTime gameEnd = game.getEndTime();
                        String endString = df.format(gameEnd);
                        LocalTime gameStart = game.getStartTime();
                        String startString = df.format(gameStart);
                        String[] unitsEnd = endString.split(":");
                        String[] unitsStart = startString.split(":");
                        int end = 3600 * Integer.parseInt(unitsEnd[0]) + 60 * Integer.parseInt(unitsEnd[1]) +
                                Integer.parseInt(unitsEnd[2]);
                        int start = 3600 * Integer.parseInt(unitsStart[0]) + 60 *
                                Integer.parseInt(unitsStart[1]) + Integer.parseInt(unitsStart[2]);

//                        Find upper and lower boundaries for mission time assignment
                        int quarter = (end - start) / 4;
                        int upper = end - quarter;
                        int lower = start + quarter;

//                        Pick random time
                        Random randomTime = new Random();
                        int time = quarter + randomTime.nextInt(upper - lower);
                        LocalTime missionStart = gameStart.plus(time, ChronoUnit.SECONDS);
                        LocalTime missionEnd = missionStart.plus(30, ChronoUnit.SECONDS);
//                        TODO: May need to make the player assignment more dynamic
//                        TODO: May need to consider multiple missions
//                        Save new mission
                        Mission mission = new Mission(missionStart, missionEnd, target1.getId(), target2.getId());
                        missionService.saveMission(mission);

//                        Assign mission to player
                        player.setMissionAssigned(mission.getId());
                        playerService.savePlayer(player);

//                        Return all players
                        responseStatus = HttpStatus.OK;
                        output.put("all_players", playerService.getAllPlayersStartInfo());
                    }
                }
            }

        } else { output.put("BAD_REQUEST", "Couldn't find player or target id given"); }

        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/atHomeBeacon", method=RequestMethod.POST, consumes="application/json")
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

    @RequestMapping(value="/playerUpdate", method=RequestMethod.POST , consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> playerUpdate(@RequestBody String json) {

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
        if (player.isReturnHome()) {
            output.put("req_new_target", true);
            player.setReturnHome(false);
            playerService.savePlayer(player);
        } else {
            output.put("req_new_target", false);
        }

        // Game over
        List<Game> games = gameService.getAllGamesByStartTimeAsc();
        if (gameService.isGameOver(games.get(0))) {
            output.put("game_over", true);
        } else {
            output.put("game_over", false);
        }

        // Mission
        Mission mission = missionService.getMission(player.getMissionAssigned()).get();
//        Find current time and mission start time in seconds
        String datePattern = "HH:mm:ss";
        DateTimeFormatter df = DateTimeFormatter.ofPattern(datePattern);
        String now = df.format(LocalTime.now());
        String start = df.format(mission.getStartTime());
        String[] unitsNow = now.split(":");
        String[] unitsStart = start.split(":");
        int nowSeconds = 3600 * Integer.parseInt(unitsNow[0]) + 60 * Integer.parseInt(unitsNow[1]) +
                Integer.parseInt(unitsNow[2]);
        int startSeconds = 3600 * Integer.parseInt(unitsStart[0]) + 60 * Integer.parseInt(unitsStart[1]) +
                Integer.parseInt(unitsStart[2]);
//        If mission should start
        if((startSeconds < nowSeconds) && !mission.getSent()){
            Player p1 = playerService.getPlayer(mission.getPlayer1()).get();
            Player p2 = playerService.getPlayer(mission.getPlayer2()).get();
            String missionDescription;
            missionDescription = "We have discovered that evidence about <b>" + p1.getRealName()
                            + "'s</b> and <b>" + p2.getRealName() + "'s</b>";

            List<Beacon> notHome = beaconService.getAllBeaconsExcept(player.getHomeBeacon());
            Random random = new Random();
            Beacon beacon = notHome.get(random.nextInt(notHome.size()));
            missionDescription += " activities can be found at <b>" + beacon.getName() +
                    "</b>.\n Get there in 30 Seconds to secure it.";

            mission.setSent(true);
            mission.setBeacon(beacon.getMajor());
            missionService.saveMission(mission);

            output.put("mission_description", missionDescription);
        }
        else{
            output.put("mission_description", "");
        }

        // Exchange pending
        Long requesterId = 0l;
        Optional<Exchange> optionalExchange = exchangeService.getMostRecentExchangeToPlayer(player);
        if (optionalExchange.isPresent()) {
            Exchange exchange = optionalExchange.get();
            if (exchangeService.getTimeRemaining(exchange) != 0l && !exchange.isRequestSent()) {
                requesterId = exchange.getRequestPlayer().getId();
                exchange.setRequestSent(true);
                exchangeService.saveExchange(exchange);
            }
        }
        output.put("exchange_pending", requesterId);

        return new ResponseEntity<>(output.toString(), HttpStatus.OK);
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

        // Handle JSON
        JSONObject input = new JSONObject(json);
        Long requesterId = input.getLong("requester_id");
        Long responderId = input.getLong("responder_id");
        JSONArray jsonContactIds = input.getJSONArray("contact_ids");
        Player requester = playerService.getPlayer(requesterId).get();
        Player responder = playerService.getPlayer(responderId).get();

        // Find exchange
        Optional<Exchange> optionalExchange = exchangeService.getMostRecentExchangeFromPlayer(requester);
        if (optionalExchange.isPresent()) {
            Exchange exchange = optionalExchange.get();

            // Handle request
            if (exchange.getResponsePlayer() == responder) {
                if (exchange.isRequesterToldComplete()) {
                    exchangeService.createExchange(requester, responder, jsonContactIds);
                    response = new ResponseEntity<>(HttpStatus.CREATED);
                }
                else if (exchange.getResponse().equals(ExchangeResponse.ACCEPTED)) {
                    List<Evidence> evidenceList = exchangeService.getMyEvidence(exchange, requester);
                    JSONObject output = new JSONObject();
                    output.put("evidence", evidenceService.evidenceListToJsonArray(evidenceList));
                    exchange.setRequesterToldComplete(true);
                    exchangeService.saveExchange(exchange);
                    response = new ResponseEntity<>(output.toString(), HttpStatus.ACCEPTED);
                } else if (exchangeService.getTimeRemaining(exchange) <= 0l) {
                    exchange.setRequesterToldComplete(true);
                    exchangeService.saveExchange(exchange);
                    response = new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
                } else {
                    if (exchange.getResponse().equals(ExchangeResponse.REJECTED)) {
                        exchange.setRequesterToldComplete(true);
                        exchangeService.saveExchange(exchange);
                        response = new ResponseEntity<>(HttpStatus.NO_CONTENT);
                    } else if (exchange.getResponse().equals(ExchangeResponse.WAITING)) {
                        response = new ResponseEntity<>(HttpStatus.PARTIAL_CONTENT);
                    }
                }
            } else {
                response = new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            exchangeService.createExchange(requester, responder, jsonContactIds);
            response = new ResponseEntity<>(HttpStatus.CREATED);
        }
        return response;
    }

    @RequestMapping(value="/exchangeResponse", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity exchangeResponse(@RequestBody String json) {
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        // Handle JSON
        JSONObject input = new JSONObject(json);
        Long requesterId = input.getLong("requester_id");
        Long responderId = input.getLong("responder_id");
        int exchangeResponseIndex = input.getInt("response");
        ExchangeResponse exchangeResponse = ExchangeResponse.values()[exchangeResponseIndex];
        JSONArray jsonContactIds = input.getJSONArray("contact_ids");
        Player requester = playerService.getPlayer(requesterId).get();
        Player responder = playerService.getPlayer(responderId).get();

        // Find exchange
        Optional<Exchange> optionalExchange = exchangeService.getExchangeByPlayers(requester, responder);
        if (optionalExchange.isPresent()) {
            Exchange exchange = optionalExchange.get();

            // Handle response
            if (exchangeResponse.equals(ExchangeResponse.WAITING)) {
                long timeRemainingBuffer = 1l;
                long timeRemaining = exchangeService.getTimeRemaining(exchange) - timeRemainingBuffer;
                if (timeRemaining <= 0l) {
                    response = new ResponseEntity<>(HttpStatus.REQUEST_TIMEOUT);
                } else {
                    JSONObject output = new JSONObject();
                    // 1s buffer for timeout, to avoid race conditions
                    output.put("time_remaining", timeRemaining);
                    response = new ResponseEntity<>(output.toString(), HttpStatus.PARTIAL_CONTENT);
                }
            } else if (exchangeResponse.equals(ExchangeResponse.ACCEPTED)) {
                List<Long> contactIds = new ArrayList<>();
                if (jsonContactIds.length() > 0) {
                    for (int i = 0; i < jsonContactIds.length(); i++) {
                        Long id = jsonContactIds.getJSONObject(i).getLong("contact_id");
                        contactIds.add(id);
                    }
                }
                List<Evidence> requestEvidenceList = exchangeService.getMyEvidence(exchange, responder);
                JSONObject output = new JSONObject();
                output.put("evidence", evidenceService.evidenceListToJsonArray(requestEvidenceList));
                List<Evidence> responseEvidenceList = exchangeService.calculateEvidence(exchange, responder, contactIds);
                exchange.setEvidenceList(responseEvidenceList);
                exchange.setResponse(exchangeResponse);
                exchangeService.saveExchange(exchange);
                // Set status code
                response = new ResponseEntity<>(output.toString(), HttpStatus.ACCEPTED);
            } else if (exchangeResponse.equals(ExchangeResponse.REJECTED)) {
                exchange.setResponse(exchangeResponse);
                exchangeService.saveExchange(exchange);
                response = new ResponseEntity<>(HttpStatus.RESET_CONTENT);
            }
        }
        return response;
    }

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
//                    increment reputation for player
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
                } else { output.put("BAD_REQUEST", "Player has been exposed or player must return home"); }
            } else { output.put("BAD_REQUEST", "Target Id given doesn't match player's assigned Target"); }
        } else { output.put("BAD_REQUEST", "Couldn't find player or target id given"); }
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/missionUpdate", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> missionUpdate(@RequestBody String json) {
//        receive JSON object
        JSONObject input = new JSONObject(json);
        Long playerId = input.getLong("player_id");

//        Create JSON object for response body
        JSONObject output = new JSONObject();
//        set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

//        Check player exists
        Optional<Player> opPlayer = playerService.getPlayer(playerId);
        if(opPlayer.isPresent()){
            Player player = opPlayer.get();
            int location = player.getNearestBeaconMajor();
            Mission mission = missionService.getMission(player.getMissionAssigned()).get();
//            Find current time and mission end time in seconds
            String datePattern = "HH:mm:ss";
            DateTimeFormatter df = DateTimeFormatter.ofPattern(datePattern);
            String now = df.format(LocalTime.now());
            String end = df.format(mission.getEndTime());
            String[] unitsNow = now.split(":");
            String[] unitsEnd = end.split(":");
            int nowSeconds = 3600 * Integer.parseInt(unitsNow[0]) + 60 * Integer.parseInt(unitsNow[1]) +
                    Integer.parseInt(unitsNow[2]);
            int endSeconds = 3600 * Integer.parseInt(unitsEnd[0]) + 60 * Integer.parseInt(unitsEnd[1]) +
                    Integer.parseInt(unitsEnd[2]);
//            Check if the mission hasn't timed out
            if((nowSeconds + 1) < endSeconds){
                if(location == mission.getBeacon()){
                    JSONArray evidence = new JSONArray();
                    JSONObject p1 = new JSONObject();
                    p1.put("player_id", mission.getPlayer1());
                    p1.put("amount", 10);
                    evidence.put(p1);
                    JSONObject p2 = new JSONObject();
                    p2.put("player_id", mission.getPlayer2());
                    p2.put("amount", 10);
                    evidence.put(p2);
                    output.put("evidence", evidence);
                } else {
                    LocalTime timeRemaining = mission.getEndTime().minus(nowSeconds, ChronoUnit.SECONDS);
                    System.out.println(timeRemaining);
                    output.put("time_remaining", timeRemaining);
                    responseStatus = HttpStatus.PARTIAL_CONTENT;
                }
            } else {
                output.put("NO_CONTENT", "Mission failed");
                responseStatus = HttpStatus.NO_CONTENT;
            }
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
            playerInfo.put("position", playerService.getLeaderboardPosition(player));
            playerInfo.put("score", player.getReputation());
            leaderboard.put(playerInfo);
        }
        JSONObject output = new JSONObject();
        output.put("leaderboard", leaderboard);
        return output.toString();
    }

}