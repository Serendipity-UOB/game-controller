package com.serendipity.gameController.service.playerService;

import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("playerService")
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    PlayerRepository playerRepository;

    @Override
    public void savePlayer(Player player){
        playerRepository.save(player);
    }

    @Override
    public Optional<Player> getPlayer(Long id){
        return playerRepository.findById(id);
    }

    @Override
    public List<Player> getAllPlayers(){
        List<Player> ps = new ArrayList<>();
        playerRepository.findAll().forEach(ps::add);
        return ps;
    }

    @Override
    public List<Player> getAllPlayersExcept(Player player) {
        List<Player> players = new ArrayList<>();
        for (Player p : getAllPlayers()) {
            if (p != player) players.add(p);
        }
        return players;
    }

    @Override
    @PostConstruct
    public void insertRootPlayers() {
        if (playerRepository.count() == 0) {
            savePlayer(new Player("Tilly", "Puppylover"));
            savePlayer(new Player("Tom", "Cookingking"));
            savePlayer(new Player("Nuha", "Cutekitten"));
            savePlayer(new Player("Jack", "Headshot"));
            savePlayer(new Player("David", "Jackedjones"));
            savePlayer(new Player("Louis", "Bigboi"));
        }
    }

}
