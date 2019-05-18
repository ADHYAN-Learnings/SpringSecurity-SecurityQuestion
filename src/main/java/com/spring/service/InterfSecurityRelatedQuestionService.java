package com.spring.service;

import java.util.List;

import com.spring.model.SecurityRelatedQuestions;

public interface InterfSecurityRelatedQuestionService {
	
  List<SecurityRelatedQuestions> findAllTheQuestions();

SecurityRelatedQuestions findById(Long securityQuestionId);
}
