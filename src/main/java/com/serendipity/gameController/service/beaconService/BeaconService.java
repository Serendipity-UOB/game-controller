package com.serendipity.gameController.service.beaconService;

import com.serendipity.gameController.model.Beacon;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface BeaconService {

    long countBeacons();

    Optional<Beacon> getBeacon(Long id);

}
