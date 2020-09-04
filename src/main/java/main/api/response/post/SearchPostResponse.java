package main.api.response.post;

import lombok.Data;
import main.api.response.user.UserForPost;
import main.model.Post;

import java.sql.Timestamp;

@Data
public class SearchPostResponse {
    private int id;
    private long timestamp;
    private String title;
    private String announce;
    private int likeCount;
    private int dislikeCount;
    private int commentCount;
    private int viewCount;
    private UserForPost user;

    public SearchPostResponse(Post post){
        id = post.getId();
        timestamp = Timestamp.valueOf(post.getTime()).getTime();
        title = post.getTitle();
        announce = post.getText();
        likeCount = post.getLikeVotes().size();
        dislikeCount = post.getDislikeVotes().size();
        viewCount = post.getViewCount();
        commentCount = post.getPostComments().size();
        user = new UserForPost(post.getUser());
    }
}
