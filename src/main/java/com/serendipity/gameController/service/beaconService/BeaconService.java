package com.serendipity.gameController.service.beaconService;

import com.serendipity.gameController.model.Player;
import org.json.JSONArray;
import com.serendipity.gameController.model.Beacon;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Optional;

@Service
public interface BeaconService {

    void saveBeacon(Beacon beacon);

    /*
     * @param beacons A JSONArray of {beacon_minor, rssi}
     * @return The minor of the closest beacon
     */
    int getClosestBeaconMinor(JSONArray beacons);

    long countBeacons();

    Optional<Beacon> getBeacon(Long id);

    Optional<Beacon> getBeaconByMinor(int minor);

    void deleteBeacons();

    void deleteBeaconById(long beacon_id);
}
