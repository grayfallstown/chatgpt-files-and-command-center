package net.grayfallstown.chatgptfileandcommandcenter.auth;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;

public class ApiKeyAuthToken extends AbstractAuthenticationToken {
    private final ProjectConfig principal;
    private String credentials;

    public ApiKeyAuthToken(ProjectConfig principal) {
        super(null);
        this.principal = principal;
        this.credentials = null;
        setAuthenticated(false);
    }

    public ApiKeyAuthToken(ProjectConfig principal, String credentials,
            Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = principal;
        this.credentials = credentials;
        setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return credentials;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((principal == null) ? 0 : principal.hashCode());
        result = prime * result + ((credentials == null) ? 0 : credentials.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ApiKeyAuthToken other = (ApiKeyAuthToken) obj;
        if (principal == null) {
            if (other.principal != null)
                return false;
        } else if (!principal.equals(other.principal))
            return false;
        if (credentials == null) {
            if (other.credentials != null)
                return false;
        } else if (!credentials.equals(other.credentials))
            return false;
        return true;
    }

}
