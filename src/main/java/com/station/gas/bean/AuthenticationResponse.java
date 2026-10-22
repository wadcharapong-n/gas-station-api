package com.station.gas.bean;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AuthenticationResponse {
    private String token;

    public AuthenticationResponse(String jwtToken) {
        this.token = jwtToken;
    }
}
