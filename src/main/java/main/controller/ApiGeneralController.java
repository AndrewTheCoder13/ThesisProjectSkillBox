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
    private TagRepository tagRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private Tag2PostRepository tag2PostRepository;

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
        if (query == null){
            HashMap<Tag, Integer> tag2Count = new HashMap<>();
            Iterable<Tag> allTags = tagRepository.findAll();
            allTags.forEach(tag -> tag2Count.put(tag, tag2PostRepository.countOfPostsByTagId(tag.getId())));
            long countOfPosts = postRepository.count();
            Tags tags = new Tags(countOfPosts, tag2Count);
            return ResponseEntity.ok().body(tags);
        } else {
            HashMap<Tag, Integer> tag2Count = new HashMap<>();
            List<Tag> allTags = tagRepository.searchTagByQuery(query);
            allTags.forEach(tag -> tag2Count.put(tag, tag2PostRepository.countOfPostsByTagId(tag.getId())));
            long countOfPosts = postRepository.count();
            Tags tags = new Tags(countOfPosts, tag2Count);
            return ResponseEntity.ok().body(tags);
        }
    }

}
