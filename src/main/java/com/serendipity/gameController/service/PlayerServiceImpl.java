package com.serendipity.gameController.service;

import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

}
