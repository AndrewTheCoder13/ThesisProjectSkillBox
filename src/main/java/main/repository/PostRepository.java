package main.repository;

import main.model.ModerationStatus;
import main.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Integer> {
    @Query("FROM Post p WHERE (LOCATE(:query, p.title) != 0 OR LOCATE(:query, p.text) != 0) AND p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < :time")
    Page<Post> searchPostsByQuery(@Param(value = "query") String query,@Param(value = "time") LocalDateTime time, Pageable pageable);

    @Query(value = "FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < ?1")
    Page<Post> finAllWithPageable(LocalDateTime time, Pageable pageable);

    @Query("FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < ?1 ORDER BY p.time DESC")
    Page<Post> findRecentPosts(LocalDateTime time, Pageable pageable);

    @Query("FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < ?1 ORDER BY p.time ASC")
    Page<Post> findOldPosts(LocalDateTime time,Pageable pageable);

    @Query("FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < ?1 ORDER BY p.likeVotes.size DESC")
    Page<Post> findBestPosts(LocalDateTime time,Pageable pageable);

    @Query("FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time < ?1 ORDER BY p.postComments.size DESC")
    Page<Post> findMostCommentedPosts(LocalDateTime time,Pageable pageable);

    @Query("FROM Post WHERE is_active = 1 AND moderationStatus = 'ACCEPTED' AND time < ?1")
    List<Post> findAllActive(LocalDateTime time);

    @Query("FROM Post WHERE is_active = 1 AND moderationStatus = 'ACCEPTED' AND time < ?2 AND id = ?1")
    Post findByIdAndActive(int id,LocalDateTime time);

    @Query("FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'ACCEPTED' AND p.time BETWEEN ?1 AND ?2")
    Page<Post> findAllByDate(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    @Query("FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = 'NEW'")
    Page<Post> findNew(Pageable pageable);

    @Query("FROM Post p WHERE p.isActive = 1 AND p.moderationStatus = ?2 AND p.moderator.id = ?1")
    Page<Post> findMyModeration(int id,ModerationStatus mode, Pageable pageable);

    @Query("FROM Post p WHERE p.isActive = 0 AND p.user.id = ?1")
    Page<Post> myPostsInactive(int id, Pageable pageable);

    @Query("FROM Post p WHERE p.isActive = 1 AND p.user.id = ?1 AND p.moderationStatus = ?2 ORDER BY p.time DESC")
    Page<Post> myPostsWithStatus(int id, ModerationStatus status, Pageable pageable);

    @Query("FROM Post WHERE is_active = 1 AND user_id = ?1 AND moderation_status = ?2")
    List<Post> myPosts(int id, String status);

    @Query("SELECT time FROM Post WHERE is_active = 1 AND moderation_status = 'ACCEPTED'")
    List<LocalDateTime> years();
}
