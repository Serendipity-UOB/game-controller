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
    Optional<Beacon> getBeaconById(Long id);

    /*
     * @param major The beacon major.
     * @param minor The beacon minor.
     * @return An optional of the beacon matching the given major and minor.
     */
    Optional<Beacon> getBeaconByMajorAndMinor(int major, int minor);

    /*
     * @param major The major of the beacon you want.
     * @return A list- of the beacon matching the given major.
     */
    List<Beacon> getBeaconsByMajor(int major);

    /*
     * @return A list containing all the beacons in the database.
     */
    List<Beacon> getAllBeacons();

    /*
     * @return The number of beacons in the database.
     */
    Long countAllBeacons();

    /*
     * Delete all the beacons in the database.
     */
    void deleteAllBeacons();

    /*
     * @param beaconId The id of the beacon to delete
     */
    void deleteBeaconById(Long beaconId);

}
