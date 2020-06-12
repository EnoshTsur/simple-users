package com.enosh.users.repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.enosh.users.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{
	
	boolean existsByUsernameAndPassword(String username, String password);
}
