package com.gerenciamento.cursos.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class RequestIdFilter extends OncePerRequestFilter {
    public static final String HEADER = "X-Request-ID";
    public static final String MDC_KEY = "requestId";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String reqId = request.getHeader(HEADER);
        if (reqId == null || reqId.isEmpty()) {
            reqId = UUID.randomUUID().toString();
        }
        // put in MDC for logging
        MDC.put(MDC_KEY, reqId);
        // ensure it's on the response too
        response.setHeader(HEADER, reqId);
        try {
            filterChain.doFilter(request, response);
        } finally {
            MDC.remove(MDC_KEY);
        }
    }
}
