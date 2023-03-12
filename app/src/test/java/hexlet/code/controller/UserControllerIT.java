package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.UserDto;
import hexlet.code.model.User;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringConfigForIT.class)
@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
public class UserControllerIT {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TestUtils testUtils;

    @AfterEach
    public void tearDown() {
        testUtils.tearDown();
    }

    @Test
    void testCreateUser() throws Exception {
        assertEquals(0, userRepository.count());
        testUtils.registerDefaultUser().andExpect(status().isOk());
        assertEquals(1, userRepository.count());
    }

    @Test
    void testGetUserById() throws Exception {
        assertEquals(0, userRepository.count());
        testUtils.registerDefaultUser();
        final User expectedUser = userRepository.findAll().get(0);

        final var response = testUtils.perform(get(USER_CONTROLLER_PATH + "/" + expectedUser.getId()))
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
        testUtils.registerDefaultUser();
        assertEquals(1, userRepository.count());

        UserDto user1 = UserDto
                .builder()
                .email("eugen@gmail.com")
                .firstName("Evgeny")
                .lastName("Novatsky")
                .password("enovatsky")
                .build();
        testUtils.registerUser(user1);
        assertEquals(2, userRepository.count());

        UserDto user2 = UserDto
                .builder()
                .email("vasya@gmail.com")
                .firstName("Vasilii")
                .lastName("Kubitsky")
                .password("vasilkube")
                .build();
        testUtils.registerUser(user1);
        assertEquals(3, userRepository.count());

        final var response = testUtils.perform(get(USER_CONTROLLER_PATH))
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
        testUtils.registerUser(currentUserDto);

        User currentUser = userRepository.findAll().get(0);
        final var newUserDto = UserDto
                .builder()
                .email("new@gmail.com")
                .firstName("Newfn")
                .lastName("Newln")
                .password("newpassword")
                .build();

        testUtils.perform(
                put(USER_CONTROLLER_PATH + "/" + currentUser.getId())
                .content(asJson(newUserDto))
                .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk());

        assertTrue(userRepository.existsById(currentUser.getId()));
        assertNotNull(userRepository.findByEmail("new@gmail.com"));
        assertNull(userRepository.findByEmail("vasya@gmail.com"));
    }

    @Test
    void testDeleteUser() throws Exception {
        testUtils.registerDefaultUser();
        assertEquals(1, userRepository.count());

        final var user = userRepository.findAll().get(0);

        testUtils.perform(delete(USER_CONTROLLER_PATH + "/" + user.getId())).andExpect(status().isOk());
        assertEquals(0, userRepository.count());
    }
}
