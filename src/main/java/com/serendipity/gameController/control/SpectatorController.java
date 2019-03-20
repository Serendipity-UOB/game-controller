package com.serendipity.gameController.control;

import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin
public class SpectatorController {

    @GetMapping(value="/spectator")
    public ResponseEntity<String> hello() {
        // Create JSON object for response body
        JSONObject output = new JSONObject();
        // set default response status
        HttpStatus responseStatus = HttpStatus.OK;

        output.put("message", "hello");

        return new ResponseEntity<>(output.toString(), responseStatus);
    }

}
