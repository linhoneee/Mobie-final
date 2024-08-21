package com.example.brandtests.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.brandtests.repository.WebSocketRepository;

import java.net.URI;

public class ChatViewModel extends ViewModel {

    private final WebSocketRepository webSocketRepository;

    public ChatViewModel() {
        webSocketRepository = new WebSocketRepository();
    }

    public LiveData<String> getIncomingMessage() {
        return webSocketRepository.getIncomingMessage();
    }

    public void connectWebSocket(URI uri) {
        webSocketRepository.connectWebSocket(uri);
    }

    public void sendMessage(String message) {
        webSocketRepository.sendMessage(message);
    }

    public void closeWebSocket() {
        webSocketRepository.closeWebSocket();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        closeWebSocket();
    }
}
