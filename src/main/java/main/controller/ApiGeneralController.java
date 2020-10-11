package main.controller;

import main.api.response.InitResponse;
import main.api.response.settings.GlobalSettingsResponse;
import main.api.response.tags.TagResponse;
import main.api.response.tags.Tags;
import main.model.GlobalSetting;
import main.model.Tag;
import main.repository.GlobalSettingsRepository;
import main.repository.PostRepository;
import main.repository.Tag2PostRepository;
import main.repository.TagRepository;
import main.service.TagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;

@RestController("/api")
public class ApiGeneralController {

    @Autowired
    private GlobalSettingsRepository globalSettingsRepository;

    @Autowired
    private TagService tagService;

    @GetMapping("/api/init")
    public ResponseEntity<InitResponse> init() {
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
    public ResponseEntity<TreeMap<String, Boolean>> settings() {
        Iterable<GlobalSetting> settings = globalSettingsRepository.findAll();
        GlobalSettingsResponse responseObject = new GlobalSettingsResponse(settings);
        TreeMap<String, Boolean> finalResponse = responseObject.getSettings();
        return ResponseEntity.ok().body(finalResponse);
    }

    @GetMapping("/api/tag")
    public ResponseEntity<Tags> tag(@RequestParam(required = false) String query) {
        return ResponseEntity.ok().body(tagService.getTags(query));
    }


}
