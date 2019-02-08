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
//
//        List<Player> players = new ArrayList<>();
//        List<Player> allPlayers = getAllPlayers();
//        for (Player p : allPlayers) {
//            if (!(p.equals(player))) players.add(p);
//        }
//        return players;
        return playerRepository.findAllByHackerNameNot(player.getHackerName());
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
        return playerRepository.findAllByHackerNameNotAndHackerNameNot(p1.getHackerName(), p2.getHackerName());
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
    public void incrementKills(Player player, int n) {
        player.setKills(player.getKills() + n);
        playerRepository.save(player);
    }

    @Override
    public void assignHome(Player player, int home) {
        player.setHomeBeacon(home);
        playerRepository.save(player);
    }

    @Override
    public int getPlayerWeight(Player player) {
        return player.getKills();
    }

    @Override
    public Long newTarget(Long playerId){
        Player currentPlayer = getPlayer(playerId).get();
        // Create a list containing just the current player and their old target
        List<Player> players = new ArrayList<>();
        if (currentPlayer.getTarget() == null) {
            players = getAllPlayersExcept(currentPlayer);
        } else {
            players = getAllPlayersExceptTwo(currentPlayer, currentPlayer.getTarget());
        }
        // Weight all the players in this list by their current number of kills
        List<Player> weightedPlayers = new ArrayList<>();
        for (Player p : players) {
            int playerWeight = getPlayerWeight(p);
            weightedPlayers.add(p);
            for (int i = 0; i < playerWeight; i++) {
                weightedPlayers.add(p);
            }
        }
        // Pick a random player from this list as the new target
        Random random = new Random();
        Player newTarget = weightedPlayers.get(random.nextInt(weightedPlayers.size()));
        // Update the target and save the current player
        currentPlayer.setTarget(newTarget);
        savePlayer(currentPlayer);
        // Return the id of the new target
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
    public List<Long> getNearbyPlayerIds(Player player, int beaconMajor) {
        List<Long> ids = new ArrayList<>();
        if (beaconMajor != 0) {
            List<Player> players = playerRepository.findAllByNearestBeaconMajor(beaconMajor);
            ids = new ArrayList<>();
            for (Player p : players) {
                if (!(p.equals(player))) ids.add(p.getId());
            }
        }
        return ids;
    }

    @Override
    public boolean isValidRealNameAndHackerName(String realName, String hackerName) {
        return isValidRealName(realName) && isValidHackerName(hackerName);
    }

    @Override
    public boolean isValidRealName(String realName) {
        return true;
    }

    @Override
    public boolean isValidHackerName(String hackerName) {
        Optional<Player> playerOptional = getPlayerByHackerName(hackerName);
        return !playerOptional.isPresent();
    }

}
