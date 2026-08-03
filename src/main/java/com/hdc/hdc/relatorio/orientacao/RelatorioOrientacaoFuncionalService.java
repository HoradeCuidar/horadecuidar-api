package com.hdc.hdc.relatorio.orientacao;

import com.hdc.hdc.pacientes.PacienteRepository;
import com.hdc.hdc.relatorio.adesao.dto.DadosDetalhamentoDiarioMedicamento;
import com.hdc.hdc.relatorio.adesao.dto.DetalhamentoAdesaoMedicamentoResponseDTO;
import com.hdc.hdc.relatorio.adesao.dto.DetalhamentoDiarioMedicamentoDTO;
import com.hdc.hdc.relatorio.orientacao.dto.DetalhamentOrientacaoFuncionalResponseDTO;
import com.hdc.hdc.relatorio.orientacao.dto.DetalhamentoDiarioFuncionalDTO;
import com.hdc.hdc.relatorio.orientacao.dto.ResumoFuncionalDTO;
import com.hdc.hdc.util.exception.InvalidValueException;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioOrientacaoFuncionalService {

    private final RelatorioOrientacaoFuncionalRepository relatorioFuncionalRepository;
    private final PacienteRepository pacienteRepository;

    public ResumoFuncionalDTO obterResumoFuncional(Integer pacienteId, LocalDate dataInicial, LocalDate dataFinal) {
        return relatorioFuncionalRepository.buscarResumo(pacienteId, dataInicial, dataFinal);
    }


}
