package com.example.edupal.service;

import com.alibaba.fastjson.JSONArray;
import okhttp3.*;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;

public class XunfeiAIService {

    private static final String hostUrl = "https://spark-api.xf-yun.com/v1/x1";
    private static final String appid = "您的AppID";
    private static final String apiSecret = "您的APISecret";
    private static final String apiKey = "您的APIKey";

    public String askQuestion(String question) throws Exception {
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        OkHttpClient client = new OkHttpClient.Builder().build();
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();

        try (WebSocket webSocket = client.newWebSocket(request, new XunfeiAIWebSocketListener(question))) {
            // 等待WebSocket连接关闭
            Thread.sleep(5000); // 根据需要调整等待时间
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return "回答来自讯飞大模型"; // 返回实际的答案
    }

    private static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());

        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        String sha = Base64.getEncoder().encodeToString(hexDigits);

        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder()
                .addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8)))
                .addQueryParameter("date", date)
                .addQueryParameter("host", url.getHost())
                .build();

        return httpUrl.toString();
    }

    private static class XunfeiAIWebSocketListener extends WebSocketListener {
        private final String question;
        private Boolean wsCloseFlag = false;

        public XunfeiAIWebSocketListener(String question) {
            this.question = question;
        }

        @Override
        public void onOpen(WebSocket webSocket, Response response) {
            JSONObject requestJson = new JSONObject();

            JSONObject header = new JSONObject();  // header参数
            header.put("app_id", appid);
            header.put("uid", UUID.randomUUID().toString().substring(0, 10));

            JSONObject parameter = new JSONObject(); // parameter参数
            JSONObject chat = new JSONObject();
            chat.put("domain", "x1");
            chat.put("temperature", 0.5);
            chat.put("max_tokens", 4096);
            parameter.put("chat", chat);

            JSONObject payload = new JSONObject(); // payload参数
            JSONObject message = new JSONObject();
            JSONArray text = new JSONArray();

            RoleContent roleContent = new RoleContent(); // 问题
            roleContent.role = "user";
            roleContent.content = question;
            text.add(JSON.toJSON(roleContent));

            message.put("text", text);
            payload.put("message", message);

            requestJson.put("header", header); // 组合
            requestJson.put("parameter", parameter);
            requestJson.put("payload", payload);
            webSocket.send(requestJson.toString());

            new Thread(() -> {
                try {
                    while (!wsCloseFlag) {
                        Thread.sleep(200);
                    }
                    webSocket.close(1000, "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }

        @Override
        public void onMessage(WebSocket webSocket, String text) {
            System.out.println(text);
            JsonParse myJsonParse = JSON.parseObject(text, JsonParse.class);
            if (myJsonParse.header.code != 0) {
                System.out.println("发生错误，错误码为：" + myJsonParse.header.code);
                System.out.println("本次请求的sid为：" + myJsonParse.header.sid);
                webSocket.close(1000, "");
            }
            List<Text> textList = myJsonParse.payload.choices.text;
            for (Text temp : textList) {
                System.out.print(temp.content);
            }
            if (myJsonParse.header.status == 2) {
                wsCloseFlag = true; // 打开释放信号
            }
        }

        @Override
        public void onFailure(WebSocket webSocket, Throwable t, Response response) {
            super.onFailure(webSocket, t, response);
            try {
                if (null != response) {
                    int code = response.code();
                    System.out.println("onFailure code:" + code);
                    assert response.body() != null;
                    System.out.println("onFailure body: " + response.body().string());
                    if (101 != code) {
                        System.out.println("connection failed");
                        System.exit(0);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        static class JsonParse {
            Header header;
            Payload payload;
        }

        static class Header {
            int code;
            int status;
            String sid;
        }

        static class Payload {
            Choices choices;
        }

        static class Choices {
            List<Text> text;
        }

        static class Text {
            String role;
            String content;
        }

        static class RoleContent {
            String role;
            String content;
        }
    }
}

