package main.api.responseAndAnswers.post;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ModerationRequest {
    @JsonProperty("post_id")
    private int postId;
    private String decision;
}
