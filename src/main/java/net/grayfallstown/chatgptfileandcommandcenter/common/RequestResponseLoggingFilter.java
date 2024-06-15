package net.grayfallstown.chatgptfileandcommandcenter.common;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.grayfallstown.chatgptfileandcommandcenter.project.ProjectManager;

@Component
public class RequestResponseLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

    @Autowired
    private ProjectManager projectManager;

    private AtomicInteger counter = new AtomicInteger(0);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization code if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // Log request URL and parameters
        int id = counter.incrementAndGet();
        String sanitizedURL = obfuscateApiKeyInUrl(httpRequest.getRequestURI());

        logger.info("Request {} URL: {} {}", id, httpRequest.getMethod(), sanitizedURL);
        httpRequest.getParameterMap()
                .forEach((key, value) -> {
                    if (key.equals("projectID")) {
                        value = Arrays.asList(value).stream()
                                .map(this::obfuscateApiKeyParameter).collect(Collectors.toList())
                                .toArray(new String[0]);
                    }
                    logger.info("Parameter: {}={}", key, String.join(",", value));
                });

        // Proceed with the next filter in the chain
        chain.doFilter(request, response);

        // Log response status
        logger.info("Response {} Status: {}", id, httpResponse.getStatus());
    }

    private String obfuscateApiKeyParameter(String value) {
        if (value != null) {
            return value.charAt(0) + "<obfuscated>" + value.charAt(value.length() - 1);
        }
        return null;
    }

    public static String obfuscateApiKeyInUrl(String url) {
        String regex = "/api/([^/]+)/";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            String apiKey = matcher.group(1);
            if (apiKey.length() > 2) {
                String obfuscatedApiKey = apiKey.charAt(0) + "<obfuscated>" + apiKey.charAt(apiKey.length() - 1);
                return url.replaceFirst(regex, "/api/" + obfuscatedApiKey + "/");
            }
        }
        return url;
    }

}
