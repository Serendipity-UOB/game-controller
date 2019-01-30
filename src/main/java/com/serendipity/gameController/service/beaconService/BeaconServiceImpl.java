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
    public int getClosestBeaconMinor(JSONArray beacons) {
        int closestBeaconMinor = 0;
        int closestBeaconRssi = -100000;
        if (beacons.length() != 0) {
            for (int i = 0; i < beacons.length(); i++) {
                JSONObject beacon = beacons.getJSONObject(i);
                int rssi = beacon.getInt("rssi");
                if ((rssi != 0) && (rssi > closestBeaconRssi)) {
                    closestBeaconMinor = beacon.getInt("beacon_minor");
                    closestBeaconRssi = rssi;
                }
            }
        }
        return closestBeaconMinor;
    }

    @Override
    public long countBeacons() { return beaconRepository.count(); }

    @Override
    public Optional<Beacon> getBeacon(Long id){
        return beaconRepository.findById(id);
    }

    @Override
    public Optional<Beacon> getBeaconByMinor(int minor) { return beaconRepository.findBeaconByMinor(minor); }

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
        Beacon b1 = new Beacon(1, "0KiC");
        Beacon b2 = new Beacon(2, "nHX3");
        Beacon b3 = new Beacon(3, "zP1C");
        saveBeacon(b1);
        saveBeacon(b2);
        saveBeacon(b3);
    }

}
