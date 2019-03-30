package com.serendipity.gameController.service.gameService;

import com.serendipity.gameController.model.Game;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface GameService {

    /*
     * @param game The game to be saved.
     */
    void saveGame(Game game);

    /*
     * @param id The id of the game you are looking for.
     * @return An optional of the game with that id.
     */
    Optional<Game> getGame(Long id);

    /*
     * @return An optional containing the next game that will be starting, if one exists, empty otherwise.
     */
    Optional<Game> getNextGame();

    /*
     * @return A list of all the games in the database.
     */
    List<Game> getAllGames();

    /*
     * @return A list of all the games in the database, ordered by start time, soonest first.
     */
    List<Game> getAllGamesByStartTimeAsc();

    /*
     * Deletes all games in the database.
     */
    void deleteAllGames();

    /*
     * @param game The game you want to check the status of.
     * @return Returns true if the end time is before the current time.
     */
    boolean isGameOver(Game game);

    /*
     * @param game The game you to get the time remaining for.
     * @return List of the hours, minutes and seconds left.
     */
    List<Integer> getTimeRemaining(Game game);
}
