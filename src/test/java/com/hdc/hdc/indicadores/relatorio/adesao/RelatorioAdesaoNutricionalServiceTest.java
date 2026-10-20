package com.hdc.hdc.indicadores.relatorio.adesao;

import com.hdc.hdc.indicadores.relatorio.adesao.dto.DadosDetalhamentoDiarioNutricional;
import com.hdc.hdc.indicadores.relatorio.adesao.dto.DadosResumoAdesaoNutricional;
import com.hdc.hdc.pacientes.Paciente;
import com.hdc.hdc.pacientes.PacienteRepository;
import com.hdc.hdc.util.exception.InvalidValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RelatorioAdesaoNutricionalServiceTest {
    @Mock RelatorioAdesaoNutricionalRepository repository;
    @Mock PacienteRepository pacienteRepository;
    @InjectMocks RelatorioAdesaoNutricionalService service;

    @Test
    void resumoCalculaPercentualDasRefeicoes() {
        LocalDate data = LocalDate.now().minusDays(2);
        when(repository.buscarResumo(1, data, data))
                .thenReturn(new DadosResumoAdesaoNutricional(4, 3, 1, 0));

        var resposta = service.obterResumo(1, data, data);

        assertEquals(4, resposta.esperado());
        assertEquals(new BigDecimal("75.00"), resposta.percentual());
    }

    @Test
    void detalhamentoPreservaTotalMesmoQuandoPaginaEstaVazia() {
        LocalDate data = LocalDate.now().minusDays(2);
        when(pacienteRepository.findById(1)).thenReturn(Optional.of(mock(Paciente.class)));
        when(repository.buscarDetalhamentoDiario(1, data, data, 5, 10)).thenReturn(List.of());
        when(repository.contarDias(1, data, data)).thenReturn(2L);

        var resposta = service.obterDetalhamento(1, data, data, 5, 10);

        assertEquals(5, resposta.conteudo().getNumber());
        assertEquals(2, resposta.conteudo().getTotalElements());
        assertTrue(resposta.conteudo().isEmpty());
    }

    @Test
    void detalhamentoIdentificaTipoECalculaPercentual() {
        LocalDate data = LocalDate.now().minusDays(2);
        when(pacienteRepository.findById(1)).thenReturn(Optional.of(mock(Paciente.class)));
        when(repository.buscarDetalhamentoDiario(1, data, data, 0, 10))
                .thenReturn(List.of(new DadosDetalhamentoDiarioNutricional(data, 4, 3, 1, 0, 1)));

        var linha = service.obterDetalhamento(1, data, data, 0, 10).conteudo().getContent().getFirst();

        assertEquals("NUTRICIONAL", linha.tipo());
        assertEquals(new BigDecimal("75.00"), linha.percentualAdesao());
    }

    @Test
    void rejeitaPaginaNegativa() {
        LocalDate data = LocalDate.now().minusDays(2);
        when(pacienteRepository.findById(1)).thenReturn(Optional.of(mock(Paciente.class)));

        assertThrows(InvalidValueException.class,
                () -> service.obterDetalhamento(1, data, data, -1, 10));
        verifyNoInteractions(repository);
    }
}
