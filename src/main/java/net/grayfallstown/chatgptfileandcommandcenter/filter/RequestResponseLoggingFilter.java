package net.grayfallstown.chatgptfileandcommandcenter.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class RequestResponseLoggingFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);

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
        logger.info("Request URL: {} {}", httpRequest.getMethod(), httpRequest.getRequestURI());
        httpRequest.getParameterMap().forEach((key, value) -> logger.info("Parameter: {}={}", key, String.join(",", value)));

        // Proceed with the next filter in the chain
        chain.doFilter(request, response);

        // Log response status
        logger.info("Response Status: {}", httpResponse.getStatus());
    }

    @Override
    public void destroy() {
        // Cleanup code if needed
    }
}
