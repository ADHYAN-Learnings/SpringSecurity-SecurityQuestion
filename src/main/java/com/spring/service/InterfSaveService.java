package com.spring.service;

import java.util.List;

import com.spring.model.AddUserModel;

public interface InterfSaveService {
     
	AddUserModel saveModel(AddUserModel addUserModel);
	
	List<AddUserModel> getUserDetails();
	
	void deleteUser(Long id);
	
	
}
