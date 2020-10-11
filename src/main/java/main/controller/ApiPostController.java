package main.controller;

import main.api.response.post.GetPostResponse;
import main.api.response.post.PostsResponse;
import main.model.Post;
import main.model.PostComment;
import main.model.TagToPost;
import main.repository.*;
import main.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController("/api/post")
public class ApiPostController {

    @Autowired
    private PostService postService;

    @GetMapping("/api/post")
    public ResponseEntity<PostsResponse> post(@RequestParam Map<String, String> allParams) {
        int offset = Integer.parseInt(allParams.get("offset"));
        int limit = Integer.parseInt(allParams.get("limit"));
        String mode = allParams.get("mode");
        Pageable pageable = null;
        switch (mode) {
            case "recent":
                pageable = PageRequest.of(offset, limit, new Sort(Sort.Direction.DESC, "time"));
                break;
            case "early":
                pageable = PageRequest.of(offset, limit, new Sort(Sort.Direction.ASC, "time"));
                break;
            case "popular":
            case "best": {
                return postService.selectWithMode(offset, limit, mode);
            }
        }
        return ResponseEntity.ok().body(postService.findAllWithPageable(pageable));
    }

    @GetMapping("api/post/{id}")
    public ResponseEntity<GetPostResponse> getPost(@PathVariable int id) {
        return postService.findById(id);
    }

    @GetMapping("api/post/byTag")
    public ResponseEntity<PostsResponse> getPostByTag(@RequestParam Map<String, String> allParams) {
        return postService.getPostByTag(allParams);
    }

    @GetMapping("/api/post/search")
    public ResponseEntity<PostsResponse> searchPosts(@RequestParam Map<String, String> allParams) {
        return postService.searchPosts(allParams);
    }

    @GetMapping("/api/post/byDate")
    public ResponseEntity<PostsResponse> getPostsByDate(@RequestParam Map<String, String> allParams){
        return postService.searchPostsByDate(allParams);
    }
}
