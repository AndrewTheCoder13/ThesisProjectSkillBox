package main.api.response.post;

import lombok.Data;
import main.model.Post;

import java.util.ArrayList;
import java.util.List;

@Data
public class PostsResponse {
    private int count;
    private List<SearchPostResponse> posts;

    public static PostsResponse getEmptyResonse(){
        PostsResponse response = new PostsResponse();
        response.setCount(0);
        return response;
    }

    public PostsResponse(){
        posts = new ArrayList<>();
    }

    public PostsResponse(List<Post> postsByTag){
        posts = new ArrayList<>();
        count = postsByTag.size();
        postsByTag.forEach(post -> {
            SearchPostResponse postResponse = new SearchPostResponse(post);
            posts.add(postResponse);
        });
    }
}
