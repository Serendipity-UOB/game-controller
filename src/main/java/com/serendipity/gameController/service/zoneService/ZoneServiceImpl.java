package com.serendipity.gameController.service.zoneService;

import com.serendipity.gameController.model.Beacon;
import com.serendipity.gameController.model.Zone;
import com.serendipity.gameController.repository.ZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("zoneService")
public class ZoneServiceImpl implements ZoneService {

    @Autowired
    ZoneRepository zoneRepository;

    @Override
    public Zone getZoneById(Long id) {
        return zoneRepository.findById(id).get();
    }

    @Override
    public List<Zone> getAllZones() {
        return zoneRepository.findAll();
    }

    @Override
    public void saveZone(Zone zone) {
        zoneRepository.save(zone);
    }

    @Override
    public void deleteZone(Zone zone) {
        zoneRepository.delete(zone);
    }

    @Override
    public void removeBeaconFromZone(Beacon beacon) {
        Zone zone = beacon.getZone();
        zone.getBeacons().remove(beacon);
        saveZone(zone);
    }

}
