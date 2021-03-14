package main.api.responseAndAnswers.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordRequest {
    private String code;
    private String password;
    private String captcha;
    @JsonProperty("captcha_secret")
    private String captchaSecret;
}
