package com.serendipity.gameController.service.zoneService;

import com.serendipity.gameController.model.Beacon;
import com.serendipity.gameController.model.Zone;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ZoneService {

    /*
     * @param id The zone's id.
     */
    Zone getZoneById(Long id);

    /*
     * @return A list of all the zones in the database.
     */
    List<Zone> getAllZones();

    /*
     * @param zone The zone to save.
     */
    void saveZone(Zone zone);

    /*
     * @param zone The zone to delete.
     */
    void deleteZone(Zone zone);

    /*
     * @param beacon The beacon to remove from its zone.
     */
    void removeBeaconFromZone(Beacon beacon);

}
