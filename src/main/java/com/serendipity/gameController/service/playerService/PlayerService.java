package com.serendipity.gameController.service.playerService;

import com.serendipity.gameController.model.Player;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface PlayerService {

    void savePlayer(Player player);

    Optional<Player> getPlayer(Long id);

    List<Player> getAllPlayers();

    List<Player> getAllPlayersExcept(Player player);

    List<Player> sortPlayers(List<Player> players);

    void insertRootPlayers();

}
