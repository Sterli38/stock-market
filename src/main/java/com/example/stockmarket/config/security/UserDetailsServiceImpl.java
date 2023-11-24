package com.example.stockmarket.config.security;

import com.example.stockmarket.dao.database.ParticipantDatabaseDao;
import com.example.stockmarket.dao.repositories.ParticipantRepository;
import com.example.stockmarket.entity.Participant;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.parameters.P;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ParticipantRepository participantRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Participant participant = participantRepository.findByName(username);
        Set<GrantedAuthority> authorities = participant.getRoles().stream()
                .map(i -> new SimpleGrantedAuthority(i.getRoleName()))
                .collect(Collectors.toSet());
        return new org.springframework.security.core.userdetails.User(username, participant.getPassword(), participant.isEnabled(), true, true, true, authorities);
    }
}
