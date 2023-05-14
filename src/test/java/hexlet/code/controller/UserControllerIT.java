package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.utils.TestUtils.BASE_URL;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static hexlet.code.utils.TestUtils.USER_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringConfigForIT.class)
@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
public class UserControllerIT {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestUtils utils;

    private final UserDto userDto = UserDto
            .builder()
            .email("vasya@gmail.com")
            .firstName("Vasilii")
            .lastName("Kubitsky")
            .password("vasilkube")
            .build();

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
        utils.registerUser(userDto).andExpect(status().isCreated());
        assertEquals(2, userRepository.count());
    }

    @Test
    void testExistedUser() throws Exception {
        assertEquals(1, userRepository.count());
        final var existedUser = utils.getDefaultUser();
        utils.registerUser(existedUser).andExpect(status().isUnprocessableEntity());
    }

    @Test
    void testGetUserById() throws Exception {
        assertEquals(1, userRepository.count());
        final User expectedUser = userRepository.findAll().get(0);

        final var response = utils.perform(
                get(BASE_URL + USER_CONTROLLER_PATH + ID, expectedUser.getId()),
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
    void testGetUsers() throws Exception {
        assertEquals(1, userRepository.count());

        utils.registerUser(userDto);
        assertEquals(2, userRepository.count());

        UserDto user2 = UserDto
                .builder()
                .email("eugen@gmail.com")
                .firstName("Evgeny")
                .lastName("Novatsky")
                .password("enovatsky")
                .build();
        utils.registerUser(user2);
        assertEquals(3, userRepository.count());

        final var response = utils.perform(get(BASE_URL + USER_CONTROLLER_PATH))
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
        utils.registerUser(userDto);

        User currentUser = userRepository.findByEmail(userDto.getEmail()).get();

        final var newUserDto = UserDto
                .builder()
                .email("new@gmail.com")
                .firstName("Newfn")
                .lastName("Newln")
                .password("newpassword")
                .build();

        utils.perform(
                put(BASE_URL + USER_CONTROLLER_PATH + ID, currentUser.getId())
                .content(asJson(newUserDto))
                .contentType(APPLICATION_JSON),
                currentUser.getEmail()
        ).andExpect(status().isOk());

        assertTrue(userRepository.existsById(currentUser.getId()));
        assertNotNull(userRepository.findByEmail("new@gmail.com"));
        assertNull(userRepository.findByEmail("vasya@gmail.com").orElse(null));
    }
    @Test
    void testUpdateUserWithoutRights() throws Exception {
        utils.registerUser(userDto);
        User existingUser = userRepository.findByEmail(USER_EMAIL).get();

        final var newUserData = UserDto
                .builder()
                .email("newemail@gmail.com")
                .firstName("Newfname")
                .lastName("Newlname")
                .password("newpassword")
                .build();

        utils.perform(
                put(BASE_URL + USER_CONTROLLER_PATH + ID, existingUser.getId())
                        .content(asJson(newUserData))
                        .contentType(APPLICATION_JSON),
                userDto.getEmail()
        ).andExpect(status().isForbidden());

        final var expectedUser = userRepository.findByEmail(USER_EMAIL);
        assertNotEquals(userDto.getEmail(), expectedUser.get().getEmail());
        assertNotEquals(userDto.getFirstName(), expectedUser.get().getFirstName());
        assertNotEquals(userDto.getLastName(), expectedUser.get().getLastName());
    }

    @Test
    void testDeleteUser() throws Exception {
        assertEquals(1, userRepository.count());

        final var user = userRepository.findAll().get(0);

        utils.perform(delete(BASE_URL + USER_CONTROLLER_PATH + ID, user.getId()), user.getEmail())
                .andExpect(status().isOk());
        assertEquals(0, userRepository.count());
    }

    @Test
    void testDeleteWithoutRights() throws Exception {
        utils.registerUser(userDto);
        final var user = userRepository.findByEmail(userDto.getEmail()).get();
        utils.perform(delete(BASE_URL + USER_CONTROLLER_PATH + ID, user.getId()), USER_EMAIL)
                .andExpect(status().isForbidden());
    }
}
