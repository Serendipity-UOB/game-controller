package com.serendipity.gameController.service.missionService;

import com.serendipity.gameController.model.Game;
import com.serendipity.gameController.model.Mission;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.MissionRepository;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service("missionService")
public class MissionServiceImpl implements MissionService {

    @Autowired
    MissionRepository missionRepository;

    @Autowired
    PlayerServiceImpl playerService;

    @Override
    public void saveMission(Mission mission){
        missionRepository.save(mission);
    }

    @Override
    public void deleteAllMissions() {
        missionRepository.deleteAll();
    }

    @Override
    public List<Mission> getAllMissions() {
        return missionRepository.findAll();
    }

    @Override
    public void unassignAllMissions() {
        List<Player> allPlayers = playerService.getAllPlayers();
        for (Player player : allPlayers) {
            player.setMissionAssigned(null);
            playerService.savePlayer(player);
        }
    }

    @Override
    public Optional<Mission> getMission(Long id){
        return missionRepository.findById(id);
    }

    @Override
    public Optional<Mission> createMission(Player player) {
        List<Player> players = playerService.getAllPlayersExcept(player);
        Random random = new Random(System.currentTimeMillis());
        // Ensure there's enough players
        if(players.size() > 1) {
            // Get 2 random targets
            Player target1 = players.get(random.nextInt(players.size()));
            players.remove(target1);
            Player target2 = players.get(random.nextInt(players.size()));
            players.remove(target2);
            // Create mission
            Mission mission = new Mission(target1, target2);
            saveMission(mission);
            return Optional.of(mission);
        } else {
            return Optional.empty();
        }
    }

}
