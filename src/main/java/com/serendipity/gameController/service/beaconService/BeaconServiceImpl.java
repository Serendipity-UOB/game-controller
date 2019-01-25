package com.serendipity.gameController.service.beaconService;

import com.serendipity.gameController.model.Beacon;
import com.serendipity.gameController.repository.BeaconRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service("beaconService")
public class BeaconServiceImpl implements BeaconService {

    @Autowired
    BeaconRepository beaconRepository;

    @Override
    public long countBeacons() { return beaconRepository.count(); }

    @Override
    public Optional<Beacon> getBeacon(Long id){
        return beaconRepository.findById(id);
    }
}
