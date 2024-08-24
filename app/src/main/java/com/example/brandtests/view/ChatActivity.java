package com.example.brandtests.view;

import android.os.Bundle;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.brandtests.R;
import com.example.brandtests.adapter.ChatMessageAdapter;
import com.example.brandtests.viewmodel.ChatViewModel;
import com.example.brandtests.viewmodel.ChatViewModelFactory;

public class ChatActivity extends AppCompatActivity {
    private ChatViewModel chatViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        RecyclerView recyclerView = findViewById(R.id.recyclerViewChat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ChatMessageAdapter adapter = new ChatMessageAdapter();
        recyclerView.setAdapter(adapter);

        ChatViewModelFactory factory = new ChatViewModelFactory(getApplication());
        chatViewModel = new ViewModelProvider(this, factory).get(ChatViewModel.class);

        chatViewModel.getMessages().observe(this, adapter::setChatMessages);

        findViewById(R.id.buttonSend).setOnClickListener(v -> {
            String messageText = ((EditText) findViewById(R.id.editTextMessage)).getText().toString();
            chatViewModel.sendMessage(messageText);
        });
    }
}
