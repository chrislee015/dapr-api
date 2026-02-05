package com.app.sandboxtwo.models;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateClientRequest {
    @NotBlank(message = "ID is required")
    private String id;
    
    @NotBlank(message = "Name is required")
    private String name;
    
    @NotBlank(message = "URL is required")
    private String url;
    
    @NotBlank(message = "API Key is required")
    private String apiKey;
}
