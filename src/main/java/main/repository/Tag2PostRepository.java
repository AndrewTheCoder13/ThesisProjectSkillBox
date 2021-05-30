package main.repository;

import main.model.Post;
import main.model.Tag;
import main.model.TagToPost;
import org.springframework.data.domain.Page;
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

    @Query("SELECT COUNT(*) FROM TagToPost ttp WHERE ttp.tag.id = ?1 AND ttp.post.isActive = 1 AND ttp.post.moderationStatus = 'ACCEPTED' AND ttp.post.time < ?2")
    int countOfPostsByTagId(int id, LocalDateTime time);

    @Query("FROM TagToPost WHERE tag_id = ?1")
    List<TagToPost> findPostsByTag(int id, Pageable pageable);

    @Query("SELECT ttp.post FROM TagToPost ttp WHERE ttp.tag.id = ?1 AND ttp.post.isActive = 1 AND ttp.post.moderationStatus = 'ACCEPTED' AND ttp.post.time < ?2")
    Page<Post> findsPosts(int id, LocalDateTime time, Pageable pageable);

    @Query("SELECT ttp FROM TagToPost ttp WHERE ttp.post.id = ?1 AND ttp.tag.name = ?2")
    TagToPost findByTagAndPost(int postId, String tagName);
}
