package com.example.brandtests.service;

import com.example.brandtests.WebSocketClient;
import com.example.brandtests.model.ChatMessage;
import com.google.gson.Gson;

public class WebSocketService {
    private WebSocketClient webSocketClient;
    private Gson gson = new Gson();

    public WebSocketService(WebSocketClient.MessageCallback callback) {
        this.webSocketClient = new WebSocketClient(callback);
    }

    public void sendMessage(ChatMessage message) {
        String jsonMessage = gson.toJson(message);
        webSocketClient.sendMessage(jsonMessage);
    }

    public void connectWebSocket() {
        webSocketClient.connect();
    }
}
