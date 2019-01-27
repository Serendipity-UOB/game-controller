package com.serendipity.gameController.service.playerService;

import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.PlayerRepository;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

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
    public Player getPlayerByHackerName(String hackerName) { return playerRepository.findByHackerName(hackerName); }

    @Override
    public long countPlayer() { return playerRepository.count(); }

    @Override
    public List<Player> getAllPlayers() {
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
    public List<Player> getAllPlayersExcept(List<Player> exceptPlayers) {
        List<Player> players = new ArrayList<>();
        for (Player p : getAllPlayers()) {
            if (!exceptPlayers.contains(p)) players.add(p);
        }
        return players;
    }

    @Override
    public List<JSONObject> getAllPlayersStartInfo() {
        List<JSONObject> players = new ArrayList<>();
        for (Player p : getAllPlayers()) {
            JSONObject obj = new JSONObject();
            obj.put("id", p.getId());
            obj.put("real_name", p.getRealName());
            obj.put("hacker_name", p.getHackerName());
            players.add(obj);
        }

        return players;
    }

    public void createPlayers() {
        if (playerRepository.count() != 0) {
//            informationService.deleteAll();
            playerRepository.deleteAll();
        }
        List<String> hackerNames = Arrays.asList("Cookingking", "Puppylover", "Headshot", "Guitarhero", "Cutiekitten", "Jackedjones");
        List<String> realNames = Arrays.asList("Tom", "Tilly", "Louis", "Nuha", "Jack", "David");
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
    public void assignHome(Player player, int home) {
        player.setHomeBeacon(home);
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

    @Override
    public void halfInformation(Player player) {
//        List<Information> information = informationService.getAllInformationForOwner(player);
//        for (Information info : information) {
//            info.setInteractions(info.getInteractions()/2);
//            informationService.saveInformation(info);
//        }
    }

    @Override
    public int getTotalInformation(Player player) {
//        List<Information> information = informationService.getAllInformationForOwner(player);
        int total = 0;
//        for (Information info : information) {
//            total += info.getInteractions();
//        }
        return total;
    }

    @Override
    public int getPlayerWeight(Player player) {
        int totalInformation = getTotalInformation(player);
        int averageInformation = totalInformation/getAllPlayers().size();
        int kills = player.getKills();
        int weight = averageInformation + kills;
        return weight;
    }

}
