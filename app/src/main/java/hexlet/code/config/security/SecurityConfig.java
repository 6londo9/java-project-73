package hexlet.code.config.security;

import hexlet.code.component.JWTHelper;
import hexlet.code.component.JWTHttpConfigurer;
import hexlet.code.filter.JWTAuthorizationFilter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;

import static hexlet.code.controller.TaskController.TASKS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String LOGIN = "/login";
    private final RequestMatcher publicUrls;
    private final RequestMatcher loginRequest;
    private final JWTHelper jwtHelper;
    public static final List<GrantedAuthority> DEFAULT_AUTHORITIES = List.of(new SimpleGrantedAuthority("USER"));

    public SecurityConfig(@Value("${base-url}") final String baseUrl,
                          final JWTHelper jwtHelper) {
        this.loginRequest = new AntPathRequestMatcher(baseUrl + LOGIN, POST.toString());
        this.publicUrls = new OrRequestMatcher(
                loginRequest,
                new AntPathRequestMatcher(baseUrl + USER_CONTROLLER_PATH, POST.toString()),
                new AntPathRequestMatcher(baseUrl + USER_CONTROLLER_PATH, GET.toString()),
                new AntPathRequestMatcher(baseUrl + TASK_STATUS_CONTROLLER_PATH, GET.toString()),
                new AntPathRequestMatcher(baseUrl + TASK_STATUS_CONTROLLER_PATH + "/**", GET.toString()),
                new AntPathRequestMatcher(baseUrl + TASKS_CONTROLLER_PATH, GET.toString()),
                new AntPathRequestMatcher(baseUrl + TASKS_CONTROLLER_PATH + "/**", GET.toString()),
                new NegatedRequestMatcher(new AntPathRequestMatcher(baseUrl + "/**"))
        );
        this.jwtHelper = jwtHelper;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        final var authorizationFilter = new JWTAuthorizationFilter(
                publicUrls,
                jwtHelper
        );

        http
                .csrf().disable()
                .authorizeHttpRequests(auth -> {
                            auth.requestMatchers(publicUrls).permitAll();
                            auth.anyRequest().authenticated();
                })
                .apply(new JWTHttpConfigurer(jwtHelper, loginRequest))
                .and()
                .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable();

        return http.build();
    }
}
