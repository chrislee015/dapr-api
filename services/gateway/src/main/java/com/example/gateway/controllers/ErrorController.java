package com.example.gateway.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/error")
public class ErrorController {

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> error() {
        log.info("Serving error page");
        
        String errorPage = """
            <!DOCTYPE html>
            <html>
            <head>
                <title>Gateway Error</title>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body {
                        font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                        margin: 0;
                        padding: 50px;
                        background-color: #f5f5f5;
                    }
                    .error-container {
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 30px;
                        background-color: white;
                        border-radius: 10px;
                        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
                    }
                    .error-title {
                        color: #d9534f;
                        margin-bottom: 20px;
                        font-size: 2em;
                    }
                    .error-message {
                        margin-bottom: 20px;
                        line-height: 1.6;
                    }
                    .home-link {
                        display: inline-block;
                        padding: 10px 20px;
                        background-color: #007bff;
                        color: white;
                        text-decoration: none;
                        border-radius: 5px;
                        transition: background-color 0.3s;
                    }
                    .home-link:hover {
                        background-color: #0056b3;
                    }
                </style>
            </head>
            <body>
                <div class="error-container">
                    <h1 class="error-title">🚧 Gateway Error</h1>
                    <div class="error-message">
                        <p>We're sorry, but something went wrong while processing your request.</p>
                        <p>This could be due to:</p>
                        <ul>
                            <li>Authentication issues</li>
                            <li>Service unavailability</li>
                            <li>Network connectivity problems</li>
                            <li>Invalid request parameters</li>
                        </ul>
                    </div>
                    <a href="https://localhost:8080/" class="home-link">🏠 Return to Home</a>
                </div>
            </body>
            </html>
            """;
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .contentType(MediaType.TEXT_HTML)
            .body(errorPage);
    }
}
