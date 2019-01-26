package com.serendipity.gameController.service.gameService;

import com.serendipity.gameController.model.Game;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface GameService {
    Optional<Game> getGame(Long id);
}
