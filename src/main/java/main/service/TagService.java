package main.service;

import main.api.response.tags.Tags;
import main.model.Tag;
import main.repository.PostRepository;
import main.repository.Tag2PostRepository;
import main.repository.TagRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class TagService {
    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private Tag2PostRepository tag2PostRepository;

    @Autowired
    private PostRepository postRepository;

    public Tags getTags(String query){
        HashMap<Tag, Integer> tag2Count = new HashMap<>();
        Iterable<Tag> allTags =  query == null? tagRepository.findAll() : tagRepository.searchTagByQuery(query);
        allTags.forEach(tag -> tag2Count.put(tag, tag2PostRepository.countOfPostsByTagId(tag.getId())));
        long countOfPosts = postRepository.count();
        return new Tags(countOfPosts, tag2Count);
    }
}
