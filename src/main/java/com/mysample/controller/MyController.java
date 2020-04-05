package com.mysample.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cloudtencent.TLSSigAPIv2;
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
import java.util.*;

@Controller
public class MyController {

    @RequestMapping("invokeMethod")
    @ResponseBody
    public Map<String, Object> invokeMethod(@RequestBody String jsonBody) {

        Map<String, Object> map = new HashMap<>();

        //
        try {

            JSONObject jso = JSON.parseObject(jsonBody);
            //
            if (jso.getJSONObject("Param").getString("random").equals("")) {
                Random random = new Random();//实例化一个random的对象
                int nRandom = random.nextInt(90000000) + 10000000;//为变量赋随机值10000000-99999999 产生八位数的随机整数
                jso.getJSONObject("Param").put("random", String.valueOf(nRandom));
            }

            if (jso.getJSONObject("Param").getLong("sdkappid") == 0) {
                jso.getJSONObject("Param").put("sdkappid", 1400346991);
            }

            if (jso.getJSONObject("Param").getString("key").equals("")) {
                jso.getJSONObject("Param").put("key", "2348dc41930db99a8aa6f53633fc922ac735ff89c77573017d1b2c73eba99cd9");
            }

            if (jso.getJSONObject("Param").getString("identifier").equals("")) {
                jso.getJSONObject("Param").put("identifier", "administrator");
            }

            if (jso.getJSONObject("Param").getLong("expiretime") == 0) {
                jso.getJSONObject("Param").put("expiretime", 604800);
            }

            if (jso.getJSONObject("Param").getString("usersig").equals("")) {
                TLSSigAPIv2 tlsSigAPIv2 = new TLSSigAPIv2(jso.getJSONObject("Param").getLong("sdkappid"), jso.getJSONObject("Param").getString("key"));
                String usersig = tlsSigAPIv2.genSig(jso.getJSONObject("Param").getString("identifier"), jso.getJSONObject("Param").getLong("expiretime"));
                jso.getJSONObject("Param").put("usersig", usersig);
            }
            //

            map = HttpPost(jso.getString("Scheme"), jso.getString("Host"), jso.getString("Action"), jso.getString("Header"), jso.getString("Param"), jso.getString("Body"));

        } catch (Exception ex) {
            map.put("result", "error");
            map.put("errorInfo", ex.getMessage());
        }
        //
        return map;
    }

    public Map<String, Object> HttpPost(String Scheme, String Host, String Path, String jsonHeader, String jsonParam, String jsonBody)
            throws URISyntaxException, IOException {
        Map<String, Object> map = new HashMap<>();

        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setScheme(Scheme);
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

                map.put("result", "1");

                jsonObject = JSON.parseObject(responseEntity);
                map.put("data", toMap(jsonObject));
            }

        } catch (Exception ex) {
            map.put("result", "0");
            map.put("data", ex.getMessage());

            response.close();
        } finally {
            response.close();
        }

        return map;
    }

    public Map<String, Object> toMap(JSONObject jso) {
        Map<String, Object> map = new HashMap<String, Object>();

        for (Map.Entry<String, Object> entry : jso.entrySet()) {
            if (entry.getValue() != null) {
                if (entry.getValue() instanceof JSONObject) {
                    map.put(entry.getKey(), toMap((JSONObject) entry.getValue()));
                } else if (entry.getValue() instanceof JSONArray) {
                    map.put(entry.getKey(), toList((JSONArray) entry.getValue()));
                } else {
                    map.put(entry.getKey(), entry.getValue().toString());
                }
            } else {
                map.put(entry.getKey(), null);
            }
        }

        return map;
    }

    public List<Object> toList(JSONArray jsa) {
        List<Object> list = new ArrayList<Object>();
        for (Iterator iterator = jsa.iterator(); iterator.hasNext();) {
            Object next = iterator.next();
            if (next != null) {
                if (next instanceof JSONArray) {
                    list.add(toList((JSONArray) next));
                } else if (next instanceof JSONObject) {
                    list.add(toMap((JSONObject) next));
                } else {
                    list.add(next);
                }
            } else {
                list.add(null);
            }
        }

        return list;
    }

}
