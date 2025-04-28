package com.practo.proxy.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class ProxyUserDetailsService implements UserDetailsService {

    /**
     * Loads user details by username.
     *
     * @param username the username to load
     * @return the user details
     * @throws UsernameNotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // In a real application, you would load the user from a database
        // For this example, we'll create a simple user with a fixed password
        return new User(
            username,
            "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG", // "password" encrypted
            Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );
    }
} 