package com.mysample.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class MyController {

    @RequestMapping("invokeMethod")
    @ResponseBody
    public Map<String, Object> invokeMethod(@RequestParam(value="Action") String Action,
                               @RequestBody String jsonBody) {

        Map<String, Object> map = new HashMap<>();

        map.put("result", "complete");

        return map;

    }

}
