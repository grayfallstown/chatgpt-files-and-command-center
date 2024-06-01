/*
package net.grayfallstown.chatgptfileandcommandcenter.auth;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    private final String apiKey;

    public ApiKeyAuthenticationProvider(String apiKey) {
        this.apiKey = apiKey;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String principal = (String) authentication.getPrincipal();
        if (apiKey.equals(principal)) {
            return new ApiKeyAuthenticationToken(principal, true);
        }
        return null;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return PreAuthenticatedAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
 */