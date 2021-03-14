package main.api.responseAndAnswers.exceptions.maxSize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.api.responseAndAnswers.image.ImageErrors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MaxSize {
    private boolean result;
    private ImageErrors errors;
}
