package com.hdc.hdc.doencas;

import com.hdc.hdc.doencas.dto.DoencaCreateDto;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import com.hdc.hdc.util.exception.ResourceWithSameNameException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Doença", "Doença com o ID " + id + " não encontrada."));
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
