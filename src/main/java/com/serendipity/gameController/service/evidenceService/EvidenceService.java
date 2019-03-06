package com.serendipity.gameController.service.evidenceService;

import com.serendipity.gameController.model.Evidence;
import org.springframework.stereotype.Service;

@Service
public interface EvidenceService {

    /*
     * @param evidence The evidence to save.
     */
    void saveEvidence(Evidence evidence);

}
