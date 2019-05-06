package com.serendipity.gameController.service.prevZoneService;

import com.serendipity.gameController.model.PrevZone;
import com.serendipity.gameController.repository.PrevZoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("prevZoneService")
public class PrevZoneServiceImpl implements PrevZoneService {

    @Autowired
    PrevZoneRepository prevZoneRepository;

    @Override
    public void savePrevZone(PrevZone prevZone) {
        prevZoneRepository.save(prevZone);
    }

    @Override
    public void deletePrevZone(PrevZone prevZone) {
        prevZoneRepository.delete(prevZone);
    }

    @Override
    public void deleteAllPrevZones() {
        if (prevZoneRepository.count() != 0) {
            prevZoneRepository.deleteAll();
        }
    }
}
