package hexlet.code.controller;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;
import hexlet.code.service.LabelService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping
    public List<Label> getAllLabels() {
        return labelService.getAllLabels();
    }

    @GetMapping(ID)
    public Label getLabel(@PathVariable Long id) {
        return labelService.getLabel(id);
    }

    @PostMapping
    public Label createLabel(
            @Valid
            @RequestBody
            LabelDto dto
    ) {
        return labelService.createLabel(dto);
    }

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

    @DeleteMapping(ID)
    public void deleteLabel(@PathVariable Long id) {
        labelService.deleteLabel(id);
    }
}
