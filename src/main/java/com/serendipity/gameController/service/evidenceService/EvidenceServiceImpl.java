package com.serendipity.gameController.service.evidenceService;

import com.serendipity.gameController.model.Evidence;
import com.serendipity.gameController.repository.EvidenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("evidenceService")
public class EvidenceServiceImpl implements EvidenceService {

    @Autowired
    EvidenceRepository evidenceRepository;

    @Override
    public void saveEvidence(Evidence evidence) {
        evidenceRepository.save(evidence);
    }

}
