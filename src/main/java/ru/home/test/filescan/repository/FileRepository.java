package ru.home.test.filescan.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.home.test.filescan.entity.File;

@Repository
public interface FileRepository extends CrudRepository<File, Long> {
    File findByName(String name);
}
