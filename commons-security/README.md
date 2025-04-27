# Commons Security

The go-to module for application security!
Commons Security provides implementations of Spring Boot Security that are suited for Practo.

## Features
- `SignatureAuthenticationFilter` - Filter for adding HMAC based Authentication to any URL.
- `VersionedAuthenticationFilter` - Version based delegating implementation of the `AbstractAuthenticationProcessingFilter` for migrating between different HMAC Auth Versions.
- `SignatureAuthenticationProvider` - HMAC Signature Based Authentication implementation of `AuthenticationProvider`
- `NonceManager` - A Nonce validation system with optional and mandatory compliance support.
- `NonceStorage` - An interface for various nonce store implementations like - `NoOpNonceStorage` and `CacheBasedNonceStorage`.
- `Details` - An interface for storing request-details in `Authentication` objects.
- `RequestDetails` - Default implementation of the `Details` interface.
- `DefaultAuthenticationDetailsSource` - Default implementation of Spring Security's `AuthenticationDetailsSource` interface to build a `Details` object for the request.

## Implementation
Add `commons-security` module as a dependency in your application's `pom.xml`

```xml    
<dependency>
    <groupId>com.practo.commons</groupId>
    <artifactId>commons-security</artifactId>
</dependency>
```

### Supported Properties:

```properties
# Time to Live in seconds for the Request Nonce.
practo.security.nonce-ttl = 10s

# The following properties use "foo" as an example client.
# Shared Secret for HMAC Digest.
practo.security.keys.foo.secret = a-secret-value

# OPTIONAL Nonce Compliance will validate the request with or without nonce.
# REQUIRED Nonce Compliance will reject the request if the nonce is absent.
practo.security.keys.foo.nonce-compliance = REQUIRED
```

Find out more about these properties here - [SecureProperties.java](https://github.com/practo/boot/blob/master/commons-security/src/main/java/com/practo/commons/security/config/SecureProperties.java)

The latest version can be found from the Practo's [M2 Repo](https://github.com/practo/m2/tree/master/releases/com/practo/commons/commons-security)

> Do not forget to scan the `com.practo.commons.security` package in your application's configuration class.

For more information on how to authenticate your requests with `commons-security`, please read [Application Security](https://github.com/practo/boot/wiki/Application-Security)
