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
    public Player getPlayerByHackerName(String hackerName) { return playerRepository.findByHackerName(hackerName); }

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

}
