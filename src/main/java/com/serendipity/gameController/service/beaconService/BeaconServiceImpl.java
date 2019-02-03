package com.serendipity.gameController.service.beaconService;

import com.serendipity.gameController.model.Beacon;
import com.serendipity.gameController.repository.BeaconRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("beaconService")
public class BeaconServiceImpl implements BeaconService {

    @Autowired
    BeaconRepository beaconRepository;

    @Override
    public void saveBeacon(Beacon beacon){
        beaconRepository.save(beacon);
    }

    @Override
    public int getClosestBeaconMajor(JSONArray beacons) {
        int closestBeaconMajor = 0;
        int closestBeaconRssi = -100000;
        if (beacons.length() != 0) {
            for (int i = 0; i < beacons.length(); i++) {
                JSONObject beacon = beacons.getJSONObject(i);
                int rssi = beacon.getInt("rssi");
                if ((rssi != 0) && (rssi > closestBeaconRssi)) {
                    closestBeaconMajor = beacon.getInt("beacon_major");
                    closestBeaconRssi = rssi;
                }
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
        Beacon b1 = new Beacon(1,1, "0KiC");
        Beacon b2 = new Beacon(1,2, "nHX3");
        Beacon b3 = new Beacon(1,3, "zP1C");
        saveBeacon(b1);
        saveBeacon(b2);
        saveBeacon(b3);
    }

}
