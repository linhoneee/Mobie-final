package com.example.brandtests.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.brandtests.R;
import com.example.brandtests.model.ChatMessage;

import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_LEFT = 0;
    private static final int VIEW_TYPE_RIGHT = 1;

    private List<ChatMessage> chatMessages;
    private long loggedInUserId;

    public ChatMessageAdapter(long loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
    }

    public void setChatMessages(List<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
        notifyDataSetChanged(); // Cập nhật dữ liệu khi nhận tin nhắn mới
    }

    @Override
    public int getItemViewType(int position) {
        ChatMessage chatMessage = chatMessages.get(position);

        // Kiểm tra null trước khi so sánh
        if (chatMessage.getUserId() != null && chatMessage.getUserId() == loggedInUserId) {
            return VIEW_TYPE_RIGHT; // Tin nhắn của người dùng đăng nhập
        } else {
            return VIEW_TYPE_LEFT; // Tin nhắn của người khác hoặc hệ thống
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_RIGHT) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_right, parent, false);
            return new RightChatMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_chat_message_left, parent, false);
            return new LeftChatMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage chatMessage = chatMessages.get(position);
        if (holder instanceof RightChatMessageViewHolder) {
            ((RightChatMessageViewHolder) holder).bind(chatMessage);
        } else if (holder instanceof LeftChatMessageViewHolder) {
            ((LeftChatMessageViewHolder) holder).bind(chatMessage);
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages == null ? 0 : chatMessages.size();
    }

    static class LeftChatMessageViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMessage;
        TextView textViewUsername;

        public LeftChatMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
        }

        public void bind(ChatMessage chatMessage) {
            textViewMessage.setText(chatMessage.getText());
            textViewUsername.setText(chatMessage.getUsername());
        }
    }

    static class RightChatMessageViewHolder extends RecyclerView.ViewHolder {

        TextView textViewMessage;
        TextView textViewUsername;

        public RightChatMessageViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewMessage = itemView.findViewById(R.id.textViewMessage);
            textViewUsername = itemView.findViewById(R.id.textViewUsername);
        }

        public void bind(ChatMessage chatMessage) {
            textViewMessage.setText(chatMessage.getText());
            textViewUsername.setText(chatMessage.getUsername());
        }
    }
}
