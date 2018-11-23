package com.serendipity.gameController.service.playerService;

import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.PlayerRepository;
import com.serendipity.gameController.service.informationService.InformationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service("playerService")
public class PlayerServiceImpl implements PlayerService {

    @Autowired
    PlayerRepository playerRepository;

    @Autowired
    InformationServiceImpl informationService;

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

    public void createPlayers() {
        if (playerRepository.count() != 0) {
            informationService.deleteAll();
            playerRepository.deleteAll();
        }
        List<String> hackerNames = Arrays.asList("Cookingking", "Puppylover", "Headshot", "Guitarhero", "Cutiekitten"/*, "Jackedjones"*/);
        List<String> realNames = Arrays.asList("Tom", "Tilly", "Louis", "Nuha", "Jack"/*, "David"*/);
        Collections.shuffle(hackerNames);
        for (int i = 0; i < realNames.size(); i++) {
            savePlayer(new Player(realNames.get(i), hackerNames.get(i)));
        }
    }

    @Override
    public void incKills(Player player) {
        player.setKills(player.getKills()+1);
        playerRepository.save(player);
    }

    @Override
    public void assignTargets() {
        List<Player> players = getAllPlayers();
        List<Player> unassigned = getAllPlayers();
        Random random = new Random();
        for (Player player : players) {
            //Get the list of players that are unassigned and not me
            List<Player> chooseFrom = new ArrayList<>();
            for (Player p : unassigned) {
                if (!(p.equals(player))) {
                    chooseFrom.add(p);
                }
            }
            //Check to see if chooseFrom is empty
            if (chooseFrom.isEmpty()) {
                //Swap the last person's target with someone who doesn't have them as a target
                //Try swapping with the first person's target
                if (!(players.get(0).equals(player))) {
                    //Then swap their targets
                    players.get(0).setTarget(player);
                    player.setTarget(players.get(0));

                } else {
                    players.get(1).setTarget(player);
                    player.setTarget(players.get(1));
                }
            } else {
                //Pick random from chooseFrom
                int i = random.nextInt(chooseFrom.size());
                Player target = chooseFrom.get(i);
                player.setTarget(target);
                //Reset unassigned
                unassigned.remove(target);
            }
        }
        //Update the database
        for (Player player : players) {
            savePlayer(player);
        }

    }


}
