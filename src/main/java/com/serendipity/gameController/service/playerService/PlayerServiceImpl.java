package com.serendipity.gameController.service.playerService;

import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.PlayerRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service("playerService")
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void savePlayer(Player player){
        playerRepository.save(player);
    }

    @Override
    public Optional<Player> getPlayer(Long id){
        return playerRepository.findById(id);
    }

    @Override
    public Optional<Player> getPlayerByHackerName(String hackerName) { return playerRepository.findByHackerName(hackerName); }

    @Override
    public long countPlayer() { return playerRepository.count(); }

    @Override
    public List<Player> getAllPlayers() {
        List<Player> ps = new ArrayList<>();
        playerRepository.findAll().forEach(ps::add);
        return ps;
    }

    @Override
    public List<Player> getAllPlayersByScore() {
        List<Player> ps = new ArrayList<>();
        playerRepository.findAllByOrderByKillsDesc().forEach(ps::add);
        return ps;
    }

    @Override
    public List<Player> getAllPlayersExcept(Player player) {
        List<Player> players = new ArrayList<>();
        for (Player p : getAllPlayers()) {
            if (p != player) players.add(p);
        }
        return players;
    }

    @Override
    public List<Player> getAllPlayersExcept(List<Player> exceptPlayers) {
        List<Player> players = new ArrayList<>();
        for (Player p : getAllPlayers()) {
            if (!exceptPlayers.contains(p)) players.add(p);
        }
        return players;
    }

    @Override
    public List<JSONObject> getAllPlayersStartInfo() {
        List<JSONObject> players = new ArrayList<>();
        for (Player p : getAllPlayers()) {
            JSONObject obj = new JSONObject();
            obj.put("id", p.getId());
            obj.put("real_name", p.getRealName());
            obj.put("hacker_name", p.getHackerName());
            players.add(obj);
        }

        return players;
    }

    @Override
    public List<Player> getAllPlayersByTarget(Player target) {
        List<Player> players = new ArrayList<>();
        for (Player p : getAllPlayers()) {
            if (p.getTarget().getId().equals(target.getId())) players.add(p);
        }
        return players;
    }

    @Override
    public void deletePlayers() {
        if (playerRepository.count() != 0) {
            playerRepository.deleteAll();
        }
    }

    public void createPlayers() {
        if (playerRepository.count() != 0) {
//            informationService.deleteAll();
            playerRepository.deleteAll();
        }
        List<String> hackerNames = Arrays.asList("Cookingking", "Puppylover", "Headshot", "Guitarhero", "Cutiekitten", "Jackedjones");
        List<String> realNames = Arrays.asList("Tom", "Tilly", "Louis", "Nuha", "Jack", "David");
        Collections.shuffle(hackerNames);
        for (int i = 0; i < realNames.size(); i++) {
            savePlayer(new Player(realNames.get(i), hackerNames.get(i)));
        }
    }

    @Override
    public void incKills(Player player) {
        player.setKills(player.getKills()+1);
        playerRepository.save(player);
    }

    @Override
    public void assignHome(Player player, int home) {
        player.setHomeBeacon(home);
        playerRepository.save(player);
    }

    @Override
    public void assignTargets() {
        List<Player> players = getAllPlayers();
        List<Player> unassigned = getAllPlayers();
        Random random = new Random();
        for (Player player : players) {
            //Get the list of players that are unassigned and not me
            List<Player> chooseFrom = new ArrayList<>();
            for (Player p : unassigned) {
                if (!(p.equals(player))) {
                    chooseFrom.add(p);
                }
            }
            //Check to see if chooseFrom is empty
            if (chooseFrom.isEmpty()) {
                //Swap the last person's target with someone who doesn't have them as a target
                //Try swapping with the first person's target
                if (!(players.get(0).equals(player))) {
                    //Then swap their targets
                    players.get(0).setTarget(player);
                    player.setTarget(players.get(0));

                } else {
                    players.get(1).setTarget(player);
                    player.setTarget(players.get(1));
                }
            } else {
                //Pick random from chooseFrom
                int i = random.nextInt(chooseFrom.size());
                Player target = chooseFrom.get(i);
                player.setTarget(target);
                //Reset unassigned
                unassigned.remove(target);
            }
        }
        //Update the database
        for (Player player : players) {
            savePlayer(player);
        }

    }

    @Override
    public void halfInformation(Player player) {
//        List<Information> information = informationService.getAllInformationForOwner(player);
//        for (Information info : information) {
//            info.setInteractions(info.getInteractions()/2);
//            informationService.saveInformation(info);
//        }
    }

    @Override
    public int getTotalInformation(Player player) {
//        List<Information> information = informationService.getAllInformationForOwner(player);
        int total = 0;
//        for (Information info : information) {
//            total += info.getInteractions();
//        }
        return total;
    }

    @Override
    public int getPlayerWeight(Player player) {
        int totalInformation = getTotalInformation(player);
        int averageInformation = totalInformation/getAllPlayers().size();
        int kills = player.getKills();
        int weight = averageInformation + kills;
        return weight;
    }

    @Override
    public Long newTarget(Long playerId){
        Player player = getPlayer(playerId).get();
        List<Player> except = new ArrayList<>();
        except.add(player);
        if (player.getTarget() != null) except.add(player.getTarget());
        List<Player> players = getAllPlayersExcept(except);
        List<Player> weightedPlayers = new ArrayList<>();
        for (Player p : players) {
            int playerWeight = getPlayerWeight(p);
            weightedPlayers.add(p);
            for (int i = 0; i < playerWeight; i++) {
                weightedPlayers.add(p);
            }
        }
        Random random = new Random();
        Player newTarget = weightedPlayers.get(random.nextInt(weightedPlayers.size()));
        player.setTarget(newTarget);
        savePlayer(player);
        return newTarget.getId();
    }

    @Override
    public Player getRandomContact(List<Long> ids) {
        Random random = new Random();
        Long contactId = ids.get(random.nextInt(ids.size()));
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
            }
        }
        return position;
    }

    @Override
    public List<Long> getNearbyPlayerIds(Player player, int beaconMinor) {
        List<Player> players = playerRepository.findAllByNearestBeaconMinor(beaconMinor);
        List<Long> ids = new ArrayList<>();
        for (Player p : players) {
            ids.add(p.getId());
        }
        return ids;
    }

//    @PostConstruct
//    public void insertRootPlayers() {
//        Player p1 = new Player("Tilly", "Headshot");
//        Player p2 = new Player("Tom", "Cutiekitten");
//        Player p3 = new Player("Louis", "Puppylover");
//        Player p4 = new Player("Jack", "Cookingking");
//        savePlayer(p1);
//        savePlayer(p2);
//        savePlayer(p3);
//        savePlayer(p4);
//    }



}
