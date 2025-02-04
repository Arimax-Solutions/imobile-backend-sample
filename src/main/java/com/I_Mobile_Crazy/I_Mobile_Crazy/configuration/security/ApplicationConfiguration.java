package com.I_Mobile_Crazy.I_Mobile_Crazy.configuration.security;

import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Configuration
@Slf4j
@RequiredArgsConstructor
public class ApplicationConfiguration {
    private final UserRepository userRepository;
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            log.info("userDetailsService () : Loading user by username");
            return userRepository.findByUsername(username).map(user -> {
                log.info("userDetailsService () : User found");
                return user;
            }).orElseThrow(() -> {
                log.error("userDetailsService () : User not found");
                return new UsernameNotFoundException("User not found");
            });
        };
    }



    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
