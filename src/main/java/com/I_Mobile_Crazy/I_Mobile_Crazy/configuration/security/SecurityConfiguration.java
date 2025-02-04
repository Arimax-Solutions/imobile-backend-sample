package com.I_Mobile_Crazy.I_Mobile_Crazy.configuration.security;

import com.I_Mobile_Crazy.I_Mobile_Crazy.configuration.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfiguration {
    private final AuthenticationProvider authenticationProvider;
    private  final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final AuthEntryPoint unauthorizedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("SecurityFilterChain {} ");

        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(
                        auth ->
                                auth.requestMatchers("/auth/**").permitAll()
                                        //.requestMatchers("/api/items/**").authenticated()
                                        //.requestMatchers("/api/customer/**").authenticated()
                                        //.requestMatchers("/api/dailyCost/**").authenticated()
                                        //.requestMatchers("/api/imei/**").authenticated()
                                        //.requestMatchers("/api/retailOrder/**").authenticated()
                                        //.requestMatchers("/api/returnItem/**").authenticated()
                                        //.requestMatchers("/api/returnOrder/**").authenticated()
                                        //.requestMatchers("/api/return/phone/**").authenticated()
                                        //.requestMatchers("/api/shop/**").authenticated()
                                        //.requestMatchers("/api/stock/**").authenticated()
                                        //.requestMatchers("/api/users/**").authenticated()
                                        //.requestMatchers("/api/wholesaleOrder/**").authenticated()
                                        .anyRequest().permitAll()
                )
                .httpBasic(Customizer.withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .exceptionHandling(exception -> exception.authenticationEntryPoint(unauthorizedHandler))
                .addFilterBefore(jwtAuthenticationFilter,UsernamePasswordAuthenticationFilter.class);
        return http.build();

    }
}
