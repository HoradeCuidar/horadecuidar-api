package com.hdc.hdc.orientacao_funcional.tag;

import com.hdc.hdc.util.exception.ResourceNotFoundException;
import com.hdc.hdc.util.exception.ResourceWithSameNameException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TagFuncionalService {

    private final TagFuncionalRepository repository;
    private final TagFuncionalMapper mapper;

    public TagFuncionalDTO save(TagFuncionalCreateDto dto) {
        validarUnicidade(dto.nome());
        TagFuncional entity = mapper.toEntity(dto);
        return mapper.toDTO(repository.save(entity));
    }

    @Transactional(readOnly = true)
    public TagFuncionalDTO getById(Long id) {
        return repository.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Tag Funcional", "Tag funcional com id " + id + " não encontrada."));
    }

    @Transactional(readOnly = true)
    public List<TagFuncionalDTO> getAll() {
        return repository.findAll().stream().map(mapper::toDTO).toList();
    }

    public void update(Long id, TagFuncionalCreateDto dto) {
        validarUnicidade(dto.nome(), id);
        TagFuncional existing = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tag Funcional", "Tag funcional com id " + id + " não encontrada."));
        existing.setNome(dto.nome());
        existing.setDescricao(dto.descricao());
        repository.save(existing);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("Tag Funcional", "Tag funcional com id " + id + " não encontrada.");
        }
        repository.deleteById(id);
    }

    private void validarUnicidade(String nome) {
        if (repository.findByNome(nome).isPresent()) {
            throw new ResourceWithSameNameException("Tag Funcional", "Tag funcional com esse nome já existente.");
        }
    }

    private void validarUnicidade(String nome, Long id) {
        repository.findByNome(nome)
                .filter(tag -> !tag.getId().equals(id))
                .ifPresent(tag -> {
                    throw new ResourceWithSameNameException("Tag Funcional", "Tag funcional com esse nome já existente.");
                });
    }
}
