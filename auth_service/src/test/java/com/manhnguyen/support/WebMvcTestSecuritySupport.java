package com.manhnguyen.support;

import com.manhnguyen.config.SecurityConfig;
import com.manhnguyen.service.CustomUserDetailsService;
import com.manhnguyen.utils.JwtAuthFilter;
import com.manhnguyen.utils.JwtUtil;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

/**
 * Shared security setup for sliced MVC tests that exercise Spring Security rules.
 */
@Import({SecurityConfig.class, JwtAuthFilter.class})
public abstract class WebMvcTestSecuritySupport {

    @MockitoBean
    protected JwtUtil jwtUtil;

    @MockitoBean
    protected CustomUserDetailsService customUserDetailsService;
}
