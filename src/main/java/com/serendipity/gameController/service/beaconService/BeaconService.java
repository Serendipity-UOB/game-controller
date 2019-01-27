package com.serendipity.gameController.service.beaconService;

import com.serendipity.gameController.model.Player;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BeaconService {

    int getClosestBeaconMinor(JSONArray beacons);

    List<Long> getNearbyPlayerIds(Player player, int beaconMinor);

}
