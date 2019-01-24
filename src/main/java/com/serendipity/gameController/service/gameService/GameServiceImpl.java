package com.serendipity.gameController.service.gameService;

import com.serendipity.gameController.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("gameService")
public class GameServiceImpl implements GameService {

    @Autowired
    GameRepository gameRepository;

}
