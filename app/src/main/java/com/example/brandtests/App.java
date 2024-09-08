package com.example.brandtests;

import android.app.Application;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.example.brandtests.service.WebSocketService;
import com.example.brandtests.view.ChatActivity;

public class App extends Application {
    private static final String TAG = "App";
    private WebSocketService webSocketService;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        long userId = sharedPreferences.getLong("UserID", -1);

        // Khởi tạo WebSocket và kết nối
        webSocketService = new WebSocketService(this::onNewMessageReceived);
        webSocketService.connectWebSocket(userId);
    }

    private void onNewMessageReceived(String message) {
        Log.d(TAG, "Received message: " + message);

        // Kiểm tra Activity hiện tại, nếu là ChatActivity thì cập nhật giao diện, nếu không thì hiện Toast
        if (ChatActivity.isActive()) {
            // Cập nhật RecyclerView của ChatActivity
            ChatActivity.updateMessages(message);
        } else {
            // Sử dụng Handler để đảm bảo Toast chạy trên UI thread
            new Handler(Looper.getMainLooper()).post(() -> {
                Toast.makeText(getApplicationContext(), "New message: " + message, Toast.LENGTH_SHORT).show();
            });
        }
    }

    public WebSocketService getWebSocketService() {
        return webSocketService;
    }
}
