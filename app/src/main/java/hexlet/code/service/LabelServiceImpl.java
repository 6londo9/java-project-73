package hexlet.code.service;

import hexlet.code.dto.LabelDto;
import hexlet.code.exception.LabelException;
import hexlet.code.model.Label;
import hexlet.code.repository.LabelRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class LabelServiceImpl implements LabelService {

    private final LabelRepository labelRepository;

    @Override
    public List<Label> getAllLabels() {
        return labelRepository.findAll();
    }

    @Override
    public Label getLabel(Long id) {
        return labelRepository.findById(id)
                .orElseThrow(() -> new LabelException("Label with id: " + id + " is not found."));
    }

    @Override
    public Label createLabel(LabelDto labelDto) {
        Label label = new Label();
        label.setName(labelDto.getName());
        return labelRepository.save(label);
    }

    @Override
    public Label updateLabel(Long id, LabelDto labelDto) {
        Label label = labelRepository.findById(id)
                .orElseThrow(() -> new LabelException("Label with id: " + id + " is not found."));
        label.setName(labelDto.getName());
        return labelRepository.save(label);
    }

    @Override
    public void deleteLabel(Long id) {
        labelRepository.deleteById(id);
    }
}
