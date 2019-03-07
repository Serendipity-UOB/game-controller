package com.serendipity.gameController.control;

import com.serendipity.gameController.model.*;
import com.serendipity.gameController.service.beaconService.BeaconServiceImpl;
import com.serendipity.gameController.service.exchangeService.ExchangeServiceImpl;
import com.serendipity.gameController.service.gameService.GameService;
import com.serendipity.gameController.service.missionService.MissionService;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import org.apache.tomcat.jni.Local;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    GameService gameService;

    @Autowired
    MissionService missionService;

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
        // TODO: Implement
        output.put("exchange_pending", 0);

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
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
        JSONObject input = new JSONObject(json);
//        { requester_id, responder_id, contact_ids[{ contact_id }]}
//        Long requesterId = input.getLong("requester_id");
        return response;
    }

    @RequestMapping(value="/exchangeResponse", method=RequestMethod.POST)
    @ResponseBody
    public ResponseEntity exchangeResponse(@RequestBody String json) {
        ResponseEntity<String> response = new ResponseEntity<>(HttpStatus.OK);
        JSONObject input = new JSONObject(json);
        return response;
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

    @RequestMapping(value="/expose", method=RequestMethod.POST, consumes="application/json")
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