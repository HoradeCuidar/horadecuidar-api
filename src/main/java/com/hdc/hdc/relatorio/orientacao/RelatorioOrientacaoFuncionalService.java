package com.hdc.hdc.relatorio.orientacao;

import com.hdc.hdc.pacientes.PacienteRepository;
import com.hdc.hdc.relatorio.orientacao.dto.DetalhamentoRealizacaoFuncionalDTO;
import com.hdc.hdc.relatorio.orientacao.dto.DetalhamentoOrientacaoFuncionalResponseDTO;
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
import java.util.List;

@Service
@RequiredArgsConstructor
public class RelatorioOrientacaoFuncionalService {

    private final RelatorioOrientacaoFuncionalRepository relatorioFuncionalRepository;
    private final PacienteRepository pacienteRepository;

    public ResumoFuncionalDTO obterResumoFuncional(Integer pacienteId, LocalDate dataInicial, LocalDate dataFinal) {
        return relatorioFuncionalRepository.buscarResumo(pacienteId, dataInicial, dataFinal);
    }

    @Transactional(readOnly = true)
    public DetalhamentoOrientacaoFuncionalResponseDTO obterDetalhamentoFuncional(
            Integer pacienteId,
            LocalDate dataInicial,
            LocalDate dataFinal,
            int pagina,
            int tamanho
    ) {
        validarPaciente(pacienteId);
        validarPeriodo(dataInicial, dataFinal);

        LocalDate ultimaDataConcluida = LocalDate.now(ZoneId.systemDefault()).minusDays(1);

        LocalDate dataFinalConsiderada =
                dataFinal.isAfter(ultimaDataConcluida)
                        ? ultimaDataConcluida
                        : dataFinal;

        if (dataInicial.isAfter(dataFinalConsiderada)) {
            return new DetalhamentoOrientacaoFuncionalResponseDTO(
                    dataInicial,
                    dataFinal,
                    null
            );
        }

        List<DetalhamentoRealizacaoFuncionalDTO> dados =
                relatorioFuncionalRepository.buscarDetalhamentoDiarioFuncional(
                        pacienteId,
                        dataInicial,
                        dataFinalConsiderada,
                        pagina,
                        tamanho
                );

        return new DetalhamentoOrientacaoFuncionalResponseDTO(
                dataInicial,
                dataFinalConsiderada,
                toPageDetalhamentoFuncional(dados, tamanho));
    }

    /*
     * Métodos Auxiliares
     * */
    private void validarPeriodo(
            LocalDate dataInicial,
            LocalDate dataFinal
    ) {
        if (dataInicial == null || dataFinal == null) {
            throw new InvalidValueException(
                    "periodo",
                    "As datas inicial e final são obrigatórias."
            );
        }

        if (dataInicial.isAfter(dataFinal)) {
            throw new InvalidValueException(
                    "periodo",
                    "A data inicial não pode ser posterior à data final."
            );
        }
    }

    private void validarPaciente(Integer pacienteId) {
        if (pacienteRepository.findById(pacienteId).isEmpty()) {
            throw new ResourceNotFoundException("pacienteId", "Paciente não encontrado.");
        }
    }

    private Page<DetalhamentoRealizacaoFuncionalDTO> toPageDetalhamentoFuncional(List<DetalhamentoRealizacaoFuncionalDTO> dados, Integer tamanho) {
        long totalElementos = dados.isEmpty()
                ? 0
                : dados.size();

        int totalPaginas = totalElementos == 0
                ? 0
                : (int) Math.ceil(
                (double) totalElementos / tamanho
        );

        Pageable pageable = PageRequest.of(totalPaginas, tamanho);

        return new PageImpl<>(dados, pageable, dados.size());
    }
}
