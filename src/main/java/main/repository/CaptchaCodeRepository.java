package main.repository;

import main.model.CaptchaCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CaptchaCodeRepository extends CrudRepository<CaptchaCode, Integer> {
    @Query("SELECT COUNT(*) FROM  CaptchaCode WHERE secretCode = :ident")
    int countOfIdentification(@Param("ident") String identification);

    @Query("DELETE FROM CaptchaCode WHERE time < :hourAgo")
    @Transactional
    @Modifying
    void oldVersions(@Param(value = "hourAgo") LocalDateTime hourAgo);

    @Query("FROM CaptchaCode WHERE secretCode = :secret")
    CaptchaCode getBySecret(@Param(value = "secret") String secret);
}
