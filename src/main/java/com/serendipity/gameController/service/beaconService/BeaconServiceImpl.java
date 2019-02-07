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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

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
    public int getClosestBeaconMajor(Long playerId, JSONArray beacons) {
        int closestBeaconMajor = 0;
        int closestBeaconRssi = -100000;
        JSONArray zeroBeacons = new JSONArray();
        if (beacons.length() != 0) {
            for (int i = 0; i < beacons.length(); i++) {
                JSONObject beacon = beacons.getJSONObject(i);
                int rssi = beacon.getInt("rssi");
                if(rssi == 0) { zeroBeacons.put(beacon); }
                else if ((rssi > closestBeaconRssi)) {
                    closestBeaconMajor = beacon.getInt("beacon_major");
                    closestBeaconRssi = rssi;
                }
            }
        }

        if (closestBeaconMajor == 0 && zeroBeacons.length() != 0) {
            Optional<Player> opPlayer = playerService.getPlayer(playerId);
            if(opPlayer.isPresent()) {
                Player player = opPlayer.get();
                Random randNum = new Random();
                int n = randNum.nextInt(zeroBeacons.length());
                closestBeaconMajor = zeroBeacons.getJSONObject(n).getInt("beacon_major");
                for(int i = 0; i < zeroBeacons.length(); i++) {
                    if (zeroBeacons.getJSONObject(i).getInt("beacon_major") == player.getNearestBeaconMajor()) {
                        closestBeaconMajor = zeroBeacons.getJSONObject(i).getInt("beacon_major");
                    }
                }
            }
            else {
//                TODO: Error for no player matching id given
            }
        }
        return closestBeaconMajor;
    }

    @Override
    public long countBeacons() { return beaconRepository.count(); }

    @Override
    public Optional<Beacon> getBeacon(Long id){
        return beaconRepository.findById(id);
    }

    @Override
    public List<Beacon> getBeaconByMajor(int major) { return beaconRepository.findAllByMajor(major); }

    @Override
    public List<Beacon> getAllBeacons() {
        return beaconRepository.findAll();
    }


    @Override
    public void deleteBeacons() {
        if (beaconRepository.count() != 0) {
            beaconRepository.deleteAll();
        }
    }

    @Override
    public void deleteBeaconById(long beacon_id) {
        if (getBeacon(beacon_id).isPresent()) {
            beaconRepository.deleteBeaconById(beacon_id);
        }
    }

    @PostConstruct
    void addBeacons() {
        Beacon b1 = new Beacon(2,1, "0KiC");
        Beacon b2 = new Beacon(2,4, "u0dd");
        Beacon b3 = new Beacon(1,2, "nHX3");
        Beacon b4 = new Beacon(1,5, "jpMn");
        saveBeacon(b1);
        saveBeacon(b2);
        saveBeacon(b3);
        saveBeacon(b4);
    }

}
