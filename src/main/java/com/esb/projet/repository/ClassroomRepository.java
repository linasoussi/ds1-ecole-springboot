package com.esb.projet.repository;

import com.esb.projet.model.Classroom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClassroomRepository extends JpaRepository<Classroom, Long> {

    boolean existsByName(String name);
}
