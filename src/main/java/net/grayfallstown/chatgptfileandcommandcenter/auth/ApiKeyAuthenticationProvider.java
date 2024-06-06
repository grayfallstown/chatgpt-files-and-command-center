package net.grayfallstown.chatgptfileandcommandcenter.auth;

import java.util.ArrayList;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectConfig;
import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectManager;

@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    private final ProjectManager projectManager;

    public ApiKeyAuthenticationProvider(ProjectManager projectManager) {
        this.projectManager = projectManager;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String apiKey = (String) authentication.getPrincipal();
        ProjectConfig project = projectManager.getProjectConfig(apiKey);

        if (project == null) {
            throw new BadCredentialsException("Invalid API Key");
        }

        return new UsernamePasswordAuthenticationToken(project, apiKey, new ArrayList<>());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
