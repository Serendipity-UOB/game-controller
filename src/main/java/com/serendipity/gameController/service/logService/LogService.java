package com.serendipity.gameController.service.logService;

import com.serendipity.gameController.model.Log;
import com.serendipity.gameController.model.LogType;
import org.json.JSONArray;

import java.time.LocalTime;
import java.util.Optional;

public interface LogService {

    /*
     * @param type The type of the log to save
     * @param id The id of the interaction to save
     * @param time The time the interaction occurred to save
     */
    void saveLog(LogType type, Long id, LocalTime time);

    /*
     * @param id The id of the log you are looking for.
     * @return An optional of the log with that id.
     */
    Optional<Log> getLog(Long id);

    /*
     *
     */
    void deleteAllLogs();

    /*
     * @return A JSONArray with all new logs
     */
    JSONArray logOutput();
}
