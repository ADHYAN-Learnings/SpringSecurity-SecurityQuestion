package com.spring.service;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.spring.model.RegistrationFlow;
import com.spring.model.SecurityQuestionAnswer;
import com.spring.model.SecurityRelatedQuestions;
import com.spring.persistence.SecurityQuestionAnswerRepository;

@Service
@Transactional
public class SecurityQuestionAnswerService implements InterfSecurityQuestionAnswerService {
	
	@Autowired
	private SecurityQuestionAnswerRepository securityQuestionAnswerRepository;

	@Override
	public SecurityQuestionAnswer save(RegistrationFlow user, SecurityRelatedQuestions securityQuestions,
			String securityAnswer) {
		return securityQuestionAnswerRepository.save( new SecurityQuestionAnswer(user,securityQuestions,securityAnswer));
	}

	@Override
	public SecurityQuestionAnswer findByUserId(Long userId) {
		return securityQuestionAnswerRepository.findByUserRegistrationId(userId);
	}
}
