package com.bhagavan.lastwish.repository;

import com.bhagavan.lastwish.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email); boolean existsByEmail(String email);
}
