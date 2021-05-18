package main.model;

import lombok.Data;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "posts")
@Data
public class Post{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private int id;

    @Column(nullable = false, name = "is_active")
    private byte isActive;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, name = "moderation_status", columnDefinition = "enum('NEW', 'ACCEPTED', 'DECLINED')")
    private ModerationStatus moderationStatus;

    @ManyToOne()
    @JoinColumn(name = "moderator_id")
    private User moderator;

    @ManyToOne()
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

    @OneToMany
    @JoinColumn(name = "post_id", referencedColumnName = "id", insertable = false, updatable = false)
    @Where(clause = "value = -1")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<PostVote> dislikeVotes;

    @OneToMany
    @JoinColumn(name = "post_id", referencedColumnName = "id", insertable = false, updatable = false)
    @Where(clause = "value = 1")
    @LazyCollection(LazyCollectionOption.EXTRA)
    private List<PostVote> likeVotes;

    @OneToMany
    @JoinColumn(name = "post_id", referencedColumnName = "id", insertable = false, updatable = false)
    private List<PostComment> postComments;

    public Integer getPostCommentsSize(){
        return postComments.size();
    }

    public Integer getLikeCount(){
        return likeVotes.size();
    }

    public int getDifference(){
        return likeVotes.size()-dislikeVotes.size();
    }
}
