package com.serendipity.gameController.service.playerService;

import com.serendipity.gameController.model.Mission;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.model.PrevZone;
import com.serendipity.gameController.model.Zone;
import com.serendipity.gameController.repository.PlayerRepository;
import com.serendipity.gameController.service.prevZoneService.PrevZoneServiceImpl;
import com.serendipity.gameController.service.zoneService.ZoneServiceImpl;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalTime;
import java.util.*;

@Service("playerService")
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    ZoneServiceImpl zoneService;

    @Autowired
    PrevZoneServiceImpl prevZoneService;

    @Override
    public void savePlayer(Player player){
        playerRepository.save(player);
    }

    @Override
    public Optional<Player> getPlayer(Long id){
        return playerRepository.findById(id);
    }

    @Override
    public Optional<Player> getPlayerByCodeName(String codeName) { return playerRepository.findByCodeName(codeName); }

    @Override
    public Optional<Player> getPlayerByMission(Mission mission) { return playerRepository.findByMissionAssigned(mission); }

    @Override
    public List<Player> getAllPlayersByCurrentZone(Zone zone) { return playerRepository.findAllByCurrentZone(zone); }

    @Override
    public long countAllPlayers() { return playerRepository.count(); }

    @Override
    public List<Player> getAllPlayers() {
        List<Player> ps = new ArrayList<>();
        playerRepository.findAll().forEach(ps::add);
        return ps;
    }

    @Override
    public List<Player> getAllPlayersByScore() {
        List<Player> ps = new ArrayList<>();
        playerRepository.findAllByOrderByReputationDesc().forEach(ps::add);
        return ps;
    }

    @Override
    public List<Player> getAllPlayersExcept(Player player) {
//
//        List<Player> players = new ArrayList<>();
//        List<Player> allPlayers = getAllPlayers();
//        for (Player p : allPlayers) {
//            if (!(p.equals(player))) players.add(p);
//        }
//        return players;
        return playerRepository.findAllByCodeNameNot(player.getCodeName());
    }

    @Override
    public List<Player> getAllPlayersExceptTwo(Player p1, Player p2) {
//        List<Player> allPlayers = getAllPlayers();
//        List<Player> returnPlayers = new ArrayList<>();
//        for (Player player : allPlayers) {
//
//
//
//            if (!(exceptPlayers.contains(player))) returnPlayers.add(player);
//        }
//        return returnPlayers;
        return playerRepository.findAllByCodeNameNotAndCodeNameNot(p1.getCodeName(), p2.getCodeName());
    }

    @Override
    public List<JSONObject> getAllPlayersStartInfo() {
        List<JSONObject> players = new ArrayList<>();
        for (Player p : getAllPlayers()) {
            JSONObject obj = new JSONObject();
            obj.put("id", p.getId());
            obj.put("real_name", p.getRealName());
            obj.put("code_name", p.getCodeName());
            players.add(obj);
        }
        return players;
    }

    @Override
    public List<Player> getAllPlayersByTarget(Player target) {
        List<Player> players = new ArrayList<>();
        for (Player p : getAllPlayers()) {
            if (p.getTarget() == null) {
//                TODO: What do we do if null, remove player maybe?
            }
            else if (p.getTarget().getId().equals(target.getId())) players.add(p);
        }
        return players;
    }

    @Override
    public void deleteAllPlayers() {
        if (playerRepository.count() != 0) {
            playerRepository.deleteAll();
        }
    }

    public void createPlayers() {
        if (playerRepository.count() != 0) {
//            informationService.deleteAll();
            playerRepository.deleteAll();
        }
        List<String> codeNames = Arrays.asList("Cookingking", "Puppylover", "Headshot", "Guitarhero", "Cutiekitten", "Jackedjones");
        List<String> realNames = Arrays.asList("Tom", "Tilly", "Louis", "Nuha", "Jack", "David");
        Collections.shuffle(codeNames);
        for (int i = 0; i < realNames.size(); i++) {
            savePlayer(new Player(realNames.get(i), codeNames.get(i)));
        }
    }

    @Override
    public void incrementReputation(Player player, int n) {
        player.setReputation(player.getReputation() + n);
        playerRepository.save(player);
    }

    @Override
    public int getPlayerWeight(Player player) {
        return player.getReputation();
    }

    @Override
    public Optional<Player> newTarget(Player player) {
        // Create a list containing just the current player and their old target
        List<Player> players;
        if (player.getTarget() == null) {
            players = getAllPlayersExcept(player);
        } else {
            players = getAllPlayersExceptTwo(player, player.getTarget());
        }

        if (!players.isEmpty()) {
            // Weight all the players in this list by their current rep
            List<Player> weightedPlayers = new ArrayList<>();
            for (Player p : players) {
                weightedPlayers.add(p);
                int playerWeight = getPlayerWeight(p);
                for (int i = 0; i < playerWeight; i++) {
                    weightedPlayers.add(p);
                }
            }

            // Pick a random player from this list as the new target
            Random random = new Random();
            Player newTarget = weightedPlayers.get(random.nextInt(100) % weightedPlayers.size());

            // Return the id of the new target
            return Optional.of(newTarget);
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Player getRandomContact(List<Long> ids) {
        Random random = new Random();
        Long contactId = ids.get(random.nextInt(100) % ids.size());
        Player contact = getPlayer(contactId).get();
        return contact;
    }

    @Override
    public int getLeaderboardPosition(Player player) {
        int position = 0;
        List<Player> leaderboard = getAllPlayersByScore();
        for (int i = 0; i < leaderboard.size(); i++) {
            if (leaderboard.get(i).equals(player)) {
                position = i + 1;
                if(i > 0){
                    boolean found = true;
                    int index = i - 1;
                    while(found){
                        if( index >= 0 && leaderboard.get(index).getReputation() ==
                                leaderboard.get(i).getReputation()) {
                            position --;
                            index --;
                        }
                        else{
                            found = false;
                        }
                    }
                }
            }
        }
        return position;
    }

    @Override
    public List<JSONObject> getNearbyPlayers(Player player) {
        List<JSONObject> players = new ArrayList<>();
        if (player.hasCurrentZone()) {
            Zone zone = player.getCurrentZone();
            List<Player> nearPlayers = playerRepository.findAllByCurrentZone(zone);
            for (Player p : nearPlayers) {
                JSONObject info = new JSONObject();
                info.put("id", p.getId());
                info.put("location", zoneService.locationMapping(p.getCurrentZone()));
                players.add(info);
            }
        }
        return players;
    }

    @Override
    public List<JSONObject> getFarPlayers(Player player) {
        List<JSONObject> players = new ArrayList<>();
        if (player.hasCurrentZone()) {
            Zone zone = player.getCurrentZone();
            List<Player> farPlayers = playerRepository.findAllByCurrentZoneNot(zone);
            for (Player p : farPlayers) {
                JSONObject info = new JSONObject();
                info.put("id", p.getId());
                info.put("location", zoneService.locationMapping(p.getCurrentZone()));
                players.add(info);
            }
        }
        return players;
    }

    @Override
    public boolean isValidRealNameAndCodeName(String realName, String codeName) {
        return isValidRealName(realName) && isValidCodeName(codeName);
    }

    @Override
    public boolean isValidRealName(String realName) {
        return true;
    }

    @Override
    public boolean isValidCodeName(String codeName) {
        Optional<Player> playerOptional = getPlayerByCodeName(codeName);
        return !playerOptional.isPresent();
    }

    @Override
    public int calculateReputationGainFromExpose() {
        return 20;
    }

    @Override
    public Zone averagePrevZone(Player player, Zone newZone) {
        int maxSize = 3;
        Zone average = new Zone();
        List<PrevZone> prevZones = player.getPrevZones();
        // If list is full
        if(prevZones.size() >= maxSize){
            //Remove PrevZone entry for first item
            prevZoneService.deletePrevZone(prevZones.get(0));
            //Move each item -1 indexes and add new zone
            prevZones = prevZones.subList(1, maxSize);
        }
        //Add new prevZone
        PrevZone prevZone = new PrevZone(LocalTime.now(), player, newZone);
        prevZoneService.savePrevZone(prevZone);
        prevZones.add(prevZone);
        //Update players prevZone list
        player.setPrevZones(prevZones);
        savePlayer(player);
        // Count frequency of zones in list
        Map<Zone, Integer> frequencies = new HashMap<Zone, Integer>();
        for(PrevZone z : prevZones){
            if(frequencies.containsKey(z.getZone())){
                int value = frequencies.get(z.getZone());
                frequencies.replace(z.getZone(), value+1);
            } else {
                frequencies.put(z.getZone(), 1);
            }
        }
        //Find first zone associated with max value in map
        int maxVal = Collections.max(frequencies.values());
        for(Map.Entry<Zone, Integer> entry : frequencies.entrySet()){
            if (entry.getValue() == maxVal) {
                average = entry.getKey();
                break;
            }
        }
        return average;
    }

}
