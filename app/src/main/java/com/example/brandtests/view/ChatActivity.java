package com.example.brandtests.view;

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

    private static List<ChatMessage> newMessages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        adapter = new ChatMessageAdapter();
        recyclerView.setAdapter(adapter);

        ChatViewModelFactory factory = new ChatViewModelFactory(getApplication());
        chatViewModel = new ViewModelProvider(this, factory).get(ChatViewModel.class);

        chatViewModel.getMessages().observe(this, adapter::setChatMessages);

        findViewById(R.id.buttonSend).setOnClickListener(v -> {
            String messageText = ((EditText) findViewById(R.id.editTextMessage)).getText().toString();
            chatViewModel.sendMessage(messageText);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        isActive = true;

        // Khi ChatActivity hoạt động, cập nhật tin nhắn mới
        if (!newMessages.isEmpty()) {
            for (ChatMessage message : newMessages) {
                adapter.setChatMessages(newMessages);
            }
            newMessages.clear();
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

        // Nếu ChatActivity đang hoạt động, cập nhật giao diện
        if (isActive) {
            newMessages.add(newMessage);
        } else {
            // Lưu tin nhắn mới vào danh sách để cập nhật khi ChatActivity mở lại
            newMessages.add(newMessage);
        }
    }
}
