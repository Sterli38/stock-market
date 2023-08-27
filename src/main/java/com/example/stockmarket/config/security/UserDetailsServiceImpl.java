package com.example.stockmarket.config.security;

import com.example.stockmarket.controller.request.UserRequest;
import com.example.stockmarket.dao.database.UserDatabaseDao;
import com.example.stockmarket.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserDatabaseDao userDatabaseDao;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDatabaseDao.getUserByUsername(username);
        return new org.springframework.security.core.userdetails.User(username, user.getPassword(), user.isEnabled(), true, true, true, user.getAuthorities());
    }
}
