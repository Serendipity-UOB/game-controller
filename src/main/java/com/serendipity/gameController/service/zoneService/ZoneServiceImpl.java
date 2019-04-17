package com.serendipity.gameController.service.zoneService;

import com.serendipity.gameController.model.Beacon;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.model.Zone;
import com.serendipity.gameController.repository.ZoneRepository;
import com.serendipity.gameController.service.beaconService.BeaconServiceImpl;
import com.serendipity.gameController.service.playerService.PlayerServiceImpl;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service("zoneService")
public class ZoneServiceImpl implements ZoneService {

    @Autowired
    ZoneRepository zoneRepository;

    @Autowired
    PlayerServiceImpl playerService;

    @Autowired
    BeaconServiceImpl beaconService;

    @Override
    public Optional<Zone> getZoneById(Long id) {
        return zoneRepository.findById(id);
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
    public void deleteAllZones() {
        zoneRepository.deleteAll();
    }

    @Override
    public List<Zone> getAllZonesExcept(Long id) { return zoneRepository.findAllByIdNot(id); }

    @Override
    public List<Zone> getAllZonesExceptUN() { return zoneRepository.findAllByNameNot("UN"); }

    @Override
    public void removeBeaconFromZone(Beacon beacon) {
        Zone zone = beacon.getZone();
        zone.getBeacons().remove(beacon);
        saveZone(zone);
    }

    @Override
    public int getNumPlayersWhoseHomeZone(Zone zone) {
        List<Player> players = playerService.getAllPlayers();
        int count = 0;
        for (Player player : players) {
            if (player.hasHomeZone()) {
                if (player.getHomeZone().equals(zone)) {
                    count++;
                }
            }
        }
        return count;
    }

    @Override
    public Optional<Zone> chooseHomeZone(Player player) {
        List<Zone> allZones = getAllZones();
        Optional<Zone> UN = null;
        if (allZones.size() > 0) {
            UN = Optional.of(allZones.get(0));
        } else {
            UN = Optional.empty();
        }
        for (Zone zone : allZones) {
            if(zone.getName().equals("UN")){
                UN = Optional.of(zone);
            }
        }
        return UN;
//        List<Zone> minZones = new ArrayList<>();
//        int minPlayers = Integer.MAX_VALUE;
//        for (Zone zone : allZones) {
//            if (getNumPlayersWhoseHomeZone(zone) < minPlayers) {
//                minPlayers = getNumPlayersWhoseHomeZone(zone);
//                minZones = new ArrayList<>();
//                minZones.add(zone);
//            } else if (getNumPlayersWhoseHomeZone(zone) == minPlayers) {
//                minZones.add(zone);
//            }
//        }
//        if (minZones.size() > 0) {
//            Random random = new Random();
//            return Optional.of(minZones.get(random.nextInt(minZones.size())));
//        } else {
//            return Optional.empty();
//        }
    }

    @Override
    public Optional<Zone> calculateCurrentZone(Player player, JSONArray jsonBeacons) {
        Optional<Beacon> optionalClosestBeacon = Optional.empty();
        int closestRssi = Integer.MIN_VALUE;
        for (int i = 0; i < jsonBeacons.length(); i++) {
            JSONObject jsonBeacon = jsonBeacons.getJSONObject(i);
            int rssi = jsonBeacon.getInt("rssi");
            int major = jsonBeacon.getInt("beacon_major");
            int minor = jsonBeacon.getInt("beacon_minor");

            // Get the beacon we're looking at
            Optional<Beacon> optionalBeacon = beaconService.getBeaconByMajorAndMinor(major, minor);
            if (optionalBeacon.isPresent()) {
                Beacon beacon = optionalBeacon.get();
                boolean isInCurrentZone = false;
                if (player.hasCurrentZone()) {
                    if (player.getCurrentZone().equals(beacon.getZone())) isInCurrentZone = true;
                }
                if ((closestRssi == Integer.MIN_VALUE)       // If we haven't seen any beacons yet
                || (closestRssi == 0 && rssi != 0)
                || ((closestRssi == 0) && (isInCurrentZone)) // Or, if we haven't seen any non-zero rssi yet and this was your current zone
                || (rssi > closestRssi && rssi != 0)) {      // Or, if the rssi we're looking at is non-zero and closer than closestRssi
                    optionalClosestBeacon = Optional.of(beacon);
                    closestRssi = rssi;
                }
            }
        }
        if (optionalClosestBeacon.isPresent()) {
            Beacon closestBeacon = optionalClosestBeacon.get();
            return Optional.of(closestBeacon.getZone());
        } else return Optional.empty();
    }

    @Override
    public List<Zone> getAllZonesExceptUNandOne(Long id){
        List<Zone> notOne = zoneRepository.findAllByIdNot(id);
        // Remove UN from selection
        List<Zone> notUN = new ArrayList<>();
        for(Zone z : notOne) {
            if (!z.getName().equals("UN")) { notUN.add(z); }
        }
        return notUN;
    }

    @Override
    public int locationMapping(Zone location){
        String zoneName = location.getName();
        int mapping = -1;
        switch(zoneName){
            case "UN":
                mapping = 0;
                break;
            case "Italy":
                mapping = 1;
                break;
            case "Sweden":
                mapping = 2;
                break;
            case "Switzerland":
                mapping = 3;
                break;
            case "Czech Republic":
                mapping = 4;
                break;
            default:
                break;
        }
        return mapping;
    }
}
