package com.serendipity.gameController.service.beaconService;

import com.serendipity.gameController.model.Beacon;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.BeaconRepository;
import com.serendipity.gameController.service.playerService.PlayerService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;

@Service("beaconService")
public class BeaconServiceImpl implements BeaconService {

    @Autowired
    BeaconRepository beaconRepository;

    @Autowired
    PlayerService playerService;

    @Override
    public void saveBeacon(Beacon beacon){
        beaconRepository.save(beacon);
    }

    @Override
    public Long countAllBeacons() { return beaconRepository.count(); }

    @Override
    public Optional<Beacon> getBeaconById(Long id){
        return beaconRepository.findById(id);
    }

    @Override
    public Optional<Beacon> getBeaconByMajorAndMinor(int major, int minor) {
        return beaconRepository.findBeaconByMajorAndMinor(major, minor);
    }

    @Override
    public List<Beacon> getBeaconsByMajor(int major) { return beaconRepository.findAllByMajor(major); }

    @Override
    public List<Beacon> getAllBeacons() {
        return beaconRepository.findAll();
    }


    @Override
    public void deleteAllBeacons() {
        if (beaconRepository.count() != 0) {
            beaconRepository.deleteAll();
        }
    }

    @Override
    public void deleteBeaconById(Long id) {
        if (getBeaconById(id).isPresent()) {
            beaconRepository.deleteBeaconById(id);
        }
    }

}
