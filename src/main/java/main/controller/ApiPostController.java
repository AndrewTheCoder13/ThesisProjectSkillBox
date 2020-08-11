package main.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/post")
public class ApiPostController {

    @GetMapping("/api/post")
    public ResponseEntity<String> post(){
        return ResponseEntity.ok().body("{count: 0, posts: []}");
    }
}
