package com.hdc.hdc.service;

import com.hdc.hdc.dto.create.DoencaCreateDto;
import com.hdc.hdc.mapper.DoencaMapper;
import com.hdc.hdc.model.Doenca;
import com.hdc.hdc.repository.DoencaRepository;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import com.hdc.hdc.util.exception.ResourceWithSameNameException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DoencaService {

    private final DoencaRepository doencaRepository;
    private final DoencaMapper doencaMapper;

    public Doenca save(DoencaCreateDto dto) {
        Doenca entity = doencaMapper.toEntity(dto);
        this.validarUnicidade(dto.nome());
        return doencaRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public Doenca getById(Long id) {
        return doencaRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doença", "Doença com id " + id + " não encontrada."));
    }

    @Transactional(readOnly = true)
    public List<Doenca> getAll() {
        return doencaRepository.findAll();
    }

    public void update(DoencaCreateDto dto, Long id) {
        this.validarUnicidade(dto.nome());

        Doenca entity = doencaRepository
                .findByNome(dto.nome())
                .orElseThrow(() -> new ResourceNotFoundException("Doença", "Doença com nome " + dto.nome() + " não encontrada."));
        entity.setNome(dto.nome());
        doencaRepository.save(entity);

    }

    public void delete(Long id) {
        doencaRepository.deleteById(id);
    }

    private void validarUnicidade(String nome) {
        if(doencaRepository.existsByNome(nome)) {
            throw new ResourceWithSameNameException("Doença com esse nome já existenete.");
        }
    }
}
