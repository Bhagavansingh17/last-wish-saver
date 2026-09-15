package com.bhagavan.lastwish.controller;
import com.bhagavan.lastwish.dto.WishDtos; import com.bhagavan.lastwish.model.Wish; import com.bhagavan.lastwish.service.WishService;
import jakarta.validation.Valid; import org.springframework.web.bind.annotation.*; import java.util.List;

@RestController @RequestMapping("/api/wishes")
public class WishController {
    private final WishService service; public WishController(WishService service){this.service=service;}
    @GetMapping public List<Wish> mine(){return service.mine();}
    @PostMapping public Wish create(@Valid @RequestBody WishDtos.CreateWishRequest r){return service.create(r);}
    @PutMapping("/{id}") public Wish update(@PathVariable Long id,@Valid @RequestBody WishDtos.UpdateWishRequest r){return service.update(id,r);}
    @DeleteMapping("/{id}") public void archive(@PathVariable Long id){service.archive(id);}
}
