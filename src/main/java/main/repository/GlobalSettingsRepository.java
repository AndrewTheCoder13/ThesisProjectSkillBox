package main.repository;

import main.model.GlobalSetting;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GlobalSettingsRepository extends CrudRepository<GlobalSetting, Integer> {

    @Query("FROM GlobalSetting WHERE code = ?1")
    Optional<GlobalSetting> findByCode(String code);
}
