package com.ynm.searchservice.config;

import com.ynm.searchservice.service.JWTService;
import com.ynm.searchservice.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


/**
 * Servlet filter that runs once per request to handle JWT-based authentication.
 * <p>
 * Responsibilities:
 * - Skips authentication for configured public endpoints.
 * - Extracts and validates a Bearer token from the Authorization header.
 * - Loads user details and populates the Spring SecurityContext on success.
 * - Returns 401 Unauthorized with a JSON error body on failures (missing/invalid/expired token).
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JWTService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    /**
     * Determines whether the current request path should bypass JWT processing.
     *
     * This method normalizes the request path by removing the servlet context path (if present),
     * then checks the resulting path against a list of publicly accessible endpoints.
     *
     * @param requestPath  the raw request URI (e.g., HttpServletRequest#getRequestURI())
     * @param contextPath  the servlet context path (e.g., HttpServletRequest#getContextPath())
     * @return true if the request should bypass JWT checks and continue the filter chain; false otherwise
     */
    private boolean shouldSkipJwtProcessing(String requestPath, String contextPath) {
        // Remove context path if present
        String path = requestPath;
        if (StringUtils.isNotEmpty(contextPath) && path.startsWith(contextPath)) {
            path = path.substring(contextPath.length());
        }

        // List of paths that should skip JWT processing
        return path.startsWith("/api/auth/login") ||
                path.equals("/api/auth/register") ||
                path.equals("/api/auth/health") ||
                path.startsWith("/api/user/ping") ||
                path.startsWith("/swagger-ui/") ||
                path.startsWith("/v3/api-docs") ||
                path.startsWith("/v3/api-docs/") ||
                path.startsWith("/users/sync") ||
                path.equals("/swagger-ui-testing.html") ||
                path.equals("/swagger-ui.html") ||
                path.equals("/auth/.well-known/jwks.json") ||
                path.equals("/v3/api-docs.yaml") ||
                path.startsWith("/")
                ;
    }


    /**
     * Core filter logic that attempts to authenticate the incoming request using a JWT.
     * <p>
     * Flow:
     * - If the request targets a public endpoint, the filter chain proceeds without authentication.
     * - Otherwise, the Authorization header is validated for a Bearer token.
     * - The token is parsed to extract the username; user details are loaded and the token is validated.
     * - On success, an authenticated UsernamePasswordAuthenticationToken is placed in the SecurityContext.
     * - On failure, the response is short-circuited with HTTP 401 and a JSON error message.
     *
     * @param request     the current HTTP request
     * @param response    the current HTTP response
     * @param filterChain the filter chain to delegate to
     * @throws ServletException if an error occurs within the servlet filter chain
     * @throws IOException      if an I/O error occurs while writing the response or delegating the filter chain
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userName;

        String urlStart = request.getRequestURI();

        log.debug("Processing request: {} with auth header: {}", urlStart, authHeader != null ? "present" : "missing");

        if (shouldSkipJwtProcessing(urlStart, request.getContextPath())) {
            log.debug("Skipping JWT processing for path: {}", urlStart);
            filterChain.doFilter(request, response);
            return;
        }

        if (StringUtils.isEmpty(authHeader) || !StringUtils.startsWith(authHeader, "Bearer")){
            log.warn("Missing or invalid Authorization header for path: {}", urlStart);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Missing or invalid Authorization header\"}");
            return;
        }

        try {
            jwt = authHeader.substring(7);
            userName = jwtService.extractUserName(jwt);
            log.debug("Extracted username from token: {}", userName);

            if (StringUtils.isNotEmpty(userName) && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                log.debug("Loaded user details for: {}", userName);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    log.debug("Token is valid for user: {}", userName);
                    SecurityContext securityContext = SecurityContextHolder.createEmptyContext();

                    UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

                    token.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    securityContext.setAuthentication(token);
                    SecurityContextHolder.setContext(securityContext);
                    log.debug("Authentication set for user: {}", userName);
                } else {
                    log.warn("Invalid or expired token for user: {}", userName);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("{\"error\":\"Invalid or expired token\"}");
                    return;
                }
            }
        } catch (Exception e) {
            log.error("Token processing failed for path: {} - Error: {}", urlStart, e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"Token processing failed: " + e.getMessage() + "\"}");
            return;
        }

        filterChain.doFilter(request, response);
    }
}