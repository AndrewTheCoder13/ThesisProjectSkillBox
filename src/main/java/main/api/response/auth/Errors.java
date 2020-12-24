package main.api.response.auth;

import lombok.Data;

@Data
public class Errors {
    private String email;
    private String name;
    private String password;
    private String captcha;
}
