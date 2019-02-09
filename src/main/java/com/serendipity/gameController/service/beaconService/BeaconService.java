package com.serendipity.gameController.service.beaconService;

import org.json.JSONArray;
import com.serendipity.gameController.model.Beacon;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public interface BeaconService {

    /*
     * @param beacon The beacon object to save to the database.
     */
    void saveBeacon(Beacon beacon);

    /*
     * @param id The id of the beacon you want.
     * @return An optional of the beacon matching the given id.
     */
    Optional<Beacon> getBeacon(Long id);

    /*
     * @param major The major of the beacon you want.
     * @return A list- of the beacon matching the given major.
     */
    List<Beacon> getBeaconByMajor(int major);

    /*
     * @return A list containing all the beacons in the database.
     */
    List<Beacon> getAllBeacons();

    /*
     * @return The number of beacons in the database.
     */
    long countAllBeacons();

    /*
     * Delete all the beacons in the database.
     */
    void deleteAllBeacons();

    /*
     * @param beaconId The id of the beacon to delete
     */
    void deleteBeaconById(long beaconId);

    /*
     * @param beacons A JSONArray of {beacon_major, beacon_minor, rssi}.
     * @return The major of the closest beacon.
     */
    int getClosestBeaconMajor(Long playerId, JSONArray beacons);

    /*
     * @return A map of beacon keys mapping to a value of how many
     * times they've been assigned as a home beacon.
     */
    Map<Integer, Integer> sumBeacons();
}
