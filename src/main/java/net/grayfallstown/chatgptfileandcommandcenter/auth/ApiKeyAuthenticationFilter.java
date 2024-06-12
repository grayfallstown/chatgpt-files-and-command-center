package net.grayfallstown.chatgptfileandcommandcenter.auth;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {

    private AuthenticationManager authenticationManager;

    @Autowired
    @Lazy
    public void setAuthenticationManager(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws jakarta.servlet.ServletException, java.io.IOException {
        String uri = request.getRequestURI();
        if (StringUtils.startsWithAny(uri, "/v3/api-docs", "/swagger-ui", "/favicon.ico")) {
            filterChain.doFilter(request, response);
            return;
        }
        String apiKey = extractApiKeyFromRequest(request);

        if (apiKey != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(apiKey, null);
            authRequest.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authenticationManager.authenticate(authRequest));
        }

        filterChain.doFilter(request, response);
    }

    private String extractApiKeyFromRequest(HttpServletRequest request) {
        return request.getParameter("projectID");
    }

}
