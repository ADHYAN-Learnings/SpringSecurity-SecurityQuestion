package com.spring.service;

import com.spring.model.RegistrationFlow;
import com.spring.model.SecurityQuestionAnswer;
import com.spring.model.SecurityRelatedQuestions;

public interface InterfSecurityQuestionAnswerService {
	
	SecurityQuestionAnswer save(final RegistrationFlow user,final SecurityRelatedQuestions securityQuestions, final String securityAnswer);
	SecurityQuestionAnswer findByUserId(Long userId);
}
