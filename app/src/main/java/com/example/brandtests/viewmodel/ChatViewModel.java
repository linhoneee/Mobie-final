package com.example.brandtests.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.brandtests.model.ChatMessage;
import com.example.brandtests.service.ChatRetrofitClient;
import com.example.brandtests.service.ChatService;
import com.example.brandtests.service.WebSocketService;
import com.google.gson.Gson;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;
public class ChatViewModel extends AndroidViewModel {
    private static final String TAG = "ChatViewModel";
    private WebSocketService webSocketService;
    private MutableLiveData<List<ChatMessage>> messages;
    private SharedPreferences sharedPreferences;
    private ChatService chatService;

    public ChatViewModel(Application application) {
        super(application);
        messages = new MutableLiveData<>(new ArrayList<>());
        sharedPreferences = application.getSharedPreferences("LoginPrefs", Application.MODE_PRIVATE);

        long userId = sharedPreferences.getLong("UserID", -1);
        webSocketService = new WebSocketService(this::onNewMessageReceived);

        webSocketService.connectWebSocket(userId);

        chatService = ChatRetrofitClient.getClient().create(ChatService.class);

        loadMessagesByRoomId(userId);
    }

    private void loadMessagesByRoomId(long roomId) {
        chatService.getMessagesByRoomId(roomId).enqueue(new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Messages loaded successfully");
                    messages.postValue(response.body());
                } else {
                    try {
                        if (response.errorBody() != null) {
                            String errorBody = response.errorBody().string();
                            Log.e(TAG, "Failed to load messages: " + errorBody);
                        } else {
                            Log.e(TAG, "Failed to load messages: Unknown error, errorBody is null");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error reading error body", e);
                    }
                }
            }


            @Override
            public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                Log.e(TAG, "Error loading messages", t);
            }
        });
    }

    private void onNewMessageReceived(String jsonMessage) {
        try {
                ChatMessage message = new Gson().fromJson(jsonMessage, ChatMessage.class);
                Log.d(TAG, "Received JSON message: " + message.getText());
                List<ChatMessage> currentMessages = messages.getValue();
                currentMessages.add(message);
                messages.postValue(currentMessages);
        } catch (Exception e) {
            Log.e(TAG, "Failed to parse message", e);
        }
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public void sendMessage(String messageText) {
        long userId = sharedPreferences.getLong("UserID", -1);
        String username = sharedPreferences.getString("email", "Unknown");
        ChatMessage message = new ChatMessage(messageText, userId, userId, username);

        // Gửi tin nhắn qua WebSocket
        webSocketService.sendMessage(message);

        // Gửi API để lưu tin nhắn
        chatService.sendMessage(message).enqueue(new Callback<ChatMessage>() {
            @Override
            public void onResponse(Call<ChatMessage> call, Response<ChatMessage> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "Message saved successfully: " + response.body());
                } else {
                    Log.e(TAG, "Failed to save message: " + response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<ChatMessage> call, Throwable t) {
                Log.e(TAG, "Error saving message", t);
            }
        });
    }
}
