package com.hdc.hdc.adesao.classificacao;

import com.hdc.hdc.adesao.classificacao.repositories.ResumoAdesaoPacienteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClassificacaoAdesaoService {

    private final ResumoAdesaoPacienteRepository resumoAdesaoPacienteRepository;

    @Transactional
    public void recalcular(Integer pacienteId) {

    }
}
