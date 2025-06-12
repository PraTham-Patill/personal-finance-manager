package com.fintrack.personalfinancemanager.config;

import com.fintrack.personalfinancemanager.model.User;
import com.fintrack.personalfinancemanager.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * Custom authentication provider that uses our UserService to authenticate users.
 */
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserService userService;

    /**
     * Authenticates the user using the provided credentials.
     *
     * @param authentication the authentication request object
     * @return a fully authenticated object including credentials
     * @throws AuthenticationException if authentication fails
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String email = authentication.getName();
        String password = authentication.getCredentials().toString();
        
        try {
            User user = userService.authenticateUser(email, password);
            return new UsernamePasswordAuthenticationToken(
                user.getId(), // Use user ID as principal
                null, // Don't expose credentials
                Collections.singletonList(new SimpleGrantedAuthority("USER"))
            );
        } catch (IllegalArgumentException e) {
            throw new BadCredentialsException("Invalid email or password", e);
        }
    }

    /**
     * Returns true if this AuthenticationProvider supports the indicated Authentication object.
     *
     * @param authentication the authentication object to check
     * @return true if the implementation can process the authentication object
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}