package com.hdc.hdc.medicamentos;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicamentoService {

    private final MedicamentoRepository medicamentoRepository;

    @Transactional(readOnly = true)
    public List<Medicamento> buscarSugestoes(String nome) {
        return medicamentoRepository.buscarPorNomeSugestoes(nome.trim());
    }

    @Transactional
    public Medicamento buscarOuCriar(String nome) {
        String nomeFormatado = nome.trim();
        return medicamentoRepository
            .findByNomeIgnoreCase(nomeFormatado)
            .orElseGet(() -> medicamentoRepository
                    .save(new Medicamento(nomeFormatado)));
    }
}
