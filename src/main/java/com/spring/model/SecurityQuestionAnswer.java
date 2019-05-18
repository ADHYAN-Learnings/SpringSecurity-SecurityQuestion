package com.spring.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

@Entity
public class SecurityQuestionAnswer {
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	@Column(name="Id")
	private Long id;
	
	@OneToOne(targetEntity=RegistrationFlow.class, fetch=FetchType.EAGER)
	@JoinColumn(nullable=false,name="security_user_id")
	private RegistrationFlow user;
	
	@OneToOne(targetEntity=SecurityRelatedQuestions.class, fetch=FetchType.EAGER)
	@JoinColumn(nullable=false,name="Security_Question_Answer_id")
	private SecurityRelatedQuestions securityQuestions;
	
	@JoinColumn(name="answer")
	private String answer;
	
	
	
	public SecurityQuestionAnswer() {
		super();
	}

	public SecurityQuestionAnswer(final RegistrationFlow user, final SecurityRelatedQuestions securityQuestions, final String answer) {
		this.user=user;
		this.securityQuestions=securityQuestions;
		this.answer=answer;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public RegistrationFlow getUser() {
		return  user;
	}

	public void setUser(RegistrationFlow user) {
		this.user = user;
	}
	
	public SecurityRelatedQuestions getSecurityQuestions() {
		return securityQuestions;
	}

	public void setSecurityQuestions(SecurityRelatedQuestions securityQuestions) {
		this.securityQuestions = securityQuestions;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	@Override
	public String toString() {
		return "SecurityQuestionAnswer [id=" + id + ", user=" + user + ", securityQuestions=" + securityQuestions
				+ ", answer=" + answer + "]";
	}
}
