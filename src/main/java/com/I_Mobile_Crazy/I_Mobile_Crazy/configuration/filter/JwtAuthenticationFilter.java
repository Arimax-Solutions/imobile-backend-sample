package com.I_Mobile_Crazy.I_Mobile_Crazy.configuration.filter;

import com.I_Mobile_Crazy.I_Mobile_Crazy.service.security.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        try {
            if (header != null && header.startsWith("Bearer ")) {
                jwt = header.substring(7);
                username = jwtService.extractUsername(jwt);
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.validateToken(jwt, userDetails)) {
                    Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("User '{}' successfully authenticated", username);
                } else {
                    log.warn("Invalid JWT token for user '{}'", username);

                }
            }
        } catch (JwtException e) {
            log.error("JWT validation error: {}", e.getMessage());
//            sendUnauthorizedError(response);

        } catch (Exception e) {
            log.error("Unexpected error during authentication: {}", e.getMessage());
//            sendInternalServerError(response);
        }

        filterChain.doFilter(request, response);
        log.info("Request processed");
    }
}
