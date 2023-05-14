package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + LABEL_CONTROLLER_PATH)
public class LabelController {
    public static final String LABEL_CONTROLLER_PATH = "/labels";
    private final LabelService labelService;

    @Operation(summary = "Get all labels")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(schema = @Schema(implementation = Label.class)),
                         description = "Labels showed"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request")
    })
    @GetMapping
    public List<Label> getAllLabels() {
        return labelService.getAllLabels();
    }

    @Operation(summary = "Get label by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(schema = @Schema(implementation = Label.class)),
                         description = "Label was found"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),

            @ApiResponse(responseCode = "404",
                         content = @Content,
                         description = "Label with such id is not found")
    })
    @GetMapping(ID)
    public Label getLabel(@PathVariable Long id) {
        return labelService.getLabel(id);
    }

    @Operation(summary = "Create label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                         content = @Content(schema = @Schema(implementation = Label.class)),
                         description = "Label was successfully created"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),

            @ApiResponse(responseCode = "422",
                         content = @Content,
                         description = "Invalid data")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public Label createLabel(
            @Valid
            @RequestBody
            LabelDto dto
    ) {
        return labelService.createLabel(dto);
    }

    @Operation(summary = "Update existing label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(schema = @Schema(implementation = Label.class)),
                         description = "Label was successfully updated"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),

            @ApiResponse(responseCode = "404",
                         content = @Content,
                         description = "Label with such id is not found"),

            @ApiResponse(responseCode = "422",
                         content = @Content,
                         description = "Invalid data")
    })
    @PutMapping(ID)
    public Label updateLabel(
            @PathVariable
            Long id,
            @Valid
            @RequestBody
            LabelDto dto
    ) {
        return labelService.updateLabel(id, dto);
    }

    @Operation(summary = "Delete label")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content,
                         description = "Label was successfully deleted"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),

            @ApiResponse(responseCode = "404",
                         content = @Content,
                         description = "Label with such id is not found")
    })
    @DeleteMapping(ID)
    public void deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
    }
}
