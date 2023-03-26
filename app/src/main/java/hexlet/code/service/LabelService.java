package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.model.Label;

import java.util.List;

public interface LabelService {
    List<Label> getAllLabels();
    Label getLabel(Long id);
    Label createLabel(LabelDto labelDto);
    Label updateLabel(Long id, LabelDto labelDto);
    void deleteLabel(Long id);
}
