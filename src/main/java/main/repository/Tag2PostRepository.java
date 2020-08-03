package main.repository;

import main.model.TagToPost;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface Tag2PostRepository extends CrudRepository<TagToPost, Integer> {
}
