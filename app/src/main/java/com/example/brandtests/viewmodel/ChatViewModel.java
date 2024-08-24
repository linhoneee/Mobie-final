package com.example.brandtests.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.brandtests.model.ChatMessage;
import com.example.brandtests.service.WebSocketService;
import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChatViewModel extends AndroidViewModel {
    private static final String TAG = "ChatViewModel";
    private WebSocketService webSocketService;
    private MutableLiveData<List<ChatMessage>> messages;
    private SharedPreferences sharedPreferences;

    public ChatViewModel(Application application) {
        super(application);
        messages = new MutableLiveData<>(new ArrayList<>());
        sharedPreferences = application.getSharedPreferences("LoginPrefs", Application.MODE_PRIVATE);
        webSocketService = new WebSocketService(this::onNewMessageReceived);
        webSocketService.connectWebSocket();
    }

    private void onNewMessageReceived(String jsonMessage) {
        ChatMessage message = new Gson().fromJson(jsonMessage, ChatMessage.class);
        List<ChatMessage> currentMessages = messages.getValue();
        currentMessages.add(message);
        messages.postValue(currentMessages);
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public void sendMessage(String messageText) {
        long userId = sharedPreferences.getLong("UserID", -1);
        String username = sharedPreferences.getString("email", "Unknown");
        ChatMessage message = new ChatMessage(messageText, userId, userId, username);
        message.setCreatedAt(LocalDateTime.now());
        webSocketService.sendMessage(message);
    }
}
