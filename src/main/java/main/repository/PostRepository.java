package main.repository;

import main.model.Post;
import main.model.Tag;
import main.model.TagToPost;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public interface PostRepository extends CrudRepository<Post, Integer> {
    @Query("FROM Post WHERE LOCATE(:query, title) != 0 OR LOCATE(:query, text) != 0")
    ArrayList<Post> searchPostsByQuery(@Param(value = "query") String query, Pageable pageable);

    @Query("FROM Post")
    ArrayList<Post> finAllWithPageable(Pageable pageable);
}
