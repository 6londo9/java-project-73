package hexlet.code.config;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;

@Component
@ComponentScan(basePackages = "hexlet.code")
@Profile(TEST_PROFILE)
@PropertySource(value = "classpath:/config/application.yml")
public class SpringConfigForIT {
    public static final String TEST_PROFILE = "test";

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplateBuilder().build();
    }
}
