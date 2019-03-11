package com.serendipity.gameController.service.evidenceService;

import com.serendipity.gameController.model.Evidence;
import com.serendipity.gameController.model.Exchange;
import com.serendipity.gameController.model.Player;
import com.serendipity.gameController.repository.EvidenceRepository;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("evidenceService")
public class EvidenceServiceImpl implements EvidenceService {

    @Autowired
    EvidenceRepository evidenceRepository;

    @Override
    public void saveEvidence(Evidence evidence) {
        evidenceRepository.save(evidence);
    }

    @Override
    public void deleteAllEvidence() {
        evidenceRepository.deleteAll();
    }

    @Override
    public JSONArray evidenceListToJsonArray(List<Evidence> evidenceList) {
        JSONArray jsonEvidenceList = new JSONArray();
        for (Evidence evidence : evidenceList) {
            JSONObject jsonEvidence = new JSONObject();
            jsonEvidence.put("player_id", evidence.getPlayerAbout().getId());
            jsonEvidence.put("amount", evidence.getAmount());
            jsonEvidenceList.put(jsonEvidence);
        }
        return jsonEvidenceList;
    }

}
