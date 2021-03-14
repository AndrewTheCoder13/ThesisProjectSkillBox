package main.api.responseAndAnswers.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordErrors {
    private String code;
    private String password;
    private String captcha;
}
