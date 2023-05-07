package hexlet.code.controller;

import com.rollbar.notifier.Rollbar;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.rollbar.spring.webmvc.RollbarSpringConfigBuilder.withAccessToken;

@RestController
public class WelcomeController {

    @Value("${ROLLBAR_TOKEN:}")
    private String rollbarToken;
    @Value("${spring.profiles.active:}")
    private String activeProfile;

    private Rollbar rollbar = Rollbar.init(
            withAccessToken(rollbarToken)
                    .environment(activeProfile)
                    .build()
    );

    void sendRollbarDebug() {
        rollbar.debug("Here is some debug message");
    }

    @Operation(summary = "Welcome page")
    @ApiResponse(responseCode = "200",
    description = "Welcome page successfully loaded")
    @GetMapping("/welcome")
    public String welcome() {
        sendRollbarDebug();
        return "Welcome to Spring";
    }
}
