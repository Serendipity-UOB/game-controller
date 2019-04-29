package com.serendipity.gameController.control;

import com.serendipity.gameController.model.*;
import com.serendipity.gameController.service.beaconService.BeaconServiceImpl;
import com.serendipity.gameController.service.evidenceService.EvidenceServiceImpl;
import com.serendipity.gameController.service.exchangeService.ExchangeServiceImpl;
import com.serendipity.gameController.service.exposeService.ExposeServiceImpl;
import com.serendipity.gameController.service.gameService.GameServiceImpl;
import com.serendipity.gameController.service.interceptService.InterceptServiceImpl;
import com.serendipity.gameController.service.logService.LogServiceImpl;
import com.serendipity.gameController.service.missionService.MissionServiceImpl;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import com.serendipity.gameController.service.zoneService.ZoneServiceImpl;

import org.apache.tomcat.jni.Local;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Null;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import static java.time.temporal.ChronoUnit.SECONDS;

@Controller
@CrossOrigin
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

    @Autowired
    ZoneServiceImpl zoneService;

    @Autowired
    InterceptServiceImpl interceptService;

    @Autowired
    LogServiceImpl logService;

    @Autowired
    ExposeServiceImpl exposeService;

    @RequestMapping(value="/registerPlayer", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> registerPlayer(@RequestBody String json) {
        ResponseEntity<String> response;
        JSONObject input = new JSONObject(json);
        System.out.println("/registerPlayer received: " + input);
        String realName = input.getString("real_name");
        String codeName = input.getString("code_name");

        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        Optional<Game> optionalNextGame = gameService.getNextGame();
        if (!optionalNextGame.isPresent()) {
            responseStatus = HttpStatus.NO_CONTENT;
            output.put("error", "No game");
        } else if (!playerService.isValidRealNameAndCodeName(realName, codeName)) {
            output.put("error", "Code name is taken");
        } else {
            Player player = new Player(realName, codeName);
            playerService.savePlayer(player);
            output.put("player_id", player.getId());
            responseStatus = HttpStatus.OK;
        }
        logService.printToCSV(new ArrayList<>(Arrays.asList(realName, LocalTime.now().toString(),
                responseStatus.toString(), input.toString(), output.toString())), "registerPlayer.csv");
        System.out.println("/registerPlayer returned: " + output);
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/gameInfo", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> getGameInfo(){
        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.OK;

        Optional<Game> optionalNextGame = gameService.getNextGame();
        if (!optionalNextGame.isPresent()) {
            responseStatus =  HttpStatus.NO_CONTENT;
            output.put("error", "No game");
        } else {
            Game nextGame = optionalNextGame.get();
            output.put("start_time", nextGame.getStartTime());
            output.put("number_players", playerService.countAllPlayers());
        }
        System.out.println("/gameInfo returned: " + output);
        logService.printToCSV(new ArrayList<>(Arrays.asList(LocalTime.now().toString(),
                responseStatus.toString(), output.toString())), "gameInfo.csv");
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/joinGame", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> joinGame(@RequestBody String json) {
        // Handle JSON
        JSONObject input = new JSONObject(json);
        System.out.println("/joinGame received: " + input);
        Long id = input.getLong("player_id");
        String realName = "";
        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        // Look for player
        Optional<Player> optionalPlayer = playerService.getPlayer(id);
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            realName = player.getRealName();
            // Assign zone, if one exists
            Optional<Zone> optionalZone = zoneService.chooseHomeZone(player);
            if (optionalZone.isPresent()) {
                Zone zone = optionalZone.get();
                player.setHomeZone(zone);
                playerService.savePlayer(player);
                // Handle response
                output.put("home_zone_name", zone.getName());
                responseStatus = HttpStatus.OK;
            } else System.out.println("No zones in the database");
        } else System.out.println("No player exists by this id");
        System.out.println("/joinGame returned: " + output);
        logService.printToCSV(new ArrayList<>(Arrays.asList(id.toString(), realName, LocalTime.now().toString(),
                responseStatus.toString(), input.toString(), output.toString())), "joinGame.csv");
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/startInfo", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> getStartInfo(@RequestBody String json) {
        // Read in request body
        JSONObject input = new JSONObject(json);
        System.out.println("/startInfo received: " + input);
        Long playerId = input.getLong("player_id");
        String realName = "";
        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        // Ensure player exists
        Optional<Player> opPlayer = playerService.getPlayer(playerId);
        if (opPlayer.isPresent()){
            Player player = opPlayer.get();
            realName = player.getRealName();
            List<Game> games = gameService.getAllGames();
            // TODO: Deal with multiple game instances
            if(games.size() > 0) {
                // Ensure game exists
                Game game = gameService.getAllGames().get(0);
                // Setup zone dispersion variables
                player.setCurrentZone(player.getHomeZone());
                player.setTimeEnteredZone(LocalTime.now());
                // Assign initial Mission
                Optional<Mission> opMission = missionService.createMission(player);
                if(opMission.isPresent()){
                    // Set mission parameters
                    Mission mission = opMission.get();
                    mission.setStartTime(game.getStartTime());
                    mission.setEndTime(game.getEndTime());
                    // Assign random zone
                    List<Zone> zones = zoneService.getAllZonesExceptUN();
                    Random random = new Random();
                    mission.setZone(zones.get(random.nextInt(zones.size())));
                    mission.setStart(true);
                    missionService.saveMission(mission);
                    player.setMissionAssigned(mission);
                    // Assign new target
                    Long targetId = playerService.newTarget(player.getId());
                    Optional<Player> opTarget = playerService.getPlayer(targetId);
                    if(opTarget.isPresent()){
                        Player target = opTarget.get();
                        player.setTarget(target);
                        playerService.savePlayer(player);
                        // Return all players
                        responseStatus = HttpStatus.OK;
                        output.put("all_players", playerService.getAllPlayersStartInfo());
                        // Return first target
                        output.put("first_target_id", target.getId());
                        // Return game endTime
                        output.put("end_time", game.getEndTime());
                    } else {
                        System.out.println("Unable to assign target");
                        output.put("BAD_REQUEST", "Unable to assign target");
                    }
                } else {
                    System.out.println("Not enough players");
                    output.put("BAD_REQUEST", "Not enough players");
                }
            } else {
                System.out.println("No game");
                responseStatus = HttpStatus.NO_CONTENT;
                output.put("NO_CONTENT", "No game");
            }
        } else {
            System.out.println("No player exists by this id");
            output.put("BAD_REQUEST", "Couldn't find player given");
        }
        System.out.println("/startInfo returned: " + output);
        logService.printToCSV(new ArrayList<>(Arrays.asList(playerId.toString(), realName, LocalTime.now().toString(),
                responseStatus.toString(), input.toString(), output.toString())), "startInfo.csv");
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/atHomeBeacon", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> atHomeBeacon(@RequestBody String json) {
        // Handle json
        JSONObject input = new JSONObject(json);
        System.out.println("/atHomeBeacon received: " + input);
        Long id = input.getLong("player_id");
        String realName = "";
        JSONArray beacons = input.getJSONArray("beacons");

        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        // Look for player
        Optional<Player> optionalPlayer = playerService.getPlayer(id);
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            realName = player.getRealName();
            if (player.hasHomeZone()) {
                Optional<Zone> optionalZone = zoneService.calculateCurrentZone(player, beacons);
                if (optionalZone.isPresent()) {
                    Zone currentZone = optionalZone.get();
                    player.setCurrentZone(currentZone);
                    Zone homeZone = player.getHomeZone();
                    if (currentZone.equals(homeZone)) {
                        output.put("home", true);
                        player.setMissionsPaused(false);
                        playerService.savePlayer(player);
                    } else {
                        output.put("home", false);
                    }
                    responseStatus = HttpStatus.OK;
                } else {
                    System.out.println("Couldn't calculate this player's current zone");
                    output.put("home", false);
                    responseStatus = HttpStatus.OK;
                }
            } else System.out.println("This player hasn't been assigned a home zone");
        } else System.out.println("No player exists by this id");
        System.out.println("/atHomeBeacon returned: " + output);
        logService.printToCSV(new ArrayList<>(Arrays.asList(id.toString(), realName, LocalTime.now().toString(),
                responseStatus.toString(), input.toString(), output.toString())), "atHomeBeacon.csv");
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/playerUpdate", method=RequestMethod.POST , consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> playerUpdate(@RequestBody String json) {

        // Handle json
        JSONObject input = new JSONObject(json);
        System.out.println("/playerUpdate received: " + input);
        Long id = input.getLong("player_id");
        String realName = "";
        JSONArray jsonBeacons = input.getJSONArray("beacons");

        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        // Does player exist
        Optional<Player> optionalPlayer = playerService.getPlayer(id);
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            realName = player.getRealName();
            // Nearby players
            List<JSONObject> nearbyPlayers = new ArrayList<>();
            // Far away players
            List<JSONObject> farPlayers = new ArrayList<>();
            Optional<Zone> optionalZone = zoneService.calculateCurrentZone(player, jsonBeacons);
            if (optionalZone.isPresent()) {
                Zone zone = optionalZone.get();
                if(!zone.equals(player.getCurrentZone())){
                    player.setCurrentZone(zone);
                    player.setTimeEnteredZone(LocalTime.now());
                    playerService.savePlayer(player);
                }
                nearbyPlayers = playerService.getNearbyPlayers(player);
                farPlayers = playerService.getFarPlayers(player);
            } else System.out.println("Couldn't calculate the player's current zone");
            output.put("nearby_players", nearbyPlayers);
            output.put("far_players", farPlayers);

            // Location
            int location = zoneService.locationMapping(player.getCurrentZone());
            output.put("location", location);

            // Reputation
            output.put("reputation", player.getReputation());

            // Position
            output.put("position", playerService.getLeaderboardPosition(player));

            // Exposed
            if (player.getExposedBy() != 0l) {
                output.put("exposed_by", player.getExposedBy());
                player.setExposedBy(0l);
                playerService.savePlayer(player);
            } else {
                output.put("exposed_by", 0l);
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

            // Exchange pending
            Long requesterId = 0l;
            Optional<Exchange> optionalExchange = exchangeService.getNextExchangeToPlayer(player);
            if (optionalExchange.isPresent()) {
                Exchange exchange = optionalExchange.get();
                if (!exchange.isRequestSent()) {
                    requesterId = exchange.getRequestPlayer().getId();
                    exchange.setRequestSent(true);
                    exchangeService.saveExchange(exchange);
                }
            }
            output.put("exchange_pending", requesterId);

            // Dispersion of players
            if(player.getTimeEnteredZone().plusSeconds(30).isBefore(LocalTime.now()) && !player.getCurrentZone().getName().equals("UN")
                && !player.isMissionsPaused()){
                // See if previous mission has been completed
                if(player.getMissionAssigned().isCompleted()) {
                    // Assign mission
                    Optional<Mission> opMission = missionService.createMission(player);
                    if (opMission.isPresent()) {
                        // Set mission parameters
                        Mission mission = opMission.get();
                        mission.setStartTime(LocalTime.now());
                        // 30 seconds to complete mission, extra second for polling delay
                        mission.setEndTime(LocalTime.now().plusSeconds(31));
                        // Assign random zone
                        List<Zone> zones = zoneService.getAllZonesExceptUNandOne(player.getCurrentZone().getId());
//                        List<Zone> zones = zoneService.getAllZonesExcept(player.getCurrentZone().getId());
                        Random random = new Random();
                        mission.setZone(zones.get(random.nextInt(zones.size())));
                        missionService.saveMission(mission);
                        player.setMissionAssigned(mission);
                        playerService.savePlayer(player);
                    }
                }
            }

            // Mission
            Optional<Mission> opMission = missionService.getMission(player.getMissionAssigned().getId());
            if( opMission.isPresent() ){
                Mission mission = opMission.get();
                // If mission should start
                if (!mission.isSent()) {
                    Player p1 = mission.getPlayer1();
                    Player p2 = mission.getPlayer2();
                    String missionDescription;
                    Zone loc = mission.getZone();
                    if(mission.isStart()){
                        missionDescription = "Welcome, Agent.\nGo to " + loc.getName() +
                                " for a hint on your target's location.";
                    } else {
                        missionDescription = "Evidence available on " + p1.getRealName()
                                + " and " + p2.getRealName() + ".\nGo to " + loc.getName() + ".";
                    }
                    mission.setSent(true);
                    missionService.saveMission(mission);
                    output.put("mission_description", missionDescription);
                } else { output.put("mission_description", ""); }
            } else {
                System.out.println("This player hasn't had a mission assigned to them");
                output.put("mission_description", "");
            }

            responseStatus = HttpStatus.OK;

        } else System.out.println("No player exists by this id");
        System.out.println("/playerUpdate returned: " + output);
        logService.printToCSV(new ArrayList<>(Arrays.asList(id.toString(), realName, LocalTime.now().toString(),
                responseStatus.toString(), input.toString(), output.toString())), "playerUpdate.csv");
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/newTarget", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> getNewTarget(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        System.out.println("/newTarget received: " + input);
        Long playerId = input.getLong("player_id");
        String realName = "";
        Optional<Player> optionalPlayer = playerService.getPlayer(playerId);
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            realName = player.getRealName();
        }

        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        Long newTargetId = playerService.newTarget(playerId);
        output.put("target_player_id", newTargetId);
        responseStatus = HttpStatus.OK;
        System.out.println("/newTarget returned: " + output);
        logService.printToCSV(new ArrayList<>(Arrays.asList(playerId.toString(), realName, LocalTime.now().toString(),
                responseStatus.toString(), input.toString(), output.toString())), "newTarget.csv");
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/exchangeRequest", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> exchangeRequest(@RequestBody String json) {
        // Handle JSON
        JSONObject input = new JSONObject(json);
        System.out.println("/exchangeRequest received: " + input);
        Long requesterId = input.getLong("requester_id");
        Long responderId = input.getLong("responder_id");
        Long exchangeId = 0L;
        String requesterName = "";
        String responderName = "";

        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        JSONArray jsonContactIds = input.getJSONArray("contact_ids");

        Optional<Player> optionalRequester = playerService.getPlayer(requesterId);
        Optional<Player> optionalResponder = playerService.getPlayer(responderId);

        // Find players
        if (optionalRequester.isPresent() && optionalResponder.isPresent()) {
            Player requester = optionalRequester.get();
            requesterName = requester.getRealName();
            Player responder = optionalResponder.get();
            responderName = responder.getRealName();
            // Find exchange
            Optional<Exchange> optionalExchange = exchangeService.getMostRecentExchangeFromPlayer(requester);
            if (optionalExchange.isPresent()) {
                Exchange exchange = optionalExchange.get();
                exchangeId = exchange.getId();
                // Handle request
                if (exchange.getResponsePlayer() == responder) {
                    if (exchange.isRequesterToldComplete()) {
                        exchangeId = exchangeService.createExchange(requester, responder, jsonContactIds);
                        output.put("CREATED", "Creating exchange");
                        responseStatus = HttpStatus.CREATED;
                    } else if (exchange.getResponse().equals(ExchangeResponse.ACCEPTED)) {
                        List<Evidence> evidenceList = exchangeService.getMyEvidence(exchange, requester);
                        output.put("evidence", evidenceService.evidenceListToJsonArray(evidenceList));
                        exchange.setRequesterToldComplete(true);
                        exchangeService.saveExchange(exchange);
                        // Add exchange to logs
                        logService.saveLog(LogType.EXCHANGE, exchange.getId(), LocalTime.now(), requester.getCurrentZone());
                        output.put("ACCEPTED", "Exchange accepted");
                        responseStatus = HttpStatus.ACCEPTED;
                    } else if (exchangeService.getTimeRemaining(exchange) <= 0l) {
                        exchange.setRequesterToldComplete(true);
                        exchangeService.saveExchange(exchange);
                        output.put("REQUEST_TIMEOUT", "Exchange timeout");
                        responseStatus = HttpStatus.REQUEST_TIMEOUT;
                    } else {
                        if (exchange.getResponse().equals(ExchangeResponse.REJECTED)) {
                            exchange.setRequesterToldComplete(true);
                            exchangeService.saveExchange(exchange);
                            output.put("NO_CONTENT", "Exchange rejected");
                            responseStatus = HttpStatus.NO_CONTENT;
                        } else if (exchange.getResponse().equals(ExchangeResponse.WAITING)) {
                            output.put("PARTIAL_CONTENT", "Waiting for response");
                            responseStatus = HttpStatus.PARTIAL_CONTENT;
                        } else {
                            System.out.println("Something has gone very wrong to get to here");
                            output.put("UNKNOWN", "Something has gone very wrong to get to here");
                        }
                    }
                } else if (exchangeService.getTimeRemaining(exchange) <= 0l || exchange.isRequesterToldComplete()) {
                    System.out.println("Creating exchange");
                    exchangeId = exchangeService.createExchange(requester, responder, jsonContactIds);
                    output.put("CREATED", "Creating exchange");
                    responseStatus = HttpStatus.CREATED;
                } else {
                    System.out.println("Player already requesting exchange with someone else");
                    output.put("NOT_FOUND", "Player already requesting exchange with someone else");
                    responseStatus = HttpStatus.NOT_FOUND;
                }
            } else {
                System.out.println("No exchange exists from this player, creating an exchange");
                output.put("CREATED", "No exchange exists from this player, creating an exchange");
                exchangeId = exchangeService.createExchange(requester, responder, jsonContactIds);
                responseStatus = HttpStatus.CREATED;
            }
        } else {
            System.out.println("Couldn't find either the requester or the responder by these ids");
            output.put("BAD_REQUEST", "Couldn't find either the requester or the responder by these ids");
        }
        System.out.println("/exchangeRequest returned: " + output);
        logService.printToCSV(new ArrayList<>(Arrays.asList(exchangeId.toString(), requesterName, responderName, LocalTime.now().toString(),
                responseStatus.toString(), input.toString(), output.toString())), "exchangeRequest.csv");
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/exchangeResponse", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> exchangeResponse(@RequestBody String json) {
        // Handle JSON
        JSONObject input = new JSONObject(json);
        System.out.println("/exchangeResponse received: " + input);
        Long requesterId = input.getLong("requester_id");
        Long responderId = input.getLong("responder_id");
        Long exchangeId = 0L;
        String requesterName = "";
        String responderName = "";


        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        int exchangeResponseIndex = input.getInt("response");
        ExchangeResponse exchangeResponse = ExchangeResponse.values()[exchangeResponseIndex];
        JSONArray jsonContactIds = input.getJSONArray("contact_ids");
        Optional<Player> optionalRequester = playerService.getPlayer(requesterId);
        Optional<Player> optionalResponder = playerService.getPlayer(responderId);

        // Find players
        if (optionalRequester.isPresent() && optionalResponder.isPresent()) {
            Player requester = optionalRequester.get();
            requesterName = requester.getRealName();
            Player responder = optionalResponder.get();
            responderName = responder.getRealName();

            // Find exchange
            Optional<Exchange> optionalExchange = exchangeService.getExchangeByPlayers(requester, responder);
            if (optionalExchange.isPresent()) {
                Exchange exchange = optionalExchange.get();
                exchangeId = exchange.getId();
                // Handle response
                if (exchangeResponse.equals(ExchangeResponse.WAITING)) {
                    long timeRemainingBuffer = 1l;
                    long timeRemaining = exchangeService.getTimeRemaining(exchange) - timeRemainingBuffer;
                    if (timeRemaining <= 0l) {
                        output.put("REQUEST_TIMEOUT", "Exchange timeout");
                        responseStatus = HttpStatus.REQUEST_TIMEOUT;
                    } else {
                        // 1s buffer for timeout, to avoid race conditions
                        output.put("time_remaining", timeRemaining);
                        responseStatus = HttpStatus.PARTIAL_CONTENT;
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
                    output.put("evidence", evidenceService.evidenceListToJsonArray(requestEvidenceList));
                    List<Evidence> responseEvidenceList = exchangeService.calculateEvidence(exchange, responder, contactIds);
                    exchange.setEvidenceList(responseEvidenceList);
                    exchange.setResponse(exchangeResponse);
                    exchangeService.saveExchange(exchange);
                    output.put("ACCEPTED", "Exchange accepted");
                    // Set status code
                    responseStatus = HttpStatus.ACCEPTED;
                } else if (exchangeResponse.equals(ExchangeResponse.REJECTED)) {
                    exchange.setResponse(exchangeResponse);
                    exchangeService.saveExchange(exchange);
                    output.put("RESET_CONTENT", "Exchange rejected");
                    responseStatus = HttpStatus.RESET_CONTENT;
                } else {
                    System.out.println("Exchange has no response status, something wrong on the server");
                    output.put("BAD_REQUEST", "Exchange has no response status, something wrong on the server");
                }
            } else {
                System.out.println("Couldn't find an exchange between these players");
                output.put("BAD_REQUEST", "Couldn't find an exchange between these players");
            }
        } else {
            System.out.println("Couldn't find either the requester or the responder by these ids");
            output.put("BAD_REQUEST", "Couldn't find either the requester or the responder by these ids");
        }
        System.out.println("/exchangeResponse returned: " + output);
        logService.printToCSV(new ArrayList<>(Arrays.asList(exchangeId.toString(), requesterName, responderName, LocalTime.now().toString(),
                responseStatus.toString(), input.toString(), output.toString())), "exchangeResponse.csv");
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/expose", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<String> expose(@RequestBody String json) {
        // receive JSON object
        JSONObject input = new JSONObject(json);
        System.out.println("/expose received: " + input);
        Long playerId = input.getLong("player_id");
        Long targetId = input.getLong("target_id");
        String realName = "";
        Long exposeId = 0L;
        // create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        // fetch player making request and target given
        Optional<Player> opPlayer = playerService.getPlayer(playerId);
        Optional<Player> opTarget = playerService.getPlayer(targetId);
        // ensure optionals have a value
        if (opPlayer.isPresent() && opTarget.isPresent()) {
            // unpack optional objects
            Player player = opPlayer.get();
            realName = player.getRealName();
            Player target = opTarget.get();
            // ensure given target matches player's assign target and they haven't been exposed
            if(player.getTarget().getId().equals(target.getId())) {
                if((player.getExposedBy() == 0l) && (!player.isReturnHome())) {
                    // increment reputation for player
                    int reputationGain = playerService.calculateReputationGainFromExpose();
                    playerService.incrementReputation(player, reputationGain);
                    output.put("reputation", reputationGain);
                    // set other players with the same targets returnHome attribute
                    // assume player is locked to getNewTarget by app
                    List<Player> players = playerService.getAllPlayersByTarget(target);
                    for (Player p : players) {
                        if (!(p.getId().equals(player.getId()))){
                            p.setReturnHome(true);
                            p.setMissionsPaused(true);
                            playerService.savePlayer(p);
                        }
                    }
                    // Pause players missions
                    player.setMissionsPaused(true);
                    playerService.savePlayer(player);
                    // set targets exposed attribute
                    target.setExposedBy(playerId);
                    target.setMissionsPaused(true);
                    playerService.savePlayer(target);
                    // Create expose
                    Expose expose = new Expose(player, target);
                    exposeService.saveExpose(expose);
                    exposeId = expose.getId();
                    // Add expose to logs
                    logService.saveLog(LogType.EXPOSE, expose.getId(), LocalTime.now(), player.getCurrentZone());
                    // set output elements
                    responseStatus = HttpStatus.OK;
                } else {
                    System.out.println("You have been exposed or must return home");
                    output.put("BAD_REQUEST", "Player has been exposed or player must return home");
                }
            } else {
                System.out.println("This player isn't your target");
                output.put("BAD_REQUEST", "Target Id given doesn't match player's assigned Target");
            }
        } else {
            System.out.println("Couldn't find one of the exposer or the target");
            output.put("BAD_REQUEST", "Couldn't find player or target id given");
        }
        System.out.println("/expose returned: " + output);
        logService.printToCSV(new ArrayList<>(Arrays.asList(exposeId.toString(), realName, LocalTime.now().toString(),
                responseStatus.toString(), input.toString(), output.toString())), "expose.csv");
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/intercept", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> intercept(@RequestBody String json) {
        // Read in request body
        JSONObject input = new JSONObject(json);
        System.out.println("/intercept received: " + input);
        Long playerId = input.getLong("player_id");
        Long targetId = input.getLong("target_id");
        String realName = "";
        Long interceptId = 0L;
        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        // Ensure player exists
        Optional<Player> opPlayer = playerService.getPlayer(playerId);
        Optional<Player> opTarget = playerService.getPlayer(targetId);
        if (opPlayer.isPresent() && opTarget.isPresent()) {
            Player player = opPlayer.get();
            realName = player.getRealName();
            Player target = opTarget.get();
            // Find intercept if it exists
            Optional<Intercept> opIntercept = interceptService.getInterceptByPlayer(player);
            if (opIntercept.isPresent()) {
                Intercept intercept = opIntercept.get();
                // Check if the correct target has been sent
                if (target.equals(intercept.getTarget())) {
                    // Check if intercept has expired
                    if (!intercept.isExpired()) {
                        // Check if intercept should expire
                        // Check 9 second after as .isBefore doesn't cover is equal
                        if (LocalTime.now().isBefore(intercept.getStartTime().plusSeconds(10))) {
                            // Find if exchange has been set
                            if (intercept.getExchange() == null) {
                                // Set exchange if exists
                                Optional<Exchange> opExchange = exchangeService.getEarliestActiveExchange(target);
                                if (opExchange.isPresent()) {
                                    Exchange exchange = opExchange.get();
                                    if (!exchange.getResponsePlayer().equals(player)) {
                                        intercept.setExchange(exchange);
                                        interceptService.saveIntercept(intercept);
                                        System.out.println("Exchange set");
                                        output.put("PARTIAL_CONTENT", "Exchange set");
                                        responseStatus = HttpStatus.PARTIAL_CONTENT;
                                    } else {
                                        System.out.println("No exchange found");
                                        output.put("PARTIAL_CONTENT", "No exchange found");
                                        responseStatus = HttpStatus.PARTIAL_CONTENT;
                                    }
                                } else {
                                    System.out.println("No exchange found");
                                    output.put("PARTIAL_CONTENT", "No exchange found");
                                    responseStatus = HttpStatus.PARTIAL_CONTENT;
                                }
                            } else {
                                System.out.println("Waiting for intercept window");
                                output.put("PARTIAL_CONTENT", "Waiting for intercept window");
                                responseStatus = HttpStatus.PARTIAL_CONTENT;
                            }
                        } else {
                            intercept.setExpired(true);
                            interceptService.saveIntercept(intercept);
                            Exchange exchange = intercept.getExchange();
                            // Determine response
                            if (exchange != null && exchange.getResponse().equals(ExchangeResponse.ACCEPTED)) {
                                JSONArray evidence = new JSONArray();
                                JSONObject p1 = new JSONObject();
                                p1.put("player_id", exchange.getRequestPlayer().getId());
                                p1.put("amount", 30);

                                JSONObject p2 = new JSONObject();
                                p2.put("player_id", exchange.getResponsePlayer().getId());
                                p2.put("amount", 10);

                                evidence.put(p1);
                                evidence.put(p2);
                                output.put("evidence", evidence);
                                // Add expose to logs
                                logService.saveLog(LogType.INTERCEPT, intercept.getId(), LocalTime.now(), player.getCurrentZone());
                                responseStatus = HttpStatus.OK;
                            } else {
                                System.out.println("Exchange wasn't accepted");
                                output.put("NO_CONTENT", "Exchange wasn't accepted");
                                responseStatus = HttpStatus.NO_CONTENT;
                            }
                        }
                    } else {
                        System.out.println("Intercept has expired, creating a new one");
                        // Overwrite expired intercept
                        intercept.setExpired(false);
                        intercept.setTarget(target);
                        intercept.setExchange(null);
                        intercept.setStartTime(LocalTime.now());
                        interceptService.saveIntercept(intercept);
                        output.put("CREATED", "Intercept has expired, creating a new one");
                        responseStatus = HttpStatus.CREATED;
                    }
                } else {
                    System.out.println("Active intercept exists for another target");
                    output.put("NOT_FOUND", "Active intercept exists for another target");
                    responseStatus = HttpStatus.NOT_FOUND;
                }
            } else {
                System.out.println("No intercept exists, creating one");
                // Create intercept
                Intercept intercept = new Intercept(player, target, LocalTime.now());
                interceptService.saveIntercept(intercept);
                output.put("CREATED", "No intercept exists, creating one");
                responseStatus = HttpStatus.CREATED;
            }
        }
        System.out.println("/intercept returned: " + output);
        logService.printToCSV(new ArrayList<>(Arrays.asList(interceptId.toString(), realName, LocalTime.now().toString(),
                responseStatus.toString(), input.toString(), output.toString())), "intercept.csv");
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/missionUpdate", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> missionUpdate(@RequestBody String json) {
        // receive JSON object
        JSONObject input = new JSONObject(json);
        System.out.println("/missionUpdate received: " + input);
        Long playerId = input.getLong("player_id");
        String realName = "";

        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        // Check player exists
        Optional<Player> opPlayer = playerService.getPlayer(playerId);
        if (opPlayer.isPresent()) {
            Player player = opPlayer.get();
            realName = player.getRealName();
            Zone location = player.getCurrentZone();
            Mission mission = player.getMissionAssigned();
            if(!player.isMissionsPaused()) {
                // Check if mission hasn't already been completed
                if (!mission.isCompleted()) {
                    // Get mission details
                    Player p1 = mission.getPlayer1();
                    Player p2 = mission.getPlayer2();
                    Zone zone = mission.getZone();
                    // Check if the mission hasn't timed out
                    if (LocalTime.now().isBefore(mission.getEndTime().plus(1, ChronoUnit.SECONDS)) || mission.isStart()) {
                        if (location.equals(zone)) {
                            // Evidence to return
                            JSONArray evidence = new JSONArray();
                            JSONObject e1 = new JSONObject();
                            e1.put("player_id", p1.getId());
                            e1.put("amount", 50);
                            evidence.put(e1);
                            JSONObject e2 = new JSONObject();
                            e2.put("player_id", p2.getId());
                            e2.put("amount", 50);
                            evidence.put(e2);
                            output.put("evidence", evidence);
                            // Success String
                            String success = "";
                            if (mission.isStart()) {
                                Player target = player.getTarget();
                                success = "Your target, " + target.getCodeName() + ", was last seen in " +
                                        target.getCurrentZone().getName() + ".\nGo find them!";
                            } else {
                                success = "Reward: Evidence on " + p1.getRealName() + " and " +
                                        p2.getRealName() + ".";
                            }
                            output.put("success_description", success);
                            // Set mission complete
                            mission.setCompleted(true);
                            missionService.saveMission(mission);
                            // Add mission to logs
                            logService.saveLog(LogType.MISSION, mission.getId(), LocalTime.now(), player.getCurrentZone());
                            responseStatus = HttpStatus.OK;
                        } else {
                            System.out.println("Not at mission location yet");
                            if (mission.isStart()) {
                                responseStatus = HttpStatus.NO_CONTENT;
                            } else {
                                Long timeRemaining = SECONDS.between(LocalTime.now(), mission.getEndTime());
                                output.put("time_remaining", timeRemaining);
                                responseStatus = HttpStatus.PARTIAL_CONTENT;
                            }
                        }
                    } else {
                        System.out.println("Mission has timed out");
                        responseStatus = HttpStatus.NON_AUTHORITATIVE_INFORMATION;
                        String failure = "No evidence gained.";
                        output.put("failure_description", failure);
                        // Set mission complete
                        mission.setCompleted(true);
                        missionService.saveMission(mission);
                        // Reset zone entry time
                        player.setTimeEnteredZone(LocalTime.now());
                        playerService.savePlayer(player);
                    }
                } else {
                    System.out.println("Mission already completed");
                    output.put("BAD_REQUEST", "Mission already completed");
                }
            } else {
                System.out.println("Missions paused");
                mission.setCompleted(true);
                missionService.saveMission(mission);
                responseStatus = HttpStatus.RESET_CONTENT;
            }
        } else {
            System.out.println("No player exists by this id");
            output.put("BAD_REQUEST", "Couldn't find player given");
        }
        System.out.println("/missionUpdate returned: " + output);
        logService.printToCSV(new ArrayList<>(Arrays.asList(playerId.toString(), realName, LocalTime.now().toString(),
                responseStatus.toString(), input.toString(), output.toString())), "missionUpdate.csv");
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/endInfo", method=RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<String> endInfo() {
        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.OK;

        JSONArray leaderboard = new JSONArray();
        List<Player> players = playerService.getAllPlayersByScore();
        for (Player player : players) {
            JSONObject playerInfo = new JSONObject();
            playerInfo.put("player_id", player.getId());
            playerInfo.put("position", playerService.getLeaderboardPosition(player));
            playerInfo.put("score", player.getReputation());
            leaderboard.put(playerInfo);
        }
        output.put("leaderboard", leaderboard);
        System.out.println("/endInfo returned: " + output);
        logService.printToCSV(new ArrayList<>(Arrays.asList(LocalTime.now().toString(),
                responseStatus.toString(), output.toString())), "endInfo.csv");
        return new ResponseEntity<>(output.toString(), responseStatus);
    }
}