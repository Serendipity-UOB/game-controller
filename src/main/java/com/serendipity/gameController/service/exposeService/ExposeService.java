package com.serendipity.gameController.service.exposeService;

import com.serendipity.gameController.model.Expose;
import com.serendipity.gameController.model.Player;

import java.util.Optional;

public interface ExposeService {

    /*
     * @param expose The expose to save
     */
    void saveExpose(Expose expose);

    /*
     * @param id The id of the expose you are looking for.
     * @return An optional of the expose with that id.
     */
    Optional<Expose> getExpose(Long id);

    /*
     *
     */
    void deleteAllExposes();
}
