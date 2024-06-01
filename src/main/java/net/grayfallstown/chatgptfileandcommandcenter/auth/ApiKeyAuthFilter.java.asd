/*
package net.grayfallstown.chatgptfileandcommandcenter.auth;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import java.io.IOException;

public class ApiKeyAuthFilter extends AbstractPreAuthenticatedProcessingFilter {

    private final ProjectManager projectManager;

    public ApiKeyAuthFilter(ProjectManager projectManager, AuthenticationManager authenticationManager) {
        this.projectManager = projectManager;
        setAuthenticationManager(authenticationManager);
    }

    @Override
    protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.matches(".*/files/.*") || path.matches(".*/git/.*")) {
            String apiKey = request.getHeader("API-Key");
            if (apiKey != null) {
                return apiKey;
            }
        }
        return null;
    }

    @Override
    protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.matches(".*/files/.*") || path.matches(".*/git/.*")) {
            return "N/A";
        }
        return null;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String path = httpRequest.getRequestURI();
        if (path.matches("/api/**")) {
            super.doFilter(httpRequest, httpResponse, chain);
        } else {
            chain.doFilter(request, response);
        }
    }
}
*/