package main.repository;

import main.model.Post;
import main.model.PostComment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

@Repository
public interface PostCommentRepository extends CrudRepository<PostComment, Integer> {
    @Query("FROM PostComment WHERE post_id = ?1")
    List<PostComment> findByPostId(int id);

}
