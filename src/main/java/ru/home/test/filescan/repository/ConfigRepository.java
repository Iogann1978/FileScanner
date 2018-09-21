package ru.home.test.filescan.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.home.test.filescan.entity.Config;

@Repository
public interface ConfigRepository extends CrudRepository<Config, Long> {
    Config findByName(String name);
}
