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
import net.grayfallstown.chatgptfileandcommandcenter.project.UnknownProjectException;

@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {

    private final ProjectManager projectManager;

    public ApiKeyAuthenticationProvider(ProjectManager projectManager) {
        this.projectManager = projectManager;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String apiKey = (String) authentication.getPrincipal();
        try {
            ProjectConfig project = projectManager.getProjectConfig(apiKey);
            return new ApiKeyAuthToken(project, apiKey, new ArrayList<>());
        } catch (UnknownProjectException e) {
            throw new BadCredentialsException("Invalid API Key");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
