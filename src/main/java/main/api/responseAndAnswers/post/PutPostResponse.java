package main.api.responseAndAnswers.post;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PutPostResponse {
    private boolean result;
    private PostErrors errors;
}
