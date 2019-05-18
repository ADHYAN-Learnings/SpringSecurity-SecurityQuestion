package com.spring.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import com.spring.model.SecurityQuestionAnswer;

public interface SecurityQuestionAnswerRepository extends JpaRepository<SecurityQuestionAnswer,Long> {
	
   SecurityQuestionAnswer findByUserRegistrationId(Long userId);
}
