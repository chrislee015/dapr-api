package com.app.springsandboxone.services;

public enum JsonPlaceholderTask {
    GET_POSTS("/posts"),
    GET_POST_BY_ID("/posts/%d"),
    GET_COMMENTS("/comments"),
    GET_USERS("/users"),
    GET_USER_BY_ID("/users/%d"),
    GET_ALBUMS("/albums"),
    GET_TODOS("/todos"),
    CREATE_POST("/posts"),
    UPDATE_POST("/posts/%d"),
    DELETE_POST("/posts/%d");
    
    private final String endpoint;
    
    JsonPlaceholderTask(String endpoint) {
        this.endpoint = endpoint;
    }
    
    public String getEndpoint() {
        return endpoint;
    }
    
    public String getFormattedEndpoint(Object... args) {
        return String.format(endpoint, args);
    }
}
