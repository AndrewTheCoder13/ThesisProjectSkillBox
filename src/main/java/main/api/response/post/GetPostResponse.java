package main.api.response.post;

import lombok.Data;
import main.api.response.comment.CommentForPost;
import main.api.response.user.UserForPost;
import main.model.Post;
import main.model.PostComment;
import main.model.TagToPost;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Data
public class GetPostResponse {
    private int id;
    private long timestamp;
    private boolean active;
    private UserForPost user;
    private String title;
    private String text;
    private int likeCount;
    private int dislikeCount;
    private int viewCount;
    private List<CommentForPost> comments;
    private List<String> tags;

    public void formatingAnswer(Post post, List<PostComment> postComments, List<TagToPost> tags){
        comments = new ArrayList<>();
        this.tags = new ArrayList<>();
        active = true;
        id = post.getId();
        timestamp = Timestamp.valueOf(post.getTime()).getTime();
        user = new UserForPost(post.getUser());
        title = post.getTitle();
        text = post.getText();
        likeCount = post.getLikeVotes().size();
        dislikeCount = post.getDislikeVotes().size();
        viewCount = post.getViewCount();
        postComments.stream().map(CommentForPost::new).forEach(commentForPost -> comments.add(commentForPost));
        tags.forEach(tagToPost -> this.tags.add(tagToPost.getTag().getName()));
    }
}
