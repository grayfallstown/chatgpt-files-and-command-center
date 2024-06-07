package net.grayfallstown.chatgptfileandcommandcenter.auth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        private final ApiKeyAuthenticationProvider apiKeyAuthenticationProvider;
        private final ApiKeyAuthenticationFilter apiKeyAuthenticationFilter;

        public SecurityConfig(ApiKeyAuthenticationProvider apiKeyAuthenticationProvider,
                        ApiKeyAuthenticationFilter apiKeyAuthenticationFilter) {
                this.apiKeyAuthenticationProvider = apiKeyAuthenticationProvider;
                this.apiKeyAuthenticationFilter = apiKeyAuthenticationFilter;
        }

        @Bean
        public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
                http.csrf(AbstractHttpConfigurer::disable)
                                .authorizeHttpRequests(
                                                authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                                                                .requestMatchers("/v3/api-docs/**", "/swagger-ui/**",
                                                                                "/swagger-ui.html", "/favicon.ico")
                                                                .permitAll()
                                                                .anyRequest().authenticated())
                                .authenticationProvider(apiKeyAuthenticationProvider)
                                .addFilterBefore(apiKeyAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                                .sessionManagement(
                                                httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                                                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                                .httpBasic(Customizer.withDefaults());

                return http.build();
        }

}
