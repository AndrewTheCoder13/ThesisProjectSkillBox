package main.api.response.comment;

import lombok.Data;
import main.api.response.user.UserForComment;
import main.model.PostComment;

import java.sql.Timestamp;

@Data
public class CommentForPost {
    private int id;
    private long timestamp;
    private String text;
    private UserForComment user;

    public CommentForPost(PostComment comment){
        id = comment.getId();
        timestamp = Timestamp.valueOf(comment.getTime()).getTime();
        text = comment.getText();
        user = new UserForComment(comment.getUser());
    }
}