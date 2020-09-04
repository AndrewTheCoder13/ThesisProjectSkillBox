package main.controller;

import main.api.response.post.GetPostResponse;
import main.api.response.post.PostsResponse;
import main.model.Post;
import main.model.PostComment;
import main.model.TagToPost;
import main.repository.*;
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
    private PostRepository postRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private Tag2PostRepository tag2PostRepository;

    @Autowired
    private TagRepository tagRepository;

    @GetMapping("/api/post")
    public ResponseEntity<PostsResponse> post(@RequestParam Map<String, String> allParams){
        int offset = Integer.parseInt(allParams.get("offset"));
        int limit = Integer.parseInt(allParams.get("limit"));
        String mode = allParams.get("mode");
        Pageable pageable = null;
        switch (mode){
            case "recent": pageable = PageRequest.of(offset, limit, new Sort(Sort.Direction.DESC, "time"));
                           break;
            case "early": pageable = PageRequest.of(offset, limit, new Sort(Sort.Direction.ASC, "time"));
                           break;
            case "popular": {
                ArrayList<Post> posts = (ArrayList<Post>)postRepository.findAll();
                posts.sort(Comparator.comparing(Post::getPostCommentsSize).reversed() );
                int begin = offset * limit;
                int end = 0;
                end = begin + limit > posts.size() ? (begin + limit) - posts.size() - 2 : begin + limit;
                List<Post> post = posts.subList(begin, end);
                PostsResponse response = new PostsResponse(post);
                return ResponseEntity.ok().body(response);
            }
            case "best": {
                ArrayList<Post> posts = (ArrayList<Post>)postRepository.findAll();
                posts.sort(Comparator.comparing(Post::getLikeCount).reversed() );
                int begin = offset * limit;
                int end = 0;
                end = begin + limit > posts.size() ? (begin + limit) - posts.size() - 2 : begin + limit;
                List<Post> post = posts.subList(begin, end);
                PostsResponse response = new PostsResponse(post);
                return ResponseEntity.ok().body(response);
            }
        }
        Iterable<Post> posts = postRepository.finAllWithPageable(pageable);
        PostsResponse response = new PostsResponse((ArrayList<Post>) posts);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("api/post/{id}")
    public ResponseEntity<GetPostResponse> getPost(@PathVariable int id){
        Optional<Post> optionalPost = postRepository.findById(id);
        Post gettedPost = new Post();
        if(optionalPost.isPresent()){
            gettedPost = optionalPost.get();
        } else {
            return ResponseEntity.notFound().build();
        }
        List<TagToPost> tags = tag2PostRepository.findPostById(id);
        List<PostComment> postComments = postCommentRepository.findByPostId(id);
        GetPostResponse response = new GetPostResponse();
        response.formatingAnswer(gettedPost, postComments, tags);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("api/post/byTag")
    public ResponseEntity<PostsResponse> getPostByTag(@RequestParam Map<String, String> allParams){
        int offset = Integer.parseInt(allParams.get("offset"));
        int limit = Integer.parseInt(allParams.get("limit"));
        String tagName = allParams.get("tag");
        int tagId = tagRepository.getTagByName(tagName).getId();
        Pageable pageable = PageRequest.of(offset, limit);
        ArrayList<TagToPost> postsToTag = (ArrayList<TagToPost>) tag2PostRepository.findPostsByTag(tagId, pageable);
        if(postsToTag.size() == 0){
            return ResponseEntity.ok().body(PostsResponse.getEmptyResonse());
        }
        ArrayList<Post> postsByTag = new ArrayList<>();
        postsToTag.forEach(tagToPost -> postsByTag.add(tagToPost.getPost()));
        PostsResponse response = new PostsResponse(postsByTag);
        return ResponseEntity.ok().body(response);
    }

    @GetMapping("/api/post/search")
    public ResponseEntity<PostsResponse> searchPosts(@RequestParam Map<String, String> allParams){
        int offset = Integer.parseInt(allParams.get("offset"));
        int limit = Integer.parseInt(allParams.get("limit"));
        String query = allParams.get("query");
        Pageable pageable = PageRequest.of(offset, limit);
        if(query == null){
            Iterable<Post> posts = postRepository.finAllWithPageable(pageable);
            PostsResponse response = new PostsResponse((ArrayList<Post>) posts);
            return ResponseEntity.ok().body(response);
        } else {
            ArrayList<Post> posts = postRepository.searchPostsByQuery(query, pageable);
            PostsResponse response = new PostsResponse(posts);
            return ResponseEntity.ok().body(response);
        }
    }
}
