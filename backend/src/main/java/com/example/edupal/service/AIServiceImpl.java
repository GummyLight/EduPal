package com.example.edupal.service;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.example.edupal.dto.request.QuestionRequest;
import com.example.edupal.dto.response.AnswerResponse;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;



@Service
public class AIServiceImpl implements AIService {

    private static final String API_URL = "https://spark-api-open.xf-yun.com/v2/chat/completions";
    private static final String API_PASSWORD = "GDmXDuVwnpEiserckONd:xSCyunWDskiLSTIDVRIb"; // Replace with your actual API password

    @Override
    public AnswerResponse askQuestion(QuestionRequest questionRequest) {
        AnswerResponse answerResponse = new AnswerResponse();
        StringBuilder contentBuilder = new StringBuilder();

        try {
            // Create the JSON request payload
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("user", questionRequest.getStudentId());
            jsonObject.set("model", "x1");

            JSONArray messagesArray = new JSONArray();
            JSONObject messageObject = new JSONObject();
            messageObject.set("role", "user");
            messageObject.set("content", questionRequest.getQuestionContent());
            messageObject.set("temperature", "0.5");
            messagesArray.put(messageObject);

            jsonObject.set("messages", messagesArray);
            jsonObject.set("stream", true);
            jsonObject.set("max_tokens", 512);

            // Set up the HTTP connection
            URL url = new URL(API_URL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + API_PASSWORD);
            con.setDoOutput(true);

            // Send the request
            OutputStream os = con.getOutputStream();
            os.write(jsonObject.toString().getBytes());
            os.flush();
            os.close();

            // Read the response
            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    // Parse each line as JSON and extract "content"
                    if (inputLine.startsWith("data:")) {
                        String jsonData = inputLine.substring(5).trim(); // Remove "data:" prefix
                        if (!jsonData.equals("[DONE]")) {
                            JSONObject responseData = new JSONObject(jsonData);
                            if (responseData.containsKey("choices")) {
                                JSONArray choicesArray = responseData.getJSONArray("choices");
                                for (int i = 0; i < choicesArray.size(); i++) {
                                    JSONObject choice = choicesArray.getJSONObject(i);
                                    if (choice.containsKey("delta")) {
                                        JSONObject delta = choice.getJSONObject("delta");
                                        if (delta.containsKey("content")) {
                                            contentBuilder.append(delta.getStr("content"));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                in.close();

                // Set the final content in AnswerResponse
                answerResponse.setAnswerContent(contentBuilder.toString());
            } else {
                answerResponse.setAnswerContent("Error: Received response code " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
            answerResponse.setAnswerContent("Error: " + e.getMessage());
        }
        return answerResponse;
    }
}
