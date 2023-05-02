package hexlet.code.component;

import hexlet.code.filter.JWTAuthenticationFilter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.util.matcher.RequestMatcher;

public class JWTHttpConfigurer extends AbstractHttpConfigurer<JWTHttpConfigurer, HttpSecurity> {
    private final JWTHelper jwtHelper;
    private final RequestMatcher loginRequest;

    public JWTHttpConfigurer(JWTHelper jwtHelper, RequestMatcher loginRequest) {
        this.jwtHelper = jwtHelper;
        this.loginRequest = loginRequest;
    }

    @Override
    public void configure(HttpSecurity http) {
        final AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        http.addFilter(new JWTAuthenticationFilter(authenticationManager, loginRequest, jwtHelper));
    }
}
