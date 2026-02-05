package com.app.sandboxtwo.models;

import lombok.Data;

import java.time.Instant;

@Data
public class CreateClientResponse {
    private String clientId;
    private String name;
    private String url;
    private boolean success;
    private String message;
    private Instant timestamp;
    
    public static CreateClientResponse success(String clientId, String name, String url) {
        CreateClientResponse response = new CreateClientResponse();
        response.setClientId(clientId);
        response.setName(name);
        response.setUrl(url);
        response.setSuccess(true);
        response.setMessage("Client created successfully");
        response.setTimestamp(Instant.now());
        return response;
    }
    
    public static CreateClientResponse error(String clientId, String message) {
        CreateClientResponse response = new CreateClientResponse();
        response.setClientId(clientId);
        response.setSuccess(false);
        response.setMessage(message);
        response.setTimestamp(Instant.now());
        return response;
    }
}
