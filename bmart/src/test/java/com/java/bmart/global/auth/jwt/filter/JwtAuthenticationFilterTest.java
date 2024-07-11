package com.java.bmart.global.auth.jwt.filter;

import com.java.bmart.global.jwt.JavaJwtTokenProvider;
import com.java.bmart.global.jwt.JwtAuthenticationProvider;
import com.java.bmart.global.jwt.TokenProvider;
import com.java.bmart.global.jwt.filter.JwtAuthenticationFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;

import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

public class JwtAuthenticationFilterTest {
    JwtAuthenticationFilter jwtAuthenticationFilter;
    TokenProvider tokenProvider;
    JwtAuthenticationProvider jwtAuthenticationProvider;

    @BeforeEach
    void setUP() {
        tokenProvider = new JavaJwtTokenProvider(
                "issuer",
                "clientSecret",
                60
        );
        jwtAuthenticationProvider = new JwtAuthenticationProvider(tokenProvider);
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtAuthenticationProvider);
    }

    @Nested
    @DisplayName("doFilterInternal 메서드 실행 시")
    class DoFilterInternalTest {

        @Test
        @DisplayName("성공")
        void success() throws ServletException, IOException {
            //given
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            FilterChain filterChain = mock(FilterChain.class);

            //when
            jwtAuthenticationFilter.doFilter(request, response, filterChain);

            //then
            then(filterChain).should().doFilter(request, response);
        }
    }
}
