package com.enosh.users.repository;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.enosh.users.model.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long>{
	
	boolean existsByUsernameAndPassword(String username, String password);
	Optional<User> findByUsernameAndPassword(String username, String password);
}
