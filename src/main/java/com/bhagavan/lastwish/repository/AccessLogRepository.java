package com.bhagavan.lastwish.repository;

import com.bhagavan.lastwish.model.AccessLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface AccessLogRepository extends JpaRepository<AccessLog, Long> {
    List<AccessLog> findTop100ByWishIdOrderByCreatedAtDesc(Long wishId);
}
