package com.serendipity.gameController.service.interceptService;

import com.serendipity.gameController.model.Intercept;
import com.serendipity.gameController.model.Player;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface InterceptService {

    /*
     * @param mission The intercept to save
     */
    void saveIntercept(Intercept intercept);

    /*
     *
     */
    void deleteAllIntercepts();

    /*
     * @param id The id of the intercept you are looking for.
     * @return An optional of the intercept with that id.
     */
    Optional<Intercept> getIntercept(Long id);

    /*
     * @param player The player of the intercept intercepter you are looking for.
     * @return An optional of the intercept with that intercepter.
     */
    Optional<Intercept> getInterceptByPlayer(Player player);
}
