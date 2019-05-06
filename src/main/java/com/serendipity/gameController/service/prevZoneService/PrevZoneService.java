package com.serendipity.gameController.service.prevZoneService;

public interface PrevZoneService {

    void savePrevZone(com.serendipity.gameController.model.PrevZone prevZone);

    void deletePrevZone(com.serendipity.gameController.model.PrevZone prevZone);

    void deleteAllPrevZones();
}
