package com.example.brandtests.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.brandtests.R;
import com.example.brandtests.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends ArrayAdapter<ChatMessage> {

    public ChatAdapter(@NonNull Context context, @NonNull List<ChatMessage> messages) {
        super(context, 0, messages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_item, parent, false);
        }

        ChatMessage message = getItem(position);

        if (message != null) {
            TextView usernameView = convertView.findViewById(R.id.chatUsername);
            TextView messageView = convertView.findViewById(R.id.chatMessage);

            usernameView.setText(message.getUsername());
            messageView.setText(message.getText());
        }

        return convertView;
    }
}
