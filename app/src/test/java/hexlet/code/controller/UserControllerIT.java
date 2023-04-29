package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.UserDto;
import hexlet.code.exception.UserException;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static hexlet.code.utils.TestUtils.USER_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringConfigForIT.class)
@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
public class UserControllerIT {
// TODO: Make tests for negative results
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestUtils utils;
    @BeforeEach
    public void beforeEach() throws Exception {
        utils.registerDefaultUser();
    }

    @AfterEach
    public void tearDown() {
        utils.tearDown();
    }

    @Test
    void testCreateUser() throws Exception {
        assertEquals(1, userRepository.count());
        final var newUser = UserDto.builder()
                        .firstName("Sergey")
                        .lastName("Vinokurov")
                        .email("vinok@gmail.com")
                        .password("password")
                .build();
        utils.registerUser(newUser).andExpect(status().isOk());
        assertEquals(2, userRepository.count());
    }

    @Test
    @Disabled(value = "Try found needed status")
    void testExistedUser() throws Exception {
        assertEquals(1, userRepository.count());
        final var existedUser = utils.getDefaultUser();
        utils.registerUser(existedUser).andExpect(status().isConflict());
    }

    @Test
    void testGetUserById() throws Exception {
        assertEquals(1, userRepository.count());
        final User expectedUser = userRepository.findAll().get(0);

        final var response = utils.perform(
                get(USER_CONTROLLER_PATH + ID, expectedUser.getId()),
                        USER_EMAIL)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final User actualUser = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedUser.getId(), actualUser.getId());
        assertEquals(expectedUser.getFirstName(), actualUser.getFirstName());
        assertEquals(expectedUser.getLastName(), actualUser.getLastName());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
    }

    @Test
    @Disabled(value = "Fix no such element exception")
    void getUserWithUnknownId() throws Exception {
        utils.perform(
                get(USER_CONTROLLER_PATH + "/10000", USER_EMAIL)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void testGetUsers() throws Exception {
        assertEquals(1, userRepository.count());

        UserDto user1 = UserDto
                .builder()
                .email("eugen@gmail.com")
                .firstName("Evgeny")
                .lastName("Novatsky")
                .password("enovatsky")
                .build();
        utils.registerUser(user1);
        assertEquals(2, userRepository.count());

        UserDto user2 = UserDto
                .builder()
                .email("vasya@gmail.com")
                .firstName("Vasilii")
                .lastName("Kubitsky")
                .password("vasilkube")
                .build();
        utils.registerUser(user2);
        assertEquals(3, userRepository.count());

        final var response = utils.perform(get(USER_CONTROLLER_PATH))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        assertThat(response.getContentAsString()).contains("vasya@gmail.com");
        assertThat(response.getContentAsString()).contains("Vasilii");
        assertThat(response.getContentAsString()).contains("Kubitsky");
        assertThat(response.getContentAsString()).doesNotContain("vasilkube");

        assertThat(response.getContentAsString()).contains("eugen@gmail.com");
        assertThat(response.getContentAsString()).contains("Evgeny");
        assertThat(response.getContentAsString()).contains("Novatsky");
        assertThat(response.getContentAsString()).doesNotContain("enovatsky");
    }

    @Test
    void testUpdateUser() throws Exception {
        final var currentUserDto = UserDto
                .builder()
                .email("vasya@gmail.com")
                .firstName("Vasilii")
                .lastName("Kubitsky")
                .password("vasilkube")
                .build();
        utils.registerUser(currentUserDto);

        User currentUser = userRepository.findByEmail(currentUserDto.getEmail())
                .orElseThrow(() -> new UserException("User with email: " + currentUserDto.getEmail() + " not found."));

        final var newUserDto = UserDto
                .builder()
                .email("new@gmail.com")
                .firstName("Newfn")
                .lastName("Newln")
                .password("newpassword")
                .build();

        utils.perform(
                put(USER_CONTROLLER_PATH + ID, currentUser.getId())
                .content(asJson(newUserDto))
                .contentType(MediaType.APPLICATION_JSON),
                currentUser.getEmail()
        ).andExpect(status().isOk());

        assertTrue(userRepository.existsById(currentUser.getId()));
        assertNotNull(userRepository.findByEmail("new@gmail.com"));
        assertNull(userRepository.findByEmail("vasya@gmail.com").orElse(null));
    }

    @Test
    void testDeleteUser() throws Exception {
        assertEquals(1, userRepository.count());

        final var user = userRepository.findAll().get(0);

        utils.perform(delete(USER_CONTROLLER_PATH + ID, user.getId()), user.getEmail())
                .andExpect(status().isOk());
        assertEquals(0, userRepository.count());
    }

    @Test
    void testDeleteWithoutRights() throws Exception {
        final var newUser = UserDto.builder()
                        .firstName("Max")
                        .lastName("Maximov")
                        .email("maxmax@gmail.com")
                        .password("max2000")
                        .build();
        utils.registerUser(newUser);
        final var user = userRepository.findByEmail(newUser.getEmail())
                        .orElseThrow(() -> new UserException("User with email: " + newUser.getEmail() + " not found."));
        utils.perform(delete(USER_CONTROLLER_PATH + ID, user.getId()), USER_EMAIL)
                .andExpect(status().isForbidden());
    }
}
