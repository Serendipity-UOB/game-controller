package com.serendipity.gameController.service.beaconService;

import org.json.JSONArray;
import com.serendipity.gameController.model.Beacon;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface BeaconService {

    /*
     * @param beacons A JSONArray of {beacon_minor, rssi}.
     * @return The minor of the closest beacon.
     */
    int getClosestBeaconMinor(JSONArray beacons);

    /*
     * @return The number of beacons in the database.
     */
    long countBeacons();

    /*
     * @param id The id of the beacon you want.
     * @return An optional of the beacon matching the given id.
     */
    Optional<Beacon> getBeacon(Long id);

    /*
     * @param minor The minor of the beacon you want.
     * @return An optional of the beacon matching the given minor.
     */
    Optional<Beacon> getBeaconByMinor(int minor);
}
