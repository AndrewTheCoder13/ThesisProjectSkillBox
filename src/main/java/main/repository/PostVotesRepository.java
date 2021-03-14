package main.repository;

import main.model.PostVote;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostVotesRepository extends CrudRepository<PostVote, Integer> {

    @Query("FROM PostVote WHERE user_id = ?1 AND post_id = ?2")
    Optional<PostVote> byUserAndPostId(int userId, int postId);
}
