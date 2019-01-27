package com.serendipity.gameController.service.playerService;

import com.serendipity.gameController.model.Player;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PlayerService {

    void savePlayer(Player player);

    Optional<Player> getPlayer(Long id);

    Player getPlayerByHackerName(String hackerName);

    long countPlayer();

    List<Player> getAllPlayers();

    List<Player> getAllPlayersByScore();

    List<JSONObject> getAllPlayersStartInfo();

    List<Player> getAllPlayersByTarget(Player target);

    void deletePlayers();

    void createPlayers();

    List<Player> getAllPlayersExcept(Player player);

    List<Player> getAllPlayersExcept(List<Player> exceptPlayers);

    void assignHome(Player player, int home);

    void assignTargets();

    void incKills(Player player);

    void halfInformation(Player player);

    int getTotalInformation(Player player);

    int getPlayerWeight(Player player);

    Long newTarget(Long playerId);

    Player getRandomContact(List<Long> ids);

    int getLeaderboardPosition(Player player);

    /*
     * @param player The current player
     * @return List of ids of the nearby players
     */
    List<Long> getNearbyPlayerIds(Player player, int beaconMinor);

}
