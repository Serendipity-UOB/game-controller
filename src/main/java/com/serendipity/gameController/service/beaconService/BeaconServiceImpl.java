package com.serendipity.gameController.service.beaconService;

import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.model.Beacon;
import com.serendipity.gameController.repository.BeaconRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
        JSONObject beacon = beacons.getJSONObject(0);
        int closestBeaconRssi = beacon.getInt("rssi");
        int closestBeaconMinor = beacon.getInt("beacon_minor");
        for (int i = 1; i < beacons.length(); i++) {
            beacon = beacons.getJSONObject(i);
            if (beacon.getInt("rssi") > closestBeaconRssi) {
                closestBeaconMinor = beacon.getInt("beacon_minor");
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
}
