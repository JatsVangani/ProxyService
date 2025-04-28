package com.practo.proxy.service;

import com.practo.proxy.security.ProxyUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

  @Autowired
  private ProxyUserDetailsService proxyUserDetailsService;

  /**
   * Loads user details by username.
   *
   * @param username the username to load
   * @return the user details
   * @throws UsernameNotFoundException if the user is not found
   */
  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return proxyUserDetailsService.loadUserByUsername(username);
  }
} 