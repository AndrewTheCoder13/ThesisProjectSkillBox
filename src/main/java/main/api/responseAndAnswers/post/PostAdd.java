package main.api.responseAndAnswers.post;

import lombok.Data;

@Data
public class PostAdd {
    private long timestamp;
    private byte active;
    private String title;
    private String[] tags;
    private String text;
}
