package com.practo.commons.security;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.practo.commons.security.util.SecurityConstants.AuthHeader;

/**
 * There can be multiple ways a user can be loaded, for example, by an email or a phone number. This
 * class defines a way for multiple values to be passed in the {@link AuthHeader#USER} header. The
 * assumption is the indentifier will be of the format
 * </p>
 * <code>{type}:{value}</code>.
 * </p>
 * For example:
 * </p>
 * <code>email:user@gmail.com</code>
 * </p>
 * or
 * </p>
 * <code>accountid:12345</code>.
 * </p>
 * The user is expected to extend this class and register the user loading methods in the
 * {@link InitializingBean#afterPropertiesSet()} method.
 *
 * @author mayankdharwa
 *
 */
public abstract class PrefixedUserDetailsService implements UserDetailsService, InitializingBean {

  private Map<String, Function<String, UserDetails>> userDetailsFunctions = new HashMap<>();

  private Function<String, UserDetails> defaultUserDetailsFunction;

  @Override
  public UserDetails loadUserByUsername(String userIdentifierStr) throws UsernameNotFoundException {
    if (StringUtils.isBlank(userIdentifierStr)) {
      throw new UsernameNotFoundException("Username cannot be null or empty");
    }

    String[] splitTexts = userIdentifierStr.split(":");
    String type = splitTexts[0].toLowerCase();

    if (defaultUserDetailsFunction != null && splitTexts.length < 2) {
      return defaultUserDetailsFunction.apply(type);
    }

    String identifier = splitTexts[1];
    Function<String, UserDetails> function =
        userDetailsFunctions.getOrDefault(type, defaultUserDetailsFunction);

    // No mapping for type and default is null as well.
    if (function == null) {
      throw new UsernameNotFoundException("Invalid prefixed username format");
    }

    return function.apply(identifier);
  }

  /**
   * Add a {@link UserDetails} mapper function for a given type. Example: <code>register("email",
   * this::byEmail)</code>
   *
   * @param type
   * @param supplier
   */
  protected void register(String type, Function<String, UserDetails> function) {
    Objects.requireNonNull(type);
    userDetailsFunctions.put(type, function);
  }

  /**
   * When the type doesn't match or is missing, this function defines the default behaviour. If not
   * set, a {@link UsernameNotFoundException} will be thrown.
   *
   * @param defaultUserDetailsFunction
   */
  protected void registerDefault(Function<String, UserDetails> defaultUserDetailsFunction) {
    this.defaultUserDetailsFunction = defaultUserDetailsFunction;
  }
}
