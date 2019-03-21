package com.serendipity.gameController.control;

import com.serendipity.gameController.model.*;
import com.serendipity.gameController.service.beaconService.BeaconServiceImpl;
import com.serendipity.gameController.service.evidenceService.EvidenceServiceImpl;
import com.serendipity.gameController.service.exchangeService.ExchangeServiceImpl;
import com.serendipity.gameController.service.gameService.GameServiceImpl;
import com.serendipity.gameController.service.interceptService.InterceptServiceImpl;
import com.serendipity.gameController.service.missionService.MissionServiceImpl;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import com.serendipity.gameController.service.zoneService.ZoneServiceImpl;
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
import static java.time.temporal.ChronoUnit.SECONDS;

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

    @Autowired
    ZoneServiceImpl zoneService;

    @Autowired
    InterceptServiceImpl interceptService;

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
        } else if (!playerService.isValidRealNameAndCodeName(realName, codeName)) {
            output.put("error", "Code name is taken");
        } else {
            Player player = new Player(realName, codeName);
            playerService.savePlayer(player);
            output.put("player_id", player.getId());
            responseStatus = HttpStatus.OK;
        }
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
        } else {
            Game nextGame = optionalNextGame.get();
            output.put("start_time", nextGame.getStartTime());
            output.put("number_players", playerService.countAllPlayers());
        }
        System.out.println("/gameInfo returned: " + output);
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/joinGame", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> joinGame(@RequestBody String json) {
        // Handle JSON
        JSONObject input = new JSONObject(json);
        System.out.println("/joinGame received: " + input);
        Long id = input.getLong("player_id");

        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        // Look for player
        Optional<Player> optionalPlayer = playerService.getPlayer(id);
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();

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
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/startInfo", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> getStartInfo(@RequestBody String json) {
        // Read in request body
        JSONObject input = new JSONObject(json);
        System.out.println("/startInfo received: " + input);
        Long playerId = input.getLong("player_id");
        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;
        // Ensure player exists
        Optional<Player> opPlayer = playerService.getPlayer(playerId);
        if (opPlayer.isPresent()) {
            // Get two random players to gain intel on from mission
            Player player = opPlayer.get();
            List<Player> players = playerService.getAllPlayersExcept(player);
            Random random = new Random();
            // Ensure 1 other player exists
            System.out.println("Players in game:" + players.size());
            if(players.size() > 0) {
                Player target1 = players.get(random.nextInt(players.size()));
                System.out.println("Target 1:" + target1.getId());
                players.remove(target1);
                // Ensure 2 other players exist
                if (players.size() > 0) {
                    Player target2 = players.get(random.nextInt(players.size()));
                    System.out.println("Target 2:" + target2.getId());
                    // Ensure game exists
                    // TODO: Deal with multiple game instances
                    Game game = gameService.getAllGames().get(0);
                    output.put("end_time", game.getEndTime());

                    // Create a mission
                    Mission mission = missionService.createMission(game, target1, target2);

                    // Assign mission to player
                    player.setMissionAssigned(mission);
                    playerService.savePlayer(player);

                    // Return all players
                    responseStatus = HttpStatus.OK;
                    output.put("all_players", playerService.getAllPlayersStartInfo());
                } else {
                    System.out.println("There aren't enough players in the game");
                    output.put("BAD_REQUEST", "Not enough players");
                }
            } else {
                System.out.println("There are no players in the game");
                output.put("BAD_REQUEST", "Not enough players");
            }
        } else {
            System.out.println("No player exists by this id");
            output.put("BAD_REQUEST", "Couldn't find player given");
        }
        System.out.println("/startInfo returned: " + output);
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/atHomeBeacon", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> atHomeBeacon(@RequestBody String json) {
        // Handle json
        JSONObject input = new JSONObject(json);
        System.out.println("/atHomeBeacon received: " + input);
        Long id = input.getLong("player_id");
        JSONArray beacons = input.getJSONArray("beacons");

        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        // Look for player
        Optional<Player> optionalPlayer = playerService.getPlayer(id);
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();
            if (player.hasHomeZone()) {
                Optional<Zone> optionalZone = zoneService.calculateCurrentZone(player, beacons);
                if (optionalZone.isPresent()) {
                    Zone currentZone = optionalZone.get();
                    player.setCurrentZone(currentZone);
                    playerService.savePlayer(player);
                    Zone homeZone = player.getHomeZone();
                    if (currentZone.equals(homeZone)) {
                        output.put("home", true);
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
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/playerUpdate", method=RequestMethod.POST , consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> playerUpdate(@RequestBody String json) {

        // Handle json
        JSONObject input = new JSONObject(json);
        System.out.println("/playerUpdate received: " + input);
        Long id = input.getLong("player_id");
        JSONArray jsonBeacons = input.getJSONArray("beacons");

        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        // Does player exist
        Optional<Player> optionalPlayer = playerService.getPlayer(id);
        if (optionalPlayer.isPresent()) {
            Player player = optionalPlayer.get();

            // Nearby players
            List<Long> nearbyPlayerIds = new ArrayList<>();
            Optional<Zone> optionalZone = zoneService.calculateCurrentZone(player, jsonBeacons);
            if (optionalZone.isPresent()) {
                Zone zone = optionalZone.get();
                player.setCurrentZone(zone);
                playerService.savePlayer(player);
                nearbyPlayerIds = playerService.getNearbyPlayerIds(player);
            } else System.out.println("Couldn't calculate the player's current zone");
            output.put("nearby_players", nearbyPlayerIds);

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
                if (exchangeService.getTimeRemaining(exchange) != 0l && !exchange.isRequestSent()) {
                    requesterId = exchange.getRequestPlayer().getId();
                    exchange.setRequestSent(true);
                    exchangeService.saveExchange(exchange);
                }
            }
            output.put("exchange_pending", requesterId);

            // Mission
            Optional<Mission> opMission = missionService.getMission(player.getMissionAssigned().getId());
            if( opMission.isPresent() ){
                Mission mission = opMission.get();
                // If mission should start
                if (mission.getStartTime().isBefore(LocalTime.now()) && !mission.isCompleted()) {
                    Player p1 = mission.getPlayer1();
                    Player p2 = mission.getPlayer2();
                    String missionDescription;
                    missionDescription = "We have discovered that evidence about <b>" + p1.getRealName()
                            + "'s</b> and <b>" + p2.getRealName() + "'s</b>";
                    Zone zone = mission.getZone();
                    if( zone == null ) {
                        List<Zone> notCurrent = zoneService.getAllZonesExcept(player.getCurrentZone().getId());
                        Random random = new Random();
                        zone = notCurrent.get(random.nextInt(notCurrent.size()));
                    }

                    missionDescription += " activities can be found at <b>" + zone.getName() +
                            "</b>.\n Get there in <b>30 Seconds</b> to secure it.";

                    mission.setZone(zone);
                    mission.setCompleted(true);
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
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/newTarget", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> getNewTarget(@RequestBody String json) {
        JSONObject input = new JSONObject(json);
        System.out.println("/newTarget received: " + input);
        Long playerId = input.getLong("player_id");

        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        Long newTargetId = playerService.newTarget(playerId);
        output.put("target_player_id", newTargetId);
        responseStatus = HttpStatus.OK;
        System.out.println("/newTarget returned: " + output);
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
            Player responder = optionalResponder.get();

            // Find exchange
            Optional<Exchange> optionalExchange = exchangeService.getMostRecentExchangeFromPlayer(requester);
            if (optionalExchange.isPresent()) {
                Exchange exchange = optionalExchange.get();

                // Handle request
                if (exchange.getResponsePlayer() == responder) {
                    if (exchange.isRequesterToldComplete()) {
                        exchangeService.createExchange(requester, responder, jsonContactIds);
                        responseStatus = HttpStatus.CREATED;
                    } else if (exchange.getResponse().equals(ExchangeResponse.ACCEPTED)) {
                        List<Evidence> evidenceList = exchangeService.getMyEvidence(exchange, requester);
                        output.put("evidence", evidenceService.evidenceListToJsonArray(evidenceList));
                        exchange.setRequesterToldComplete(true);
                        exchangeService.saveExchange(exchange);
                        responseStatus = HttpStatus.ACCEPTED;
                    } else if (exchangeService.getTimeRemaining(exchange) <= 0l) {
                        exchange.setRequesterToldComplete(true);
                        exchangeService.saveExchange(exchange);
                        responseStatus = HttpStatus.REQUEST_TIMEOUT;
                    } else {
                        if (exchange.getResponse().equals(ExchangeResponse.REJECTED)) {
                            exchange.setRequesterToldComplete(true);
                            exchangeService.saveExchange(exchange);
                            responseStatus = HttpStatus.NO_CONTENT;
                        } else if (exchange.getResponse().equals(ExchangeResponse.WAITING)) {
                            responseStatus = HttpStatus.PARTIAL_CONTENT;
                        } else {
                            System.out.println("Something has gone very wrong to get to here");
                        }
                    }
                } else if (exchangeService.getTimeRemaining(exchange) <= 0l) {
                    System.out.println("Creating exchange");
                    exchangeService.createExchange(requester, responder, jsonContactIds);
                    responseStatus = HttpStatus.CREATED;
                } else {
                    System.out.println("Player already requesting exchange with someone else");
                    responseStatus = HttpStatus.NOT_FOUND;
                }
            } else {
                System.out.println("No exchange exists from this player, creating an exchange");
                exchangeService.createExchange(requester, responder, jsonContactIds);
                responseStatus = HttpStatus.CREATED;
            }
        } else System.out.println("Couldn't find either the requester or the responder by these ids");
        System.out.println("/exchangeRequest returned: " + output);
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

        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

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
                // Set status code
                responseStatus = HttpStatus.ACCEPTED;
            } else if (exchangeResponse.equals(ExchangeResponse.REJECTED)) {
                exchange.setResponse(exchangeResponse);
                exchangeService.saveExchange(exchange);
                responseStatus = HttpStatus.RESET_CONTENT;
            } else System.out.println("Exchange has no response status, something wrong on the server");
        } else System.out.println("Couldn't find an exchange between these players");
        System.out.println("/exchangeResponse returned: " + output);
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
                            playerService.savePlayer(p);
                        }
                    }
                    // set targets exposed attribute
                    target.setExposedBy(playerId);
                    playerService.savePlayer(target);
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

        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        // Ensure player exists
        Optional<Player> opPlayer = playerService.getPlayer(playerId);
        Optional<Player> opTarget = playerService.getPlayer(targetId);
        if (opPlayer.isPresent() && opTarget.isPresent()) {
            Player player = opPlayer.get();
            Player target = opTarget.get();
            // Find exchange
            Optional<Exchange> optionalExchange = exchangeService.getMostRecentExchangeFromPlayer(target);
            if (optionalExchange.isPresent()) {
                Exchange exchange = optionalExchange.get();
                // Ensure the player isn't part of the intercepted exchange
                if(!exchange.getRequestPlayer().equals(player) && !exchange.getResponsePlayer().equals(player)) {
                    if (exchangeService.getTimeRemaining(exchange) <= 0l) {
                        // Find if player has an intercept
                        Optional<Intercept> opIntercept = interceptService.getInterceptByPlayer(player);
                        if (opIntercept.isPresent()) {
                            Intercept intercept = opIntercept.get();
                            // Find if the intercept is still active
                            if (!intercept.isExpired()) {
                                // Find if the active intercept is for the exchange targeted
                                if (intercept.getExchange().equals(exchange)) {
                                    // Find if exchange is still active
                                    if (exchange.isRequesterToldComplete()) {
                                        // Determine response
                                        if (exchange.getResponse().equals(ExchangeResponse.ACCEPTED)) {
                                            List<Evidence> evidenceList = exchangeService.getMyEvidence(exchange, exchange.getRequestPlayer());
                                            output.put("evidence", evidenceService.evidenceListToJsonArray(evidenceList));
                                            responseStatus = HttpStatus.OK;
                                        } else {
                                            System.out.println("Exchange wasn't accepted");
                                            responseStatus = HttpStatus.NO_CONTENT;
                                        }
                                        // Set intercept to be expired
                                        intercept.setExpired(true);
                                        interceptService.saveIntercept(intercept);
                                    } else {
                                        System.out.println("Still waiting");
                                        responseStatus = HttpStatus.PARTIAL_CONTENT;
                                    }
                                } else {
                                    System.out.println("Trying to intercept wrong exchange");
                                    responseStatus = HttpStatus.NOT_FOUND;
                                }
                            } else {
                                System.out.println("Intercept has expired, creating a new one");
                                // Overwrite expired intercept
                                intercept.setExpired(false);
                                intercept.setExchange(exchange);
                                interceptService.saveIntercept(intercept);
                                responseStatus = HttpStatus.CREATED;
                            }
                        } else {
                            System.out.println("No intercept exists, creating one");
                            // Create intercept
                            Intercept intercept = new Intercept(player, exchange);
                            interceptService.saveIntercept(intercept);
                            responseStatus = HttpStatus.CREATED;
                        }
                    } else { output.put("BAD_REQUEST", "Couldn't find exchange for target given"); }
                } else { output.put("BAD_REQUEST", "Cannot target own exchange"); }
            } else { output.put("BAD_REQUEST", "Couldn't find exchange for target given"); }
        } else {
            System.out.println("No player exists by this id");
            output.put("BAD_REQUEST", "Couldn't find player or target id given");
        }
        System.out.println("/intercept returned: " + output);
        return new ResponseEntity<>(output.toString(), responseStatus);
    }

    @RequestMapping(value="/missionUpdate", method=RequestMethod.POST, consumes="application/json")
    @ResponseBody
    public ResponseEntity<String> missionUpdate(@RequestBody String json) {
        // receive JSON object
        JSONObject input = new JSONObject(json);
        System.out.println("/missionUpdate received: " + input);
        Long playerId = input.getLong("player_id");

        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.BAD_REQUEST;

        // Check player exists
        Optional<Player> opPlayer = playerService.getPlayer(playerId);
        if (opPlayer.isPresent()) {
            Player player = opPlayer.get();
            Zone location = player.getCurrentZone();
            Mission mission = player.getMissionAssigned();
            // Get mission details
            Player p1 = mission.getPlayer1();
            Player p2 = mission.getPlayer2();
            Zone zone = mission.getZone();
            // Check if the mission hasn't timed out
            if (LocalTime.now().isBefore(mission.getEndTime().minus(1, ChronoUnit.SECONDS))) {
                if (location.equals(zone)) {
                    // Evidence to return
                    JSONArray evidence = new JSONArray();
                    JSONObject e1 = new JSONObject();
                    e1.put("player_id", p1.getId());
                    e1.put("amount", 10);
                    evidence.put(e1);
                    JSONObject e2 = new JSONObject();
                    e2.put("player_id", p2.getId());
                    e2.put("amount", 10);
                    evidence.put(e2);
                    output.put("evidence", evidence);
                    // Success String
                    String success = "You recovered evidence on <b>" + p1.getRealName() +"</b> and <b>" +
                            p2.getRealName() +"’s</b> activities at <b>" + zone.getName() + "</b>.";
                    output.put("success_description", success);
                    responseStatus = HttpStatus.OK;
                } else {
                    System.out.println("Not at mission location yet");
                    Long timeRemaining = SECONDS.between(LocalTime.now(), mission.getEndTime());
                    output.put("time_remaining", timeRemaining);
                    responseStatus = HttpStatus.PARTIAL_CONTENT;
                }
            } else {
                System.out.println("Mission has timed out");
                responseStatus = HttpStatus.NON_AUTHORITATIVE_INFORMATION;
                String failure = "You didn’t managed to recover evidence on <b>" + p1.getRealName() +"</b> and <b>" +
                        p2.getRealName() +"’s</b> at <b>" + zone.getName() + "</b>.";
                output.put("failure_description", failure);
            }
        } else {
            System.out.println("No player exists by this id");
            output.put("BAD_REQUEST", "Couldn't find player given");
        }
        System.out.println("/missionUpdate returned: " + output);
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
        return new ResponseEntity<>(output.toString(), responseStatus);
    }
}