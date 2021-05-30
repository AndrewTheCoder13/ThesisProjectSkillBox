package main.api.responseAndAnswers.post;

import lombok.Data;
import main.model.Post;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostsResponse {
    private long count;
    private List<SearchPostResponse> posts;

    public static PostsResponse getEmptyResonse() {
        PostsResponse response = new PostsResponse();
        response.setCount(0);
        return response;
    }

    public PostsResponse() {
        posts = new ArrayList<>();
    }

    public PostsResponse(Page<Post> receivedPosts){
        posts = new ArrayList<>();
        count = receivedPosts.getTotalElements();
        receivedPosts.forEach(post -> {
            SearchPostResponse postResponse = new SearchPostResponse(post);
            posts.add(postResponse);
        });
    }

}
