package com.serendipity.gameController.service.missionService;

import com.serendipity.gameController.model.Game;
import com.serendipity.gameController.model.Mission;
import com.serendipity.gameController.model.Player;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface MissionService {

    /*
     * @param mission The mission to save
     */
    void saveMission(Mission mission);

    /*
     *
     */
    List<Mission> getAllMissions();

    /*
     *
     */
    void deleteAllMissions();

    /*
     *
     */
    void unassignAllMissions();

    /*
     * @param id The id of the mission you are looking for.
     * @return An optional of the mission with that id.
     */
    Optional<Mission> getMission(Long id);

    /*
     * @param player The player the mission is being assigned for
     * @return An optional of the mission created
     */
    Optional<Mission> createMission(Player player);
}
