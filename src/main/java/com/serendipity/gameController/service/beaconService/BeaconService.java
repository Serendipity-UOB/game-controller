package com.serendipity.gameController.service.beaconService;

import com.serendipity.gameController.model.Player;
import org.json.JSONArray;
import com.serendipity.gameController.model.Beacon;
import org.springframework.stereotype.Service;

import java.util.List;

import java.util.Optional;

@Service
public interface BeaconService {

    int getClosestBeaconMinor(JSONArray beacons);

    List<Long> getNearbyPlayerIds(Player player, int beaconMinor);


    long countBeacons();

    Optional<Beacon> getBeacon(Long id);

    Optional<Beacon> getBeaconByMinor(int minor);
}
