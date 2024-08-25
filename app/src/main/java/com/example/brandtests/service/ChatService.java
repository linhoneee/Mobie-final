package com.example.brandtests.service;

import com.example.brandtests.model.ChatMessage;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ChatService {

    @POST("/api/messages/send")
    Call<ChatMessage> sendMessage(@Body ChatMessage message);
}
