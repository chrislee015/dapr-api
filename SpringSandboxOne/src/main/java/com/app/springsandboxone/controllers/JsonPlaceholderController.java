package com.app.springsandboxone.controllers;

import com.app.springsandboxone.models.*;
import com.app.springsandboxone.services.FanOutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fanout")
public class JsonPlaceholderController {
    
    private final FanOutService fanOutService;
    
    public JsonPlaceholderController(FanOutService fanOutService) {
        this.fanOutService = fanOutService;
    }
    
    @GetMapping("/health")
    public ResponseEntity<List<TaskResult<String>>> healthCheck() {
        return ResponseEntity.ok(fanOutService.healthCheck());
    }
    
//    @GetMapping("/posts")
//    public ResponseEntity<List<TaskResult<List<Post>>>> getAllPosts() {
//        return ResponseEntity.ok(fanOutService.getAllPosts());
//    }
//
//    @GetMapping("/posts/{id}")
//    public ResponseEntity<List<TaskResult<Post>>> getPostById(@PathVariable int id) {
//        return ResponseEntity.ok(fanOutService.getPostById(id));
//    }
//
//    @PostMapping("/posts")
//    public ResponseEntity<List<TaskResult<Post>>> createPost(@RequestBody Post post) {
//        return ResponseEntity.ok(fanOutService.createPost(post));
//    }
//
//    @PutMapping("/posts/{id}")
//    public ResponseEntity<List<TaskResult<Post>>> updatePost(@PathVariable int id, @RequestBody Post post) {
//        return ResponseEntity.ok(fanOutService.updatePost(id, post));
//    }
//
//    @DeleteMapping("/posts/{id}")
//    public ResponseEntity<List<TaskResult<String>>> deletePost(@PathVariable int id) {
//        return ResponseEntity.ok(fanOutService.deletePost(id));
//    }
//
//    @GetMapping("/users")
//    public ResponseEntity<List<TaskResult<List<User>>>> getAllUsers() {
//        return ResponseEntity.ok(fanOutService.getAllUsers());
//    }
//
//    @GetMapping("/comments")
//    public ResponseEntity<List<TaskResult<List<Comment>>>> getAllComments() {
//        return ResponseEntity.ok(fanOutService.getAllComments());
//    }
}
