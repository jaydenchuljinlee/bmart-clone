package com.java.bmart.global.jwt;

import com.java.bmart.global.jwt.dto.Claims;
import com.java.bmart.global.jwt.dto.CreateTokenCommand;

public interface TokenProvider {
    String createToken(final CreateTokenCommand createTokenCommand);

    Claims validateToken(final String accessToken);
}
