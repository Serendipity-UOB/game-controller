package com.serendipity.gameController.service.gameService;

import com.serendipity.gameController.model.Game;
import com.serendipity.gameController.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
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
    public Optional<Game> getNextGame() {
        return gameRepository.findFirstByStartTimeAfterOrderByStartTimeAsc(LocalTime.now());
    }

    @Override
    public List<Game> getAllGames() {
        return gameRepository.findAll();
    }

    @Override
    public List<Game> getAllGamesByStartTimeAsc() {
        return gameRepository.findAllByOrderByStartTimeAsc();
    }

    @Override
    public void deleteAllGames() {
        if (gameRepository.count() != 0) {
            gameRepository.deleteAll();
        }
    }

    @Override
    public boolean isGameOver(Game game) {
        return game.getEndTime().isAfter(LocalTime.now());
    }


}
