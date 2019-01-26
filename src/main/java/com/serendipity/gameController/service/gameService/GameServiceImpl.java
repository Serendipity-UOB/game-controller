package com.serendipity.gameController.service.gameService;

import com.serendipity.gameController.model.Game;
import com.serendipity.gameController.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("gameService")
public class GameServiceImpl implements GameService {

    @Autowired
    GameRepository gameRepository;

    @Override
    public Optional <Game> getGame(Long id) { return gameRepository.findById(id); }

}
