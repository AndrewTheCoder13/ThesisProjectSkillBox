package main.model;

import lombok.Data;
import lombok.ToString;
import main.model.security.Role;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;


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

    @OneToMany
    @JoinColumn(name = "user_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ToString.Exclude
    private List<Post> userPosts;

    public Role getRole(){
        return isModerator == 1? Role.MODERATOR : Role.USER;
    }


}
