package hexlet.code.controller;

import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @GetMapping
    public List<User> getUsers() {
        return userService.getAllUsers();
    }

    @GetMapping(ID)
    public User getUser(
            @PathVariable
            Long id
    ) {
        return userService.getCurrentUser(id);
    }

    @PostMapping
    public User createUser(
            @RequestBody
            @Valid
            UserDto dto
    ) {
        return userService.createUser(dto);
    }

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

    @DeleteMapping(ID)
    @PreAuthorize(ONLY_OWNER_BY_ID)
    public void deleteUser(
            @PathVariable
            Long id
    ) {
        userService.deleteUser(id);
    }
}
