package main.service;

import main.api.response.post.GetPostResponse;
import main.api.response.post.PostsResponse;
import main.model.Post;
import main.model.PostComment;
import main.model.TagToPost;
import main.repository.PostCommentRepository;
import main.repository.PostRepository;
import main.repository.Tag2PostRepository;
import main.repository.TagRepository;
import org.javatuples.Triplet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostCommentRepository postCommentRepository;

    @Autowired
    private Tag2PostRepository tag2PostRepository;

    @Autowired
    private TagRepository tagRepository;

    public ResponseEntity<PostsResponse> searchPosts(Map<String, String> allParams) {
        int offset = Integer.parseInt(allParams.get("offset"));
        int limit = Integer.parseInt(allParams.get("limit"));
        String query = allParams.get("query");
        Pageable pageable = PageRequest.of(offset, limit);
        if (query == null) {
            return ResponseEntity.ok().body(findAllWithPageable(pageable));
        } else {
            ArrayList<Post> posts = postRepository.searchPostsByQuery(query, LocalDateTime.now(), pageable);
            PostsResponse response = posts == null ? PostsResponse.getEmptyResonse() : new PostsResponse(posts);
            return ResponseEntity.ok().body(response);
        }
    }

    public ResponseEntity<PostsResponse> getPostByTag(Map<String, String> allParams) {
        int offset = Integer.parseInt(allParams.get("offset"));
        int limit = Integer.parseInt(allParams.get("limit"));
        String tagName = allParams.get("tag");
        int tagId = tagRepository.getTagByName(tagName).getId();
        Pageable pageable = PageRequest.of(offset, limit);
        ArrayList<Post> posts = (ArrayList<Post>) tag2PostRepository.findsPosts(tagId, LocalDateTime.now(), pageable);
        switch (posts.size()) {
            case 0:
                return ResponseEntity.ok().body(PostsResponse.getEmptyResonse());
            default: {
                return ResponseEntity.ok().body(new PostsResponse(posts));
            }
        }
    }

    public ResponseEntity<GetPostResponse> findById(int id) {
        Optional<Post> optionalPost = Optional.ofNullable(postRepository.findByIdAndActive(id, LocalDateTime.now()));
        if (!optionalPost.isPresent()) return ResponseEntity.notFound().build();
        Post gettedPost = optionalPost.get();
        List<TagToPost> tags = tag2PostRepository.findPostById(id);
        List<PostComment> postComments = postCommentRepository.findByPostId(id);
        GetPostResponse response = new GetPostResponse();
        response.formatingAnswer(gettedPost, postComments, tags);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<PostsResponse> selectWithMode(int offset, int limit, String mode) {
        ArrayList<Post> posts = (ArrayList<Post>) postRepository.findAllActive(LocalDateTime.now());
        posts.sort(mode.equals("best") ? Comparator.comparing(Post::getLikeCount).reversed() : Comparator.comparing(Post::getPostCommentsSize).reversed());
        List<Post> post = makingSubArray(new Triplet<>(offset, limit, posts));
        PostsResponse response = new PostsResponse(post);
        return ResponseEntity.ok().body(response);
    }

    public ResponseEntity<PostsResponse> searchPostsByDate(Map<String, String> allParams) {
        int offset = Integer.parseInt(allParams.get("offset"));
        int limit = Integer.parseInt(allParams.get("limit"));
        String date[] = allParams.get("date").split("-");
        int year = Integer.parseInt(date[0]);
        int mount = Integer.parseInt(date[1]);
        int day = Integer.parseInt(date[2]);
        Pageable pageable = PageRequest.of(offset, limit);
        LocalDateTime time = LocalDateTime.of(year, mount, day, 0, 0);
        ArrayList<Post> posts = postRepository.findAllByDate(time, pageable);
        switch (posts.size()) {
            case 0:
                return ResponseEntity.ok().body(PostsResponse.getEmptyResonse());
            default: {
                return ResponseEntity.ok().body(new PostsResponse(posts));
            }
        }
    }

    public ResponseEntity<PostsResponse> getPostForModeration(Map<String, String> allParams) {
        int offset = Integer.parseInt(allParams.get("offset"));
        int limit = Integer.parseInt(allParams.get("limit"));
        String status = allParams.get("status");
        ArrayList<Post> posts;
        Pageable pageable = PageRequest.of(offset, limit);
        switch (status) {
            case "new":
                posts = postRepository.findNew(pageable);
                break;
            default:
                posts = postRepository.findMyModeration(1, status, pageable);
        }
        switch (posts.size()) {
            case 0:
                return ResponseEntity.ok().body(PostsResponse.getEmptyResonse());
            default: {
                return ResponseEntity.ok().body(new PostsResponse(posts));
            }
        }
    }


    public ResponseEntity<PostsResponse> getMyPosts(Map<String, String> allParams) {
        int offset = Integer.parseInt(allParams.get("offset"));
        int limit = Integer.parseInt(allParams.get("limit"));
        String status = allParams.get("status");
        status = status.equals("pending")? "NEW" : status.equals("declined")? "DECLINED" : "ACCEPTED";
        ArrayList<Post> posts;
        Pageable pageable = PageRequest.of(offset, limit);
        switch (status) {
            case "inactive ":
                posts = postRepository.myPostsInactive(1, pageable);
                break;
            default:
                posts = postRepository.myPostsWithStatus(1, status, pageable);
        }
        switch (posts.size()) {
            case 0:
                return ResponseEntity.ok().body(PostsResponse.getEmptyResonse());
            default: {
                return ResponseEntity.ok().body(new PostsResponse(posts));
            }
        }
    }

    private List<Post> makingSubArray(Triplet<Integer, Integer, ArrayList<Post>> triplet) {
        int begin = triplet.getValue0() * triplet.getValue1();
        int end = begin + triplet.getValue1() > triplet.getValue2().size() ? (begin + triplet.getValue1()) - triplet.getValue2().size() - 2 : begin + triplet.getValue1();
        return triplet.getValue2().subList(begin, end);
    }

    public PostsResponse findAllWithPageable(Pageable pageable) {
        ArrayList<Post> posts = postRepository.finAllWithPageable(LocalDateTime.now(), pageable);
        return new PostsResponse(posts);
    }

}
