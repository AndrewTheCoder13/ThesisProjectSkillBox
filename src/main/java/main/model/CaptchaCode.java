package main.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "captcha_codes")
@Data
public class CaptchaCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false, name = "secret_code")
    private String secretCode;
}
