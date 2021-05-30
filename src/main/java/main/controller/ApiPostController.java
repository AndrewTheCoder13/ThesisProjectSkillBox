package main.controller;

import lombok.AllArgsConstructor;
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
@AllArgsConstructor
public class ApiPostController {

    private final PostService postService;

    @GetMapping("")
    public ResponseEntity<PostsResponse> post( @RequestParam(required = false, defaultValue = "0") int offset,
                                               @RequestParam(required = false, defaultValue = "10") int limit,
                                               @RequestParam(required = false, defaultValue = "recent") String mode) {
        return postService.postsForMainPage(mode, offset, limit);
    }

    @GetMapping("search")
    public ResponseEntity<PostsResponse> searchPosts(@RequestParam(required = false, defaultValue = "0") int offset,
                                                     @RequestParam(required = false, defaultValue = "10") int limit,
                                                     @RequestParam(required = false) String query) {
        return postService.searchPosts(offset, limit, query);
    }

    @GetMapping("byDate")
    public ResponseEntity<PostsResponse> getPostsByDate(@RequestParam(required = false, defaultValue = "0") int offset,
                                                        @RequestParam(required = false, defaultValue = "10") int limit,
                                                        @RequestParam(required = false) String date) {
        return postService.searchPostsByDate(offset, limit, date);
    }

    @GetMapping("byTag")
    public ResponseEntity<PostsResponse> getPostByTag(@RequestParam(required = false, defaultValue = "0") int offset,
                                                      @RequestParam(required = false, defaultValue = "10") int limit,
                                                      @RequestParam(required = false) String tag) {
        return postService.getPostByTag(offset, limit, tag);
    }

    @GetMapping("moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<PostsResponse> getPostForModeration(@RequestParam(required = false, defaultValue = "0") int offset,
                                                              @RequestParam(required = false, defaultValue = "10") int limit,
                                                              @RequestParam(required = false, defaultValue = "new") String status) {
        return postService.getPostForModeration(offset, limit, status);
    }

    @GetMapping("my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostsResponse> geyMyPosts(@RequestParam(required = false, defaultValue = "0") int offset,
                                                    @RequestParam(required = false, defaultValue = "10") int limit,
                                                    @RequestParam(required = false, defaultValue = "inactive ") String status,
                                                    Principal principal) {
        return postService.getMyPosts(offset, limit, status, principal);
    }

    @GetMapping("{id}")
    public ResponseEntity<GetPostResponse> getPost(@PathVariable int id, Principal principal) {
        return postService.findById(id, principal);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PostMapping("")
    public ResponseEntity<PutPostResponse> putPost(Principal principal, @RequestBody PostAdd postForAdd){
        return postService.putOrEditPost(principal, postForAdd);
    }

    @PreAuthorize("hasAuthority('user:write')")
    @PutMapping("{id}")
    public ResponseEntity<PutPostResponse> editPost(Principal principal, @RequestBody PostAdd postForAdd, @PathVariable int id){
        return postService.putOrEditPost(principal, postForAdd, id);
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
