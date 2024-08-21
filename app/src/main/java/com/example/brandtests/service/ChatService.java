package com.example.brandtests.service;

import com.example.brandtests.model.ChatMessage;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ChatService {

    @GET("/api/messages/room/{roomId}")
    Call<List<ChatMessage>> getMessagesByRoomId(@Path("roomId") Long roomId);
}
