package com.bhagavan.lastwish.repository;

import com.bhagavan.lastwish.model.Wish;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface WishRepository extends JpaRepository<Wish, Long> {
    List<Wish> findByOwnerIdOrderByCreatedAtDesc(Long ownerId); Optional<Wish> findByIdAndOwnerId(Long id, Long ownerId);
}
