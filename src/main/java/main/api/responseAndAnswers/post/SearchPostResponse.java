package main.api.responseAndAnswers.post;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import main.api.responseAndAnswers.user.UserForPost;
import main.model.Post;
import org.jsoup.Jsoup;

import java.sql.Timestamp;

@Data
public class SearchPostResponse {
    private int id;
    @JsonSerialize
    @JsonDeserialize
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
        timestamp = Timestamp.valueOf(post.getTime()).getTime() / 1000;
        title = post.getTitle();
        announce = Jsoup.parse(post.getText()).text();
        announce = announce.length() < 200 ? announce : announce.substring(0, 200);
        likeCount = post.getLikeVotes().size();
        dislikeCount = post.getDislikeVotes().size();
        viewCount = post.getViewCount();
        commentCount = post.getPostComments().size();
        user = new UserForPost(post.getUser());
    }
}
