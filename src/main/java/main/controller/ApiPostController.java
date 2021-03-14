package main.controller;

import main.api.responseAndAnswers.post.*;
import main.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;

@RestController("/api/post")
@RequestMapping("/api/post")
public class ApiPostController {

    @Autowired
    private PostService postService;

    @GetMapping("")
    public ResponseEntity<PostsResponse> post( @RequestParam(required = false, defaultValue = "0") int offset,
                                               @RequestParam(required = false, defaultValue = "10") int limit,
                                               @RequestParam(required = false, defaultValue = "recent") String mode) {
        Pageable pageable = null;
        switch (mode) {
            case "recent":
                pageable = PageRequest.of(offset, limit, Sort.by(Sort.Direction.DESC, "time"));
                break;
            case "early":
                pageable = PageRequest.of(offset, limit, Sort.by(Sort.Direction.ASC, "time"));
                break;
            case "popular":
            case "best": {
                return postService.selectWithMode(offset, limit, mode);
            }
        }
        return ResponseEntity.ok().body(postService.findAllWithPageable(pageable));
    }

    @GetMapping("search")
    public ResponseEntity<PostsResponse> searchPosts(@RequestParam Map<String, String> allParams) {
        return postService.searchPosts(allParams);
    }

    @GetMapping("byDate")
    public ResponseEntity<PostsResponse> getPostsByDate(@RequestParam Map<String, String> allParams) {
        return postService.searchPostsByDate(allParams);
    }

    @GetMapping("byTag")
    public ResponseEntity<PostsResponse> getPostByTag(@RequestParam Map<String, String> allParams) {
        return postService.getPostByTag(allParams);
    }

    @GetMapping("moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostsResponse> getPostForModeration(@RequestParam Map<String, String> allParams) {
        return postService.getPostForModeration(allParams);
    }

    @GetMapping("my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostsResponse> geyMyPosts(@RequestParam Map<String, String> allParams, Principal principal) {
        return postService.getMyPosts(allParams, principal);
    }

    @GetMapping("{id}")
    public ResponseEntity<GetPostResponse> getPost(@PathVariable int id, Principal principal) {
        return postService.findById(id, principal);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("")
    public ResponseEntity<PutPostResponse> putPost(Principal principal, @RequestBody PostAdd postForAdd){
        return postService.putPost(principal, postForAdd);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PutMapping("{id}")
    public ResponseEntity<PutPostResponse> editPost(Principal principal, @RequestBody PostAdd postForAdd, @PathVariable int id){
        return postService.editPost(principal, postForAdd, id);
    }
    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("like")
    public ResponseEntity<PutPostResponse> like(Principal principal, @RequestBody PostVoteRequest post_id){
        return postService.putVote(principal, post_id.getPostId(), 1);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("dislike")
    public ResponseEntity<PutPostResponse> dislike(Principal principal, @RequestBody PostVoteRequest post_id){
        return postService.putVote(principal, post_id.getPostId(), -1);
    }

}
