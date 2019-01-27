package com.serendipity.gameController.service.gameService;

import com.serendipity.gameController.model.Game;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface GameService {

    /*
     * @return A list of all the games in the database.
     */
    List<Game> getAllGames();

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
     * Deletes all games in the database.
     */
    void deleteGames();
}
