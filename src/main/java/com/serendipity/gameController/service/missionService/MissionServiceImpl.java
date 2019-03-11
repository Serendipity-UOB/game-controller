package com.serendipity.gameController.service.missionService;

import com.serendipity.gameController.model.Mission;
import com.serendipity.gameController.repository.MissionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("missionService")
public class MissionServiceImpl implements MissionService {

    @Autowired
    MissionRepository missionRepository;

    @Override
    public void saveMission(Mission mission){
        missionRepository.save(mission);
    }

    @Override
    public void deleteAllMissions() {
        missionRepository.deleteAll();
    }

    @Override
    public Optional<Mission> getMission(Long id){
        return missionRepository.findById(id);
    }


}
