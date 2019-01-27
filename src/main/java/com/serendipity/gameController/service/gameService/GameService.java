package com.serendipity.gameController.service.gameService;

import com.serendipity.gameController.model.Game;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface GameService {

    /*
     * @param id The id of the game you are looking for.
     * @return An optional of the game with that id.
     */

    void saveGame(Game game);

    Optional<Game> getGame(Long id);

    void deleteGames();
}
