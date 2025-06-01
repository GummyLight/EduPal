
package com.example.edupal.repository;

import com.example.edupal.model.LearningProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LearningProgressRepository extends JpaRepository<LearningProgress, Long>{
    LearningProgress findByStudentId(String studentId);
}
