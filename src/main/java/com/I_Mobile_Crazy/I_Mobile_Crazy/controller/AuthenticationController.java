package com.I_Mobile_Crazy.I_Mobile_Crazy.controller;

import com.I_Mobile_Crazy.I_Mobile_Crazy.entity.User;
import com.I_Mobile_Crazy.I_Mobile_Crazy.repo.UserRepository;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.custom.UserService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.service.security.AuthenticationService;
import com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.request.LoginRequest;
import com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.request.RegisterRequest;
import com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.respond.AuthenticationResponse;
import com.I_Mobile_Crazy.I_Mobile_Crazy.util.payload.respond.StandardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

/**
 * @author : Chanuka Weerakkody
 * @since : 20.1.1
 **/
@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin("*")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    //@PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<StandardResponse> register(@RequestBody RegisterRequest request) {
        log.info("register {} " + request);

        if (!userService.existsByUsername(request.getUsername())) {
            return ResponseEntity.ok(
                    StandardResponse.builder()
                            .message("User registered successfully")
                            .status(200)
                            .data(AuthenticationResponse.builder()
                                    .token(authenticationService
                                            .register(request))
                                    .build())
                            .build());
        }
        return ResponseEntity.ok(StandardResponse.builder()
                .message("User already exists.")
                .status(400)
                .build());
    }

    //Login
    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
        log.info("Login request: {}", request);
        AuthenticationResponse response = authenticationService.authenticate(request);
        Optional<User> userOptional = userRepository.findByUsername(request.getUsername());

        if (userOptional.isPresent()) {
            User authenticatedUser = userOptional.get();
            response.setAuthenticatedUser(authenticatedUser);
        } else {
            throw new RuntimeException("User not found for username: " + request.getUsername());
        }
        return ResponseEntity.ok(response);
    }

}
