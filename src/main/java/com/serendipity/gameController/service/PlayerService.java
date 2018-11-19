package com.serendipity.gameController.service;

import com.serendipity.gameController.model.Player;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PlayerService {

    void savePlayer(Player player);

    Player getPlayer(Long id);

    List<Player> getAllPlayers();

}
