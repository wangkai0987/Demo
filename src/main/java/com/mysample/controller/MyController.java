package com.mysample.controller;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
public class MyController {

    @RequestMapping("invokeMethod")
    @ResponseBody
    public Map<String, Object> invokeMethod(@RequestParam(value="Action") String Action,
                               @RequestBody String jsonBody) throws IOException {

        Map<String, Object> map = new HashMap<>();

        map.put("result", "complete");
        map.put("Action", Action);
        map.put("jsonBody", jsonBody);

        //

//        CloseableHttpClient httpclient = HttpClients.createDefault();
//        HttpGet httpget = new HttpGet("http://localhost/");
//        CloseableHttpResponse response = httpclient.execute(httpget);
//        try {
//
//            // TODO
//
//        } finally {
//
//            response.close();
//
//        }

        //

        return map;

    }

}
