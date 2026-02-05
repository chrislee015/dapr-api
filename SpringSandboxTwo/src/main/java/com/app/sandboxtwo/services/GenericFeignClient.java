package com.app.sandboxtwo.services;

import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;
import java.util.Map;

public interface GenericFeignClient {
    
    @RequestLine("GET /posts")
    @Headers("X-API-Key: {apiKey}")
    List<Map<String, Object>> getPosts(@Param("apiKey") String apiKey);
    
    @RequestLine("GET /posts/{id}")
    @Headers("X-API-Key: {apiKey}")
    Map<String, Object> getPost(@Param("id") String id, @Param("apiKey") String apiKey);
    
    @RequestLine("GET /users")
    @Headers("X-API-Key: {apiKey}")
    List<Map<String, Object>> getUsers(@Param("apiKey") String apiKey);
    
    @RequestLine("GET /users/{id}")
    @Headers("X-API-Key: {apiKey}")
    Map<String, Object> getUser(@Param("id") String id, @Param("apiKey") String apiKey);
    
    @RequestLine("GET /albums")
    @Headers("X-API-Key: {apiKey}")
    List<Map<String, Object>> getAlbums(@Param("apiKey") String apiKey);
}
