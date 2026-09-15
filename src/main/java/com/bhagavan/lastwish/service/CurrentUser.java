package com.bhagavan.lastwish.service;

import com.bhagavan.lastwish.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUser {
    public User get() {
        Object p = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return (User)p;
    }
}
