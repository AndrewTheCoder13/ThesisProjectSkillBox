package main.service;

import lombok.AllArgsConstructor;
import main.api.responseAndAnswers.tags.Tags;
import main.model.Tag;
import main.repository.PostRepository;
import main.repository.Tag2PostRepository;
import main.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;

@Service
@AllArgsConstructor
public class TagService {
    
    private final TagRepository tagRepository;
    private final Tag2PostRepository tag2PostRepository;
    private final PostRepository postRepository;

    public Tags getTags(String query){
        HashMap<Tag, Integer> tag2Count = new HashMap<>();
        Iterable<Tag> allTags =  query == null? tagRepository.findAll() : tagRepository.searchTagByQuery(query);
        allTags.forEach(tag -> tag2Count.put(tag, tag2PostRepository.countOfPostsByTagId(tag.getId(), LocalDateTime.now())));
        long countOfPosts = postRepository.count();
        return new Tags(countOfPosts, tag2Count);
    }
}
