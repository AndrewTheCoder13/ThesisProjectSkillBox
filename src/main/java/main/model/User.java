package main.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;


@Entity
@Table(name = "users")
@Data
public class User
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @NotNull
    @Column(name = "is_moderator", nullable = false)
    private byte isModerator;

    @NotNull
    @Column(name = "reg_time", nullable = false)
    private LocalDateTime regTime;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    private String code;

    private String photo;


}
