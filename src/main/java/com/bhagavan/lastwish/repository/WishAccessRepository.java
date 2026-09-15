package com.bhagavan.lastwish.repository;

import com.bhagavan.lastwish.model.WishAccess;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface WishAccessRepository extends JpaRepository<WishAccess, Long> {
    Optional<WishAccess> findByWishIdAndBeneficiaryId(Long wishId, Long beneficiaryId); List<WishAccess> findByBeneficiaryId(Long beneficiaryId);
}
