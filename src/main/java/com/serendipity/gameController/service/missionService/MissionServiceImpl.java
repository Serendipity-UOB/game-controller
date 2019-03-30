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
            player.setMissionsAssigned(new ArrayList<>());
            playerService.savePlayer(player);
        }
    }

    @Override
    public Optional<Mission> getMission(Long id){
        return missionRepository.findById(id);
    }

    @Override
    public Mission createMission(Player player, Game game, Player target1, Player target2) {
        // Find length of game in seconds
        String datePattern = "HH:mm:ss";
        DateTimeFormatter df = DateTimeFormatter.ofPattern(datePattern);
        LocalTime gameEnd = game.getEndTime();
        String endString = df.format(gameEnd);
        LocalTime gameStart = game.getStartTime();
        String startString = df.format(gameStart);
        String[] unitsEnd = endString.split(":");
        String[] unitsStart = startString.split(":");
        int end = 3600 * Integer.parseInt(unitsEnd[0]) + 60 * Integer.parseInt(unitsEnd[1]) +
                Integer.parseInt(unitsEnd[2]);
        int start = 3600 * Integer.parseInt(unitsStart[0]) + 60 *
                Integer.parseInt(unitsStart[1]) + Integer.parseInt(unitsStart[2]);

        // Find upper and lower boundaries for mission time assignment
        int quarter = (end - start) / 4;
        int upper = end - quarter;
        int lower = start + quarter;

        // Pick random time
        Random randomTime = new Random();
        int time = quarter + randomTime.nextInt(upper - lower);
        LocalTime missionStart = gameStart.plus(time, ChronoUnit.SECONDS);
        LocalTime missionEnd = missionStart.plus(30, ChronoUnit.SECONDS);
        // TODO: May need to make the player assignment more dynamic
        // TODO: May need to consider multiple missions
        // Save new mission
        Mission mission = new Mission(player, missionStart, missionEnd, target1, target2);
        saveMission(mission);

        return mission;
    }


}
