package main.repository;

import main.model.Tag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends CrudRepository<Tag, Integer> {

    @Query("FROM Tag WHERE name = :name")
    Optional<Tag> getTagByName(@Param(value = "name") String name);

    @Query("FROM Tag WHERE LOCATE(:query, name) != 0")
    List<Tag> searchTagByQuery(@Param(value = "query") String query);
}
