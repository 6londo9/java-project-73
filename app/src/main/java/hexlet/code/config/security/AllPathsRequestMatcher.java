package hexlet.code.config.security;

import jakarta.servlet.http.HttpServletRequest;

public class AllPathsRequestMatcher implements org.springframework.security.web.util.matcher.RequestMatcher {
    @Override
    public boolean matches(HttpServletRequest request) {
        return true;
    }
}
