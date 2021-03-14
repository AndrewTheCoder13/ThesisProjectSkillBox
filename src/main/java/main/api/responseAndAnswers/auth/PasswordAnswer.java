package main.api.responseAndAnswers.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordAnswer {
    private boolean result;
    private PasswordErrors errors;
}
