package main.api.responseAndAnswers.post;

import lombok.Data;

@Data
public class PostErrors {
    private String title;
    private String text;

    public boolean checkErrors(PostErrors errors) {
        return errors.getText() != null || errors.getTitle() != null;
    }
}
