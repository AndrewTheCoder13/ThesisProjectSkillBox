package main.api.responseAndAnswers.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RestoreResponse {
    private boolean result;
    private Errors errors;
}
