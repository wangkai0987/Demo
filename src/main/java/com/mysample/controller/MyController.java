package com.mysample.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
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
        map.put("Action", Action);
        map.put("jsonBody", jsonBody);
        //
        try {

            HttpPost("49.234.148.35:8080", "/demo/invokeMethod", "{\n" +
                    "  \"Content-Type\": \"application/json\",\n" +
                    "  \"Transfer-Encoding\": \"chunked\",\n" +
                    "  \"Keep-Alive\": \"timeout=58\"\n" +
                    "}", "{\n" +
                    "  \"Action\": \"123\"\n" +
                    "}", jsonBody);

        } catch (Exception ex) {

        }
        //
        return map;
    }

    public Map<String, Object> HttpPost(String Host, String Path, String jsonHeader, String jsonParam, String jsonBody)
            throws URISyntaxException, IOException {
        Map<String, Object> map = new HashMap<>();

        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setHost(Host);
        uriBuilder.setPath(Path);
        JSONObject jsonObject = JSON.parseObject(jsonParam);
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            uriBuilder.addParameter(entry.getKey(), entry.getValue().toString());
        }

        URI uri = uriBuilder.build();
        HttpPost httppost = new HttpPost(uri);
        //
        jsonObject = JSON.parseObject(jsonHeader);
        for (Map.Entry<String, Object> entry : jsonObject.entrySet()) {
            httppost.addHeader(entry.getKey(), entry.getValue().toString());
        }
        //
        StringEntity se = new StringEntity(jsonBody, "UTF-8");
        se.setContentEncoding("UTF-8");
        httppost.setEntity(se);

        CloseableHttpClient httpclient = HttpClients.createDefault();
        CloseableHttpResponse response = httpclient.execute(httppost);

        try {

            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() == 200) {
                String responseEntity = EntityUtils.toString(response.getEntity());

                map.put("result", "ok");
                map.put("jsonResult", responseEntity);
            }

        } catch (Exception ex) {
            map.put("result", "error");
            map.put("errorInfo", ex.getMessage());

            response.close();
        } finally {
            response.close();
        }

        return map;
    }

}
