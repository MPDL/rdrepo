package de.mpg.mpdl.r2d2.db;

import java.util.UUID;

import org.springframework.data.repository.CrudRepository;

import de.mpg.mpdl.r2d2.model.Dataset;
import de.mpg.mpdl.r2d2.model.aa.User;

public interface UserRepository extends CrudRepository<User, UUID>{
	
	public User findByEmail(String email);

}
