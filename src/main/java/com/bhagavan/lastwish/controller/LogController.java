package com.bhagavan.lastwish.controller;
import com.bhagavan.lastwish.model.AccessLog; import com.bhagavan.lastwish.repository.AccessLogRepository;
import org.springframework.web.bind.annotation.*; import java.util.List;

@RestController @RequestMapping("/api/audit-logs")
public class LogController {
    private final AccessLogRepository repo; public LogController(AccessLogRepository repo){this.repo=repo;}
    @GetMapping("/{wishId}") public List<AccessLog> logs(@PathVariable Long wishId){return repo.findTop100ByWishIdOrderByCreatedAtDesc(wishId);}
}
