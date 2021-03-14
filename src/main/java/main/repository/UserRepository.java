package main.repository;

import main.model.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Integer> {
    @Query("From User WHERE email = ?1")
    Optional<User> findByEmail(String email);

    @Query("From User WHERE code = ?1")
    Optional<User> findByCode(String code);
}
