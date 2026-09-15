package com.bhagavan.lastwish.repository;

import com.bhagavan.lastwish.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByVerificationRequestId(Long requestId);
}
