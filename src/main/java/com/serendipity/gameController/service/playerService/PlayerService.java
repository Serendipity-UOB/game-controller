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

    void createPlayers();

    List<Player> getAllPlayersExcept(Player player);

    List<Player> getAllPlayersExcept(List<Player> exceptPlayers);

    void assignTargets();

    void incKills(Player player);

    void halfInformation(Player player);

    int getTotalInformation(Player player);

}
