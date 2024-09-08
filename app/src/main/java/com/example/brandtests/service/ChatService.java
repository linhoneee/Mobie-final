package com.example.brandtests.service;

import com.example.brandtests.model.ChatMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface ChatService {

    @POST("/api/messages/send")
    Call<ChatMessage> sendMessage(@Body ChatMessage message);

    @GET("/api/messages/room/{roomid}")
    Call<List<ChatMessage>> getMessagesByRoomId(@Path("roomid") Long roomId);
}
