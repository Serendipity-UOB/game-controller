package com.serendipity.gameController.service.gameService;

import com.serendipity.gameController.model.Game;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service("gameService")
public class GameServiceImpl implements GameService {

    @Autowired
    GameRepository gameRepository;

    @Override
    public void saveGame(Game game){
        gameRepository.save(game);
    }

    @Override
    public Optional<Game> getGame(Long id) { return gameRepository.findById(id); }

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public void deleteGames() {
        if (gameRepository.count() != 0) {
            gameRepository.deleteAll();
        }
    }


}
