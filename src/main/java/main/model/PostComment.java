package main.model;


import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_comments")
@Data
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    private PostComment parent;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private Post post;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private String text;

}
