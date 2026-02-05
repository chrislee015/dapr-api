package com.app.sandboxtwo.models;

import com.app.sandboxtwo.services.GenericFeignClient;
import lombok.Data;

@Data
public class RuntimeFeignClient {
    private String id;
    private String name;
    private String url;
    private String apiKey;
    private GenericFeignClient client;
    
    public RuntimeFeignClient(String id, String name, String url, String apiKey, GenericFeignClient client) {
        this.id = id;
        this.name = name;
        this.url = url;
        this.apiKey = apiKey;
        this.client = client;
    }
}
