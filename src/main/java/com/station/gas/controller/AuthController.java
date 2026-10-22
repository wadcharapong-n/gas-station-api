package com.station.gas.controller;

import com.station.gas.bean.AuthenticationRequest;
import com.station.gas.bean.AuthenticationResponse;
import com.station.gas.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        var user = userDetailsService.loadUserByUsername(request.getEmail());
        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPassword());
        if(matches) {
            var jwtToken = jwtUtils.generateToken(user);
            return ResponseEntity.ok(new AuthenticationResponse(jwtToken));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

}
