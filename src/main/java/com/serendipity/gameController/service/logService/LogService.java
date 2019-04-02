package com.serendipity.gameController.service.logService;

import com.serendipity.gameController.model.Log;
import com.serendipity.gameController.model.LogType;
import com.serendipity.gameController.model.Zone;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface LogService {

    /*
     * @param type The type of the log
     * @param id The id of the interaction
     * @param time The time the interaction occurred
     * @param zone The zone the interaction happened at
     */
    void saveLog(LogType type, Long id, LocalTime time, Zone zone);

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

    /*
     * @return A JSONArray with all new zone colours and sizes
     */
    JSONArray zoneDisplay();

    /*
     * @return A JSONArray with the top players
     */
    JSONArray topPlayers();

    /*
     * @return A JSONObject with the minutes and seconds remaining
     */
    JSONObject timeRemaining();

    /*
     *
     */
    void printToCSV(List<String> data, String filename) throws IOException;

    /*
     *
     */
    void initCSVs();
}
