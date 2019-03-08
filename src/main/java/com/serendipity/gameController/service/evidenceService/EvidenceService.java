package com.serendipity.gameController.service.evidenceService;

import com.serendipity.gameController.model.Evidence;
import com.serendipity.gameController.model.Player;
import org.json.JSONArray;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface EvidenceService {

    /*
     * @param evidence The evidence to save.
     */
    void saveEvidence(Evidence evidence);

    /*
     * @param evidenceList The list of evidence to convert.
     * @return The same list of evidence as a JSONArray with attributes "player_id" and "amount".
     */
    JSONArray evidenceListToJsonArray(List<Evidence> evidenceList);

}
