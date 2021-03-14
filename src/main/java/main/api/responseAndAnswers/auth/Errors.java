package main.api.responseAndAnswers.auth;

import lombok.Data;

@Data
public class Errors {
    private String email;
    private String name;
    private String password;
    private String captcha;

    public boolean checkErrors() {
        return getEmail() != null || getPassword() != null || getName() != null || getCaptcha() != null;
    }
}
