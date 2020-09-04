package main.controller;

import main.api.response.CheckResponse;
import main.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/auth/")
public class ApiAuthController {
    @GetMapping("/api/auth/check")
    public ResponseEntity<CheckResponse> check(){
        CheckResponse response = new CheckResponse();
        response.setResult(false);
        return ResponseEntity.ok().body(response);
    }

}
