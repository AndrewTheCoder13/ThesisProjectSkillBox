package main.model;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false, name = "is_active")
    private byte isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "moderation_status", columnDefinition = "enum('NEW', 'ACCEPTED', 'DECLINED')")
    private ModerationStatus moderationStatus;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDateTime time;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false, name = "view_count")
    private int viewCount;
}
