package main.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_votes")
@Data
public class PostVote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @ManyToOne()
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne()
    @JoinColumn(name = "post_id", nullable = false)
    private Post post;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private byte value;

}
