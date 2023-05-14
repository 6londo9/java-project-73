package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;

@AllArgsConstructor
@RestController
@RequestMapping("${base-url}" + USER_CONTROLLER_PATH)
public class UserController {
    public static final String USER_CONTROLLER_PATH = "/users";
    public static final String ID = "/{id}";
    private static final String ONLY_OWNER_BY_ID = """
            @userRepository.findById(#id).get().getEmail() == authentication.getName()
        """;
    private final UserService userService;
    private final UserRepository userRepository;

    @Operation(summary = "Get all users")
    @ApiResponse(responseCode = "200",
                 content = @Content(schema = @Schema(implementation = User.class)),
                 description = "Users showed")
    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Get user by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(schema = @Schema(implementation = User.class)),
                         description = "User was found"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),

            @ApiResponse(responseCode = "404",
                         content = @Content,
                         description = "User with such id is not found")
    })
    @GetMapping(ID)
    public User getUser(
            @PathVariable
            Long id
    ) {
        return userService.getUserById(id);
    }

    @Operation(summary = "Create new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                         content = @Content(schema = @Schema(implementation = User.class)),
                         description = "User was successfully created"),

            @ApiResponse(responseCode = "422",
                         content = @Content,
                         description = "Invalid data")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public User createUser(
            @RequestBody
            @Valid
            UserDto dto
    ) {
        return userService.createUser(dto);
    }

    @Operation(summary = "Update existing task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(schema = @Schema(implementation = User.class)),
                         description = "User was successfully updated"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),

            @ApiResponse(responseCode = "403",
                         content = @Content,
                         description = "Not enough rights to update user"),

            @ApiResponse(responseCode = "404",
                         content = @Content,
                         description = "User with such id is not found"),

            @ApiResponse(responseCode = "422",
                         content = @Content,
                         description = "Invalid data")
    })
    @PutMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public User updateUser(
            @PathVariable
            Long id,
            @RequestBody
            UserDto dto
    ) {
        return userService.updateUser(id, dto);
    }

    @Operation(summary = "Delete user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content,
                         description = "User was successfully deleted"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),

            @ApiResponse(responseCode = "403",
                         content = @Content,
                         description = "Not enough enough rights to delete user"),

            @ApiResponse(responseCode = "404",
                         content = @Content,
                         description = "User with such id is not found")
    })
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteUser(
            @PathVariable
            Long id
    ) {
        userService.deleteUser(id);
    }
}
