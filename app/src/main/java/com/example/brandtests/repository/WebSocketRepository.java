//package com.example.brandtests.repository;
//
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//
//import org.jetbrains.annotations.NotNull;
//import org.java_websocket.client.WebSocketClient;
//import org.java_websocket.handshake.ServerHandshake;
//
//import java.net.URI;
//
//public class WebSocketRepository {
//
//    private WebSocketClient webSocketClient;
//    private MutableLiveData<String> incomingMessage = new MutableLiveData<>();
//
//    public LiveData<String> getIncomingMessage() {
//        return incomingMessage;
//    }
//
//    public void connectWebSocket(URI uri) {
//        webSocketClient = new WebSocketClient(uri) {
//            @Override
//            public void onOpen(@NotNull ServerHandshake handshake) {
//                // Kết nối thành công
//            }
//
//            @Override
//            public void onMessage(@NotNull String message) {
//                // Nhận tin nhắn và cập nhật LiveData
//                incomingMessage.postValue(message);
//            }
//
//            @Override
//            public void onClose(int code, String reason, boolean remote) {
//                // Xử lý khi WebSocket bị đóng
//            }
//
//            @Override
//            public void onError(@NotNull Exception ex) {
//                // Xử lý lỗi
//            }
//        };
//        webSocketClient.connect();
//    }
//
//    public void sendMessage(String message) {
//        if (webSocketClient != null && webSocketClient.isOpen()) {
//            webSocketClient.send(message);
//        }
//    }
//
//    public void closeWebSocket() {
//        if (webSocketClient != null) {
//            webSocketClient.close();
//        }
//    }
//}
