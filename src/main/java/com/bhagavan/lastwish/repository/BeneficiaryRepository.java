package com.bhagavan.lastwish.repository;

import com.bhagavan.lastwish.model.Beneficiary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BeneficiaryRepository extends JpaRepository<Beneficiary, Long> {

    List<Beneficiary> findByOwnerId(Long ownerId);

    Optional<Beneficiary> findByEmail(String email);

    Optional<Beneficiary> findByIdAndOwnerId(Long id, Long ownerId);
}