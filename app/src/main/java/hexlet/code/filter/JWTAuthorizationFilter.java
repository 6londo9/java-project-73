package hexlet.code.filter;

import hexlet.code.config.security.JWTHelper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;

import static hexlet.code.config.security.SecurityConfig.DEFAULT_AUTHORITIES;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY;

public class JWTAuthorizationFilter extends OncePerRequestFilter {

    private static final String BEARER = "Bearer";
    private final RequestMatcher publicUrls;
    private final JWTHelper jwtHelper;
    public JWTAuthorizationFilter(final RequestMatcher requestMatcher,
                                  final JWTHelper jwtHelper) {
        this.publicUrls = requestMatcher;
        this.jwtHelper = jwtHelper;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        return publicUrls.matches(request);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        final var authToken = Optional.ofNullable(request.getHeader(AUTHORIZATION))
                .map(header -> header.replaceFirst("^" + BEARER, ""))
                .map(String::trim)
                .map(jwtHelper::verify)
                .map(claims -> claims.get(SPRING_SECURITY_FORM_USERNAME_KEY))
                .map(Object::toString)
                .map(this::buildAuthToken)
                .orElseThrow();

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken buildAuthToken(String username) {
        return new UsernamePasswordAuthenticationToken(
                username,
                null,
                DEFAULT_AUTHORITIES
        );
    }
}
