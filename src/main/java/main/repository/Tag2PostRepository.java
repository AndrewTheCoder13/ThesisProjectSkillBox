package main.repository;

import main.model.Post;
import main.model.Tag;
import main.model.TagToPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface Tag2PostRepository extends CrudRepository<TagToPost, Integer> {
    @Query("FROM TagToPost WHERE post_id = ?1")
    List<TagToPost> findPostById(int id);

    @Query("SELECT COUNT(*) FROM TagToPost WHERE tag_id = ?1")
    int countOfPostsByTagId(int id);

    @Query("FROM TagToPost WHERE tag_id = ?1")
    List<TagToPost> findPostsByTag(int id, Pageable pageable);

    @Query("SELECT post FROM TagToPost WHERE tag_id = ?1 AND isActive = 1 AND moderationStatus = 'ACCEPTED' AND time < ?2")
    List<Post> findsPosts(int id, LocalDateTime time, Pageable pageable);
}
