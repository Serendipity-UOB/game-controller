package com.serendipity.gameController.service.playerService;

import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.model.Zone;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PlayerService {

    /*
     * @param player The player to save
     */
    void savePlayer(Player player);

    /*
     * @param id The id of the player you are looking for.
     * @return An optional of the player with that id.
     */
    Optional<Player> getPlayer(Long id);

    /*
     * @param codename The codename of the player you are looking for.
     * @return The player with that codename.
     */
    Optional<Player> getPlayerByCodeName(String codeName);

    /*
     * @return The number of players in the database.
     */
    long countAllPlayers();

    /*
     * @return A list of all the players in the database.
     */
    List<Player> getAllPlayers();

    /*
     * @return A list of all the players in the database, ordered by score descending.
     */
    List<Player> getAllPlayersByScore();

    /*
     * @return A list of JSONObject containing {id, real_name, code_name}.
     */
    List<JSONObject> getAllPlayersStartInfo();

    List<Player> getAllPlayersByTarget(Player target);

    void deletePlayers();

    void createPlayers();

    /*
     * @param player The player you don't want to return.
     * @return A list of all the players in the database except the given player.
     */
    List<Player> getAllPlayersExcept(Player player);

    /*
     * @param players The two players that you don't want to return.
     * @return A list of all the players in the database except the given players.
     */
    List<Player> getAllPlayersExceptTwo(Player p1, Player p2);

    /*
     * @param player The player to get the weight for.
     * @return The weight of the given player.
     */
    int getPlayerWeight(Player player);

    /*
     * @param player The player to increment the reputation for.
     * @param n The number of reputation to add.
     */
    void incrementReputation(Player player, int n);

    /*
     * @param playerId The id of the player to get a new target for.
     * @return The id of their new target.
     */
    Long newTarget(Long playerId);

    /*
     * @param ids A list of the ids of all the player's contacts.
     * @return A random player from that list.
     */
    Player getRandomContact(List<Long> ids);

    /*
     * @param player The player whose position you want.
     * @return The player's position on the leaderboard.
     */
    int getLeaderboardPosition(Player player);

    /*
     * @param player The current player
     * @return List of ids of the nearby players
     */
    List<Long> getNearbyPlayerIds(Player player);

    /*
     * @param realName The real name that has been entered by the user
     * @param codeName The code name that has been entered by the user
     * @return True if this is a valid combination of realName and codeName
     */
    boolean isValidRealNameAndCodeName(String realName, String codeName);

    /*
     * @param realName The real name that has been entered by the user
     * @return True if this is a valid realName
     */
    boolean isValidRealName(String realName);

    /*
     * @param codeName The code name that has been entered by the user
     * @return True if this is a valid codeName
     */
    boolean isValidCodeName(String codeName);

}
