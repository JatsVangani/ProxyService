package com.practo.commons.security;

import java.util.Collections;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.practo.commons.security.authentication.SignatureAuthenticationProvider;
import com.practo.commons.security.config.SecureProperties;
import com.practo.commons.security.config.SecureProperties.ServiceCredential;
import com.practo.commons.security.util.SecurityConstants.AuthHeader;

/**
 * Service Credential User Details Service is used by the {@link SignatureAuthenticationProvider} to
 * load the external service as a user. This class can be sub-classed to add additional checks or to
 * add authorities to each external service's UserDetails object.
 */
public class ServiceCredentialUserDetailsService implements UserDetailsService {

  private SecureProperties properties;

  /**
   * Constructor.
   *
   * @param properties
   */
  public ServiceCredentialUserDetailsService(SecureProperties properties) {
    this.properties = properties;
  }

  @Override
  public UserDetails loadUserByUsername(String serviceName) throws UsernameNotFoundException {
    if (serviceName == null) {
      throw new UsernameNotFoundException("Missing or empty " + AuthHeader.SERVICE + " header");
    }

    ServiceCredential credential = properties.getServiceCredential(serviceName);
    if (credential == null) {
      throw new UsernameNotFoundException("Invalid or unregistered service " + serviceName);
    }

    return createUser(serviceName, credential);
  }

  /**
   * Creates a new UserDetails object with the service name as the username and hmac secret as the
   * password. Granted Authorities are set to an empty list. This method can be overriden to create
   * a different UserDetails object, add additional checks or to provide granted-authorities which
   * will eventually be set into the {@link Authentication} object by the
   * {@link SignatureAuthenticationProvider}.
   *
   * @param serviceName
   * @param credential
   * @return
   */
  protected UserDetails createUser(String serviceName, ServiceCredential credential) {
    return new User(serviceName, credential.getSecret(), Collections.emptySet());
  }
}
