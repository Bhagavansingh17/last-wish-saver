package com.bhagavan.lastwish.controller;
import com.bhagavan.lastwish.dto.VerificationDtos; import com.bhagavan.lastwish.model.VerificationRequest; import com.bhagavan.lastwish.service.VerificationService;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*; import java.util.List;

@RestController @RequestMapping("/api")
public class VerificationController {
    private final VerificationService service; public VerificationController(VerificationService service){this.service=service;}
    @PostMapping("/wishes/{wishId}/verification-requests") public VerificationRequest create(@PathVariable Long wishId,@Valid @RequestBody VerificationDtos.CreateVerificationRequest r){return service.create(wishId,r);}
    @GetMapping("/verification-requests/mine") public List<VerificationRequest> mine(){return service.mine();}
    @PostMapping("/verification-requests/{id}/decision") public VerificationRequest decide(@PathVariable Long id,@RequestBody VerificationDtos.DecisionRequest r){return service.decide(id,r);}
}
