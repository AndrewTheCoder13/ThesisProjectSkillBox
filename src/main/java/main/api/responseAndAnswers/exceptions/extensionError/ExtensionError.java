package main.api.responseAndAnswers.exceptions.extensionError;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.api.responseAndAnswers.image.ImageErrors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExtensionError {
    private boolean result;
    private ImageErrors errors;
}
