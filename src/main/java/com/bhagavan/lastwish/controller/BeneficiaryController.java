package com.bhagavan.lastwish.controller;
import com.bhagavan.lastwish.dto.BeneficiaryDtos; import com.bhagavan.lastwish.model.*; import com.bhagavan.lastwish.service.BeneficiaryService;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*; import java.util.List;

@RestController @RequestMapping("/api")
public class BeneficiaryController {
    private final BeneficiaryService service; public BeneficiaryController(BeneficiaryService service){this.service=service;}
    @GetMapping("/beneficiaries") public List<Beneficiary> mine(){return service.mine();}
    @PostMapping("/beneficiaries") public Beneficiary add(@Valid @RequestBody BeneficiaryDtos.CreateBeneficiaryRequest r){return service.add(r);}
    @PostMapping("/wishes/{wishId}/access") public WishAccess grant(@PathVariable Long wishId,@RequestBody BeneficiaryDtos.GrantAccessRequest r){return service.grant(wishId,r);}
}
