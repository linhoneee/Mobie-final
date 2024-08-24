//package com.example.brandtests.service;
//
//import com.example.brandtests.model.ChatMessage;
//import retrofit2.Call;
//import retrofit2.Callback;
//import retrofit2.Response;
//import retrofit2.http.Body;
//import retrofit2.http.POST;
//
//public class ChatService {
//    private ChatApi chatApi;
//
//    public ChatService() {
//        chatApi = ChatRetrofitClient.getClient().create(ChatApi.class);
//    }
//
//    public void sendMessage(ChatMessage message, long roomId) {
//        chatApi.sendMessage(message).enqueue(new Callback<ChatMessage>() {
//            @Override
//            public void onResponse(Call<ChatMessage> call, Response<ChatMessage> response) {
//                if (response.isSuccessful()) {
//                    ChatMessage savedMessage = response.body();
//                    // Xử lý tin nhắn đã lưu và gửi lại về WebSocket (nếu cần)
//                    // Ví dụ: gửi tin nhắn qua WebSocketService để cập nhật giao diện
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ChatMessage> call, Throwable t) {
//                // Xử lý khi có lỗi xảy ra
//                t.printStackTrace();
//            }
//        });
//    }
//
//    private interface ChatApi {
//        @POST("api/messages")
//        Call<ChatMessage> sendMessage(@Body ChatMessage message);
//    }
//}
