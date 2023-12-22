package com.example.debeziumPostgres.repository;

import com.example.debeziumPostgres.domain.Change;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChangeRepository extends JpaRepository<Change, Long> {

}
