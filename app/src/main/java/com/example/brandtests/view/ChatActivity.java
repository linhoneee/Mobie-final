package com.example.brandtests.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brandtests.R;
import com.example.brandtests.adapter.ChatMessageAdapter;
import com.example.brandtests.model.ChatMessage;
import com.example.brandtests.viewmodel.ChatViewModel;
import com.example.brandtests.viewmodel.ChatViewModelFactory;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    private static boolean isActive = false;
    private ChatViewModel chatViewModel;
    private ChatMessageAdapter adapter;
    private RecyclerView recyclerView; // Biến toàn cục để quản lý RecyclerView
    private LinearLayoutManager layoutManager;

    private static List<ChatMessage> newMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        long loggedInUserId = sharedPreferences.getLong("UserID", -1); // Lấy userID từ SharedPreferences

        recyclerView = findViewById(R.id.recyclerViewChat);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true); // Cuộn danh sách từ dưới lên
        recyclerView.setLayoutManager(layoutManager);

        adapter = new ChatMessageAdapter(loggedInUserId); // Truyền userID vào adapter
        recyclerView.setAdapter(adapter);

        ChatViewModelFactory factory = new ChatViewModelFactory(getApplication());
        chatViewModel = new ViewModelProvider(this, factory).get(ChatViewModel.class);

        // Quan sát LiveData và cập nhật giao diện khi dữ liệu thay đổi
        chatViewModel.getMessages().observe(this, chatMessages -> {
            adapter.setChatMessages(chatMessages);

            // Cuộn xuống cuối cùng khi dữ liệu thay đổi
            if (chatMessages != null && !chatMessages.isEmpty()) {
                recyclerView.scrollToPosition(chatMessages.size() - 1);
            }
        });

        findViewById(R.id.buttonSend).setOnClickListener(v -> {
            String messageText = ((EditText) findViewById(R.id.editTextMessage)).getText().toString();
            chatViewModel.sendMessage(messageText);

            // Cuộn xuống cuối sau khi gửi tin nhắn
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;

        // Khi ChatActivity hoạt động, cập nhật tin nhắn mới và cuộn xuống cuối
        if (!newMessages.isEmpty()) {
            adapter.setChatMessages(newMessages);
            newMessages.clear();
            recyclerView.scrollToPosition(adapter.getItemCount() - 1); // Cuộn xuống cuối cùng
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isActive = false;
    }

    public static boolean isActive() {
        return isActive;
    }

    public static void updateMessages(String message) {
        ChatMessage newMessage = new ChatMessage();
        newMessage.setText(message);

        // Nếu ChatActivity đang hoạt động, cập nhật giao diện và cuộn xuống cuối
        if (isActive) {
            newMessages.add(newMessage);
        } else {
            // Lưu tin nhắn mới vào danh sách để cập nhật khi ChatActivity mở lại
            newMessages.add(newMessage);
        }
    }
}
