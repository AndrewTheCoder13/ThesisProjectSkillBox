package main.repository;

import main.model.Post;
import main.model.Tag;
import main.model.TagToPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
    @Query("FROM Post WHERE LOCATE(:query, title) != 0 OR LOCATE(:query, text) != 0 AND is_active = 1 AND moderation_status = 'ACCEPTED' AND time < :time")
    ArrayList<Post> searchPostsByQuery(@Param(value = "query") String query,@Param(value = "time") LocalDateTime time, Pageable pageable);

    @Query("FROM Post WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < ?1")
    ArrayList<Post> finAllWithPageable(LocalDateTime time, Pageable pageable);

    @Query("FROM Post WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < ?1")
    ArrayList<Post> findAllActive(LocalDateTime time);

    @Query("FROM Post WHERE is_active = 1 AND moderation_status = 'ACCEPTED' AND time < ?2 AND id = ?1")
    Post findByIdAndActive(int id,LocalDateTime time);

}
