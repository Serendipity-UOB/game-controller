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

    @Override
    public int getClosestBeaconMajor(Long playerId, JSONArray beacons) {
        int closestBeaconMajor = 0;
//        int closestBeaconRssi = -100000;
//        JSONArray zeroBeacons = new JSONArray();
//        if (beacons.length() != 0) {
//            for (int i = 0; i < beacons.length(); i++) {
//                JSONObject beacon = beacons.getJSONObject(i);
//                int rssi = beacon.getInt("rssi");
//                if(rssi == 0) { zeroBeacons.put(beacon); }
//                else if ((rssi > closestBeaconRssi)) {
//                    closestBeaconMajor = beacon.getInt("beacon_major");
//                    closestBeaconRssi = rssi;
//                }
//            }
//        }
//
//        if (closestBeaconMajor == 0 && zeroBeacons.length() != 0) {
//            Optional<Player> opPlayer = playerService.getPlayer(playerId);
//            if(opPlayer.isPresent()) {
//                Player player = opPlayer.get();
//                Random randNum = new Random();
//                int n = randNum.nextInt(zeroBeacons.length());
//                closestBeaconMajor = zeroBeacons.getJSONObject(n).getInt("beacon_major");
//                for(int i = 0; i < zeroBeacons.length(); i++) {
//                    if (zeroBeacons.getJSONObject(i).getInt("beacon_major") == player.getNearestBeaconMajor()) {
//                        closestBeaconMajor = zeroBeacons.getJSONObject(i).getInt("beacon_major");
//                    }
//                }
//            }
//            else {
////                TODO: Error for no player matching id given
//                // Return -1 and deal with in controller by returning 400 BAD REQUEST?
//            }
//        }
        return closestBeaconMajor;
    }

    @Override
    public List<Beacon> getAllBeaconsExcept(int major) {
        return beaconRepository.findAllByMajorNot(major);
    }

//    @PostConstruct
//    void addBeacons() {
//        Beacon b1 = new Beacon(2,1, "0KiC");
//        Beacon b2 = new Beacon(2,4, "u0dd");
//        Beacon b3 = new Beacon(1,2, "nHX3");
//        Beacon b4 = new Beacon(1,5, "jpMn");
//        saveBeacon(b1);
//        saveBeacon(b2);
//        saveBeacon(b3);
//        saveBeacon(b4);
//    }

}
