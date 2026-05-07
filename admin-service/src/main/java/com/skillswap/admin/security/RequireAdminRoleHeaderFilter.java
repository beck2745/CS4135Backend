package com.skillswap.admin.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * When enabled (Docker/production), moderation APIs require {@code X-Authenticated-Role: ADMIN}
 * set by the API gateway from the JWT. Prevents trusting the gateway as the only authorisation layer.
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class RequireAdminRoleHeaderFilter extends OncePerRequestFilter {

    @Value("${sharecraft.security.require-admin-role-header:false}")
    private boolean requireAdminRoleHeader;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        // Health probes are anonymous (browser / Docker / load balancer); real admin routes stay protected.
        if (!requireAdminRoleHeader || !uri.startsWith("/api/admin") || "/api/admin/health".equals(uri)) {
            filterChain.doFilter(request, response);
            return;
        }
        String role = request.getHeader("X-Authenticated-Role");
        if (role == null || !"ADMIN".equalsIgnoreCase(role.trim())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Administrator role required");
            return;
        }
        filterChain.doFilter(request, response);
    }
}
