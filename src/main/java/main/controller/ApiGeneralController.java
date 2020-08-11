package main.controller;

import main.api.response.CheckResponse;
import main.api.response.InitResponse;
import main.repository.GlobalSettingsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api")
public class ApiGeneralController {

    @GetMapping("/api/init")
    public ResponseEntity<InitResponse> init(){
        InitResponse response = new InitResponse();
        response.setTitle("DevPub");
        response.setSubtitle("Рассказы разработчиков");
        response.setPhone("+7 903 666-44-55");
        response.setEmail("mail@mail.ru");
        response.setCopyright("Дмитрий Сергеев");
        response.setCopyrightFrom("2005");
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/api/settings")
    public ResponseEntity<String> settings(){
        return ResponseEntity.ok().body("{MULTIUSER_MODE: true, " +
                "POST_PREMODERATION: true, STATISTICS_IS_PUBLIC: true}");
    }

    @GetMapping("/api/tag")
    public ResponseEntity<String> tag(){
        return ResponseEntity.ok().body("{tags: []}");
    }

}
