package com.I_Mobile_Crazy.I_Mobile_Crazy.service.security;

import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.User;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.UserRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.util.enums.Role;
import com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.request.LoginRequest;
import com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.request.RegisterRequest;
import com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.respond.AuthenticationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    //User registration
    public String register(RegisterRequest request) {
        log.info("register {} " + request);

        var user = User.builder()
                .name(request.getName())
                .username(request.getUsername())
                .contact_number(request.getContact_number())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        userRepository.save(user);

        var jwtToken = jwtService.generateToken(user);
        return jwtToken;
    }

    //Authenticate user
    public AuthenticationResponse authenticate(LoginRequest request) {
        log.info("authenticate {} " + request);

        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));
        var user = userRepository.findByUsername(request.getUsername()).orElseThrow(()-> {
            log.error("User not found");
            return new UsernameNotFoundException("User not found");
        });
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
