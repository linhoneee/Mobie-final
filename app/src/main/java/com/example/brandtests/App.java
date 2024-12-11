package com.example.brandtests;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.brandtests.model.ChatMessage;
import com.example.brandtests.service.WebSocketService;
import com.example.brandtests.view.ChatActivity;
import com.google.gson.Gson;

public class App extends Application {
    private static final String TAG = "App";
    private WebSocketService webSocketService;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        long userId = sharedPreferences.getLong("UserID", -1);

        webSocketService = new WebSocketService(this::onNewMessageReceived);
        webSocketService.connectWebSocket(userId);
    }

    private void onNewMessageReceived(String message) {
        Log.d(TAG, "Received message: " + message);

        Gson gson = new Gson();
        ChatMessage messageObj = gson.fromJson(message, ChatMessage.class);

        // Kiểm tra Activity hiện tại, nếu là ChatActivity thì cập nhật giao diện, nếu không thì hiện Toast
        if (ChatActivity.isActive()) {
            ChatActivity.updateMessages(message);
        } else {
            new Handler(Looper.getMainLooper()).post(() -> {
                String text = messageObj.getText();  // Lấy text từ Message object
                Toast.makeText(getApplicationContext(), "Tin nhắn mới: " + text, Toast.LENGTH_SHORT).show();

                NotificationHelper.showNotification(getApplicationContext(), "Tin nhắn mới", "Bạn đã nhận được tin nhắn mới!");
            });
        }
    }

}
