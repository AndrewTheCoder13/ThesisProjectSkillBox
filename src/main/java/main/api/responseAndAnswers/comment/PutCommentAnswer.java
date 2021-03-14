package main.api.responseAndAnswers.comment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PutCommentAnswer {
    private Integer id;
    private Boolean result;
    private CommentErrors errors;
}
