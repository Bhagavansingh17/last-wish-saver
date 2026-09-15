package com.bhagavan.lastwish.service;

import com.bhagavan.lastwish.dto.BeneficiaryDtos;
import com.bhagavan.lastwish.model.*;
import com.bhagavan.lastwish.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class BeneficiaryService {
    private final BeneficiaryRepository beneficiaries; private final WishRepository wishes; private final WishAccessRepository access; private final AccessLogRepository logs; private final CurrentUser current;
    public BeneficiaryService(BeneficiaryRepository b, WishRepository w, WishAccessRepository a, AccessLogRepository l, CurrentUser c){beneficiaries=b;wishes=w;access=a;logs=l;current=c;}
    public List<Beneficiary> mine(){return beneficiaries.findByOwnerId(current.get().getId());}
    @Transactional public Beneficiary add(BeneficiaryDtos.CreateBeneficiaryRequest r){
        Beneficiary b=new Beneficiary(); b.setOwner(current.get()); b.setName(r.name()); b.setEmail(r.email()); b.setRelationship(r.relationship()); return beneficiaries.save(b);
    }
    @Transactional public WishAccess grant(Long wishId, BeneficiaryDtos.GrantAccessRequest r){
        Wish w=wishes.findByIdAndOwnerId(wishId,current.get().getId()).orElseThrow(()->new IllegalArgumentException("Wish not found"));
        Beneficiary b=beneficiaries.findByIdAndOwnerId(r.beneficiaryId(),current.get().getId()).orElseThrow(()->new IllegalArgumentException("Beneficiary not found"));
        WishAccess x=access.findByWishIdAndBeneficiaryId(wishId,b.getId()).orElseGet(WishAccess::new);
        x.setWish(w);x.setBeneficiary(b);x.setEnabled(true);
        x.setAccessLevel("DOWNLOAD".equalsIgnoreCase(r.accessLevel())?Enums.AccessLevel.DOWNLOAD:Enums.AccessLevel.VIEW);
        WishAccess saved=access.save(x);
        AccessLog l=new AccessLog();l.setActor(current.get());l.setWishId(wishId);l.setAction("GRANT_ACCESS");l.setDetails("Access granted to beneficiary "+b.getId());logs.save(l);
        return saved;
    }
}
