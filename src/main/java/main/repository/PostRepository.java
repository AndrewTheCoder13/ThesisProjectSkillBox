package main.repository;

import main.model.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends PagingAndSortingRepository<Post, Integer> {
    @Query("FROM Post WHERE (LOCATE(:query, title) != 0 OR LOCATE(:query, text) != 0) AND is_active = 1 AND moderation_status = 'ACCEPTED' AND time < :time")
    List<Post> searchPostsByQuery(@Param(value = "query") String query,@Param(value = "time") LocalDateTime time, Pageable pageable);

    @Query(value = "FROM Post WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < ?1")
    List<Post> finAllWithPageable(LocalDateTime time, Pageable pageable);

    @Query("FROM Post WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < ?1 ORDER BY time DESC")
    List<Post> findRecentPosts(LocalDateTime time,Pageable pageable);

    @Query("FROM Post WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < ?1 ORDER BY time ASC")
    List<Post> findOldPosts(LocalDateTime time,Pageable pageable);

    @Query(nativeQuery = true, value = "select *, count(case when post_votes.value = 1 then 1 else null end) as count from posts left join post_votes ON posts.id = post_votes.post_id WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND posts.time < CURRENT_TIME() group by post_id order by count DESC")
    List<Post> findBestPosts(LocalDateTime time,Pageable pageable);

    @Query(nativeQuery = true, value = "select *, case when post_comments.id is null then 0 else count(*) end count from posts left join post_comments ON posts.id = post_comments.post_id WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND posts.time < CURRENT_TIME() group by posts.id order by count DESC")
    List<Post> findMostCommentedPosts(LocalDateTime time,Pageable pageable);

    @Query("FROM Post WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < ?1")
    List<Post> findAllActive(LocalDateTime time);

    @Query("FROM Post WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < ?2 AND id = ?1")
    Post findByIdAndActive(int id,LocalDateTime time);

    @Query("FROM Post WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time BETWEEN ?1 AND ?2")
    List<Post> findAllByDate(LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);

    @Query("FROM Post WHERE is_active = 1 AND moderation_status = 'NEW'")
    List<Post> findNew(Pageable pageable);

    @Query("FROM Post WHERE is_active = 1 AND moderation_status = ?2 AND moderator_id = ?1")
    List<Post> findMyModeration(int id,String mode, Pageable pageable);

    @Query("FROM Post WHERE is_active = 0 AND user_id = ?1")
    List<Post> myPostsInactive(int id, Pageable pageable);

    @Query("FROM Post WHERE is_active = 1 AND user_id = ?1 AND moderation_status = ?2")
    List<Post> myPostsWithStatus(int id, String status, Pageable pageable);

    @Query("FROM Post WHERE is_active = 1 AND user_id = ?1 AND moderation_status = ?2")
    List<Post> myPosts(int id, String status);

    @Query("SELECT time FROM Post WHERE is_active = 1 AND moderation_status = 'ACCEPTED'")
    List<LocalDateTime> years();
}
