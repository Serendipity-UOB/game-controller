package com.serendipity.gameController.service.beaconService;

import com.serendipity.gameController.repository.BeaconRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("beaconService")
public class BeaconServiceImpl implements BeaconService {

    @Autowired
    BeaconRepository beaconRepository;

}
