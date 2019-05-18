package com.spring.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.spring.model.SecurityRelatedQuestions;

public interface SecurityRelatedQuestionRepository extends JpaRepository<SecurityRelatedQuestions,Long>  {
	
}
