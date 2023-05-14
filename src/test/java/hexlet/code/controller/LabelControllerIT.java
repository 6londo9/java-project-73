package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static hexlet.code.utils.TestUtils.BASE_URL;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static hexlet.code.utils.TestUtils.USER_EMAIL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringConfigForIT.class)
@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
public class LabelControllerIT {

    @Autowired
    private TestUtils utils;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void beforeEach() throws Exception {
        utils.createDefaultLabel();
    }
    @AfterEach
    public void afterEach() {
        utils.tearDown();
    }

    @Test
    void testGetAllLabels() throws Exception {
        utils.createLabel(new LabelDto("Issue"));
        utils.createLabel(new LabelDto("Tests"));

        final var response = utils.perform(
                get(BASE_URL + LABEL_CONTROLLER_PATH),
                USER_EMAIL
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        assertEquals(3, labelRepository.count());
        assertThat(response.getContentAsString()).contains("Issue");
        assertThat(response.getContentAsString()).contains("Tests");
        assertThat(response.getContentAsString()).contains("Bug");
    }

    @Test
    void testGetLabelById() throws Exception {
        final var expectedLabel = labelRepository.findAll().get(0);
        final var response = utils.perform(
                get(BASE_URL + LABEL_CONTROLLER_PATH + ID, expectedLabel.getId()),
                USER_EMAIL
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label actualLabel = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedLabel.getId(), actualLabel.getId());
        assertEquals(expectedLabel.getName(), actualLabel.getName());
    }

    @Test
    void testCreateLabel() throws Exception {
        final var labelDto = new LabelDto("Issue");
        utils.perform(
                post(BASE_URL + LABEL_CONTROLLER_PATH)
                        .content(asJson(labelDto))
                        .contentType(APPLICATION_JSON),
                USER_EMAIL
        ).andExpect(status().isCreated());

        assertEquals(2, labelRepository.count());
    }

    @Test
    void testUpdateLabel() throws Exception {
        final var currentLabel = labelRepository.findAll().get(0);
        final var dto = new LabelDto("Testing");

        final var response = utils.perform(
                put(BASE_URL + LABEL_CONTROLLER_PATH + ID, currentLabel.getId())
                        .content(asJson(dto))
                        .contentType(APPLICATION_JSON),
                USER_EMAIL
        )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        final Label actualLabel = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertTrue(labelRepository.existsById(currentLabel.getId()));
        assertNull(labelRepository.findByName(currentLabel.getName()).orElse(null));
        assertNotNull(labelRepository.findByName(actualLabel.getName()));
        assertEquals(actualLabel.getName(), dto.getName());
    }

    @Test
    void testDeleteLabel() throws Exception {
        assertEquals(1, labelRepository.count());
        final var currentLabel = labelRepository.findAll().get(0);

        utils.perform(
                delete(BASE_URL + LABEL_CONTROLLER_PATH + ID, currentLabel.getId()),
                USER_EMAIL
        ).andExpect(status().isOk());
        assertEquals(0, labelRepository.count());
    }
}
