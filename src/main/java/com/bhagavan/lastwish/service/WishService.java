package com.bhagavan.lastwish.service;

import com.bhagavan.lastwish.dto.WishDtos;
import com.bhagavan.lastwish.model.*;
import com.bhagavan.lastwish.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class WishService {
    private final WishRepository wishes; private final AccessLogRepository logs; private final CurrentUser current;
    public WishService(WishRepository wishes, AccessLogRepository logs, CurrentUser current) {
        this.wishes=wishes; this.logs=logs; this.current=current;
    }
    public List<Wish> mine(){ return wishes.findByOwnerIdOrderByCreatedAtDesc(current.get().getId()); }
    @Transactional public Wish create(WishDtos.CreateWishRequest r){
        Wish w=new Wish(); w.setOwner(current.get()); w.setTitle(r.title()); w.setContent(r.content());
        Wish saved=wishes.save(w); log("CREATE_WISH", saved.getId(), "Wish created"); return saved;
    }
    @Transactional public Wish update(Long id, WishDtos.UpdateWishRequest r){
        Wish w=owned(id); w.setTitle(r.title()); w.setContent(r.content()); w.setUpdatedAt(java.time.LocalDateTime.now());
        log("UPDATE_WISH", id, "Wish updated"); return wishes.save(w);
    }
    @Transactional public void archive(Long id){ Wish w=owned(id); w.setStatus(Enums.WishStatus.ARCHIVED); wishes.save(w); log("ARCHIVE_WISH",id,"Wish archived"); }
    public Wish owned(Long id){ return wishes.findByIdAndOwnerId(id,current.get().getId()).orElseThrow(()->new IllegalArgumentException("Wish not found")); }
    private void log(String action, Long wishId, String details){ AccessLog l=new AccessLog(); l.setActor(current.get()); l.setWishId(wishId); l.setAction(action); l.setDetails(details); logs.save(l); }
}
