package com.cryptographydemo.securenotes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class SecurityFilter extends HttpFilter {

    @Value("${security.token}")
    private String expectedToken;

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String path = req.getRequestURI();
        if (path.startsWith("/notes")) {
            String auth = req.getHeader("Authorization");
            if (auth == null || !auth.equals(expectedToken)) {
                res.sendError(HttpStatus.UNAUTHORIZED.value(), "Unauthorized");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}