package ru.home.test.filescan.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.home.test.filescan.entity.Action;

@Repository
public interface ActionRepository extends CrudRepository<Action, Long> {
}
