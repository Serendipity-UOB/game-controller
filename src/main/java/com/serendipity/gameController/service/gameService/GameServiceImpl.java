package com.serendipity.gameController.service.gameService;

import com.serendipity.gameController.model.Game;
import com.serendipity.gameController.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static java.lang.Math.abs;

@Service("gameService")
public class GameServiceImpl implements GameService {

    @Autowired
    GameRepository gameRepository;

    @Override
    public void saveGame(Game game){
        gameRepository.save(game);
    }

    @Override
    public Optional<Game> getGame(Long id) {
        return gameRepository.findById(id);
    }

    @Override
    public Optional<Game> getNextGame() {
        return gameRepository.findFirstByStartTimeGreaterThanEqualOrderByStartTimeAsc(LocalTime.now());
    }

    @Override
    public Optional<Game> getCurrentGame() {
        return gameRepository.findFirstByStartTimeLessThanEqualAndEndTimeGreaterThanEqualOrderByStartTimeAsc(LocalTime.now(), LocalTime.now());
    }

    @Override
    public Optional<Game> getPreviousGame() {
        return gameRepository.findFirstByEndTimeLessThanEqualOrderByEndTimeDesc(LocalTime.now());
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
        return game.getEndTime().isBefore(LocalTime.now());
    }

    @Override
    public List<Integer> getTimeRemaining(Game game) {
        List<Integer> time = new ArrayList<>();
        int diff = game.getEndTime().toSecondOfDay() - LocalTime.now().toSecondOfDay();
        time.add((diff/(60 * 60)) % 24);
        time.add((diff/60) % 60);
        time.add(diff % 60);
        return time;
    }

    @Override
    public List<Integer> getTimeToStart(Game game) {
        List<Integer> time = new ArrayList<>();
        int diff = game.getStartTime().toSecondOfDay() - LocalTime.now().toSecondOfDay();
        time.add((diff/(60 * 60)) % 24);
        time.add((diff/60) % 60);
        time.add(diff % 60);
        return time;
    }
}
