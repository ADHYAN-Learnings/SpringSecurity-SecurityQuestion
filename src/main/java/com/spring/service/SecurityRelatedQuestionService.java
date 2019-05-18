package com.spring.service;

import java.util.List;
import com.spring.model.SecurityRelatedQuestions;
import com.spring.persistence.SecurityRelatedQuestionRepository;

import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
@Transactional
public class SecurityRelatedQuestionService implements InterfSecurityRelatedQuestionService {
	
	@Autowired
	private SecurityRelatedQuestionRepository securityRelatedQuestionRepository;

	@Override
	public List<SecurityRelatedQuestions> findAllTheQuestions() {
		return securityRelatedQuestionRepository.findAll();
	}

	@Override
	public SecurityRelatedQuestions findById(Long QuestionId) {
		return securityRelatedQuestionRepository.getOne(QuestionId);
	}
}