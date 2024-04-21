package com.yueliu;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import fi.iki.elonen.NanoHTTPD;
import com.time.nlp.*;
import com.time.util.DateUtil;

import java.net.*;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

/*
 * #%L
 * yueliu
 * %%
 * Copyright (C) 2012 - 2024 nanohttpd
 * %%
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * 3. Neither the name of the nanohttpd nor the names of its contributors
 *    may be used to endorse or promote products derived from this software without
 *    specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING,
 * BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

/**
 * Hello world!
 */
public class App extends NanoHTTPD {

    public App() throws IOException {
        super(8234);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        System.out.println("\nRunning! Point your browsers to http://localhost:8234/ \n");
    }

    public static void main(String[] args) {
        try {
            new App();

        } catch (IOException ioe) {
            System.err.println("Couldn't start server:\n" + ioe);
        }
    }

    @Override
    public Response serve(IHTTPSession session) {
        System.err.println("wolaile!!!");
        if (session.getMethod() == Method.POST && session.getUri().equals("/time")) {
            Map<String, String> body = new HashMap<>();
            try {
                session.parseBody(body);
                body = session.getParms();
            } catch (IOException e) {
                System.err.println(e.getMessage());
                return newFixedLengthResponse("Internal server error while parsing request body: " + e.getMessage());
            } catch (ResponseException e) {
                System.err.println(e.getMessage());
                return newFixedLengthResponse("Internal server error: " + e.getMessage());
            }
            String test = body.get("content");
            TimeUnit t;
            try {
                t = parseTime(test);

            } catch (URISyntaxException e) {
                e.printStackTrace();
                JSONObject errResult = new JSONObject();
                errResult.put("err", e.getMessage());
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, "application/json", errResult.toString());
            }

            if (t == null) {
                JSONObject errResult = new JSONObject();
                errResult.put("err", "null");
                return newFixedLengthResponse(Response.Status.BAD_REQUEST, "application/json", errResult.toString());
            }

            JSONObject result = new JSONObject();
            result.put("time", t.getTime().getTime());
            result.put("all_day_time", t.getIsAllDayTime());

            return newFixedLengthResponse(Response.Status.OK, "application/json", result.toString());
        } else {
            // 默认的处理逻辑，返回 HTML 页面
            String msg = "<html><body><h1>Hello server</h1>\n";
            Map<String, String> parms = session.getParms();
            if (parms.get("username") == null) {
                msg += "<form action='?' method='post'>\n  <p>Your name: <input type='text' name='username'></p>\n"
                        + "<input type='submit' value='Submit'></form>\n";
            } else {
                msg += "You submitted: " + parms.get("username");
            }
            return newFixedLengthResponse(msg + "</body></html>\n");
        }
    }

    private TimeUnit parseTime(String content) throws URISyntaxException {
        URL url = TimeNormalizer.class.getResource("/TimeExp.m");
        // System.out.println(url.toURI().toString());
        TimeNormalizer normalizer = new TimeNormalizer(url.toURI().toString());
        normalizer.setPreferFuture(true);

        System.err.println(content);
        normalizer.parse(content);// 抽取时间
        TimeUnit[] unit = normalizer.getTimeUnit();
        System.out.println(content);

        if (unit.length == 0) {
            return null;
        }

        System.out.println(DateUtil.formatDateDefault(unit[0].getTime()) + "-" + unit[0].getIsAllDayTime());

        return unit[0];
    }

    private void getJsonData(IHTTPSession session) {
        Map<String, String> files = new HashMap<>();
        Method method = session.getMethod();
        if (Method.POST.equals(method)) {
            try {
                session.parseBody(files);
                String postBody = files.get("postData");
                JsonObject jsonObject = new Gson().fromJson(postBody, JsonObject.class);
                String content = jsonObject.get("content").getAsString();
                System.out.println(content);
                // 处理参数
            } catch (IOException | ResponseException e) {
                // 处理异常
            }
        }
    }
}
