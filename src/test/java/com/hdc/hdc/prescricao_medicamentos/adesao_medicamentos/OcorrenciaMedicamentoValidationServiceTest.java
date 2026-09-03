package com.hdc.hdc.prescricao_medicamentos.adesao_medicamentos;

import com.hdc.hdc.medicamentos.Medicamento;
import com.hdc.hdc.medicamentos.MedicamentoService;
import com.hdc.hdc.prescricao_medicamentos.PrescricaoMedicamento;
import com.hdc.hdc.prescricao_medicamentos.PrescricaoMedicamentoMapper;
import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacao;
import com.hdc.hdc.prescricao_medicamentos.associacoes.ItemMedicacaoDTO;
import com.hdc.hdc.prescricao_medicamentos.enums.DosagemUnidade;
import com.hdc.hdc.prescricao_medicamentos.enums.IntervaloTipo;
import com.hdc.hdc.prescricao_medicamentos.enums.StatusAdesao;
import com.hdc.hdc.prescricao_medicamentos.enums.ViaAdministracao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OcorrenciaMedicamentoValidationServiceTest {

    @Mock
    private OcorrenciaMedicamentoRepository ocorrenciaRepository;

    @Mock
    private MedicamentoService medicamentoService;

    private OcorrenciaMedicamentoValidationService service;

    @BeforeEach
    void setUp() {
        service = new OcorrenciaMedicamentoValidationService(ocorrenciaRepository, medicamentoService);
    }

    @Test
    void naoDeveDuplicarOcorrenciaRealizadaAoSincronizar() {
        LocalDate hoje = LocalDate.now();
        PrescricaoMedicamento prescricao = prescricao(hoje.minusDays(1), hoje.plusDays(1), 1);
        OcorrenciaMedicamento realizada = ocorrencia(
                prescricao, hoje.plusDays(1), 1, StatusAdesao.REALIZADO
        );
        when(ocorrenciaRepository.findByPrescricaoId(prescricao.getId()))
                .thenReturn(List.of(realizada));

        service.sincronizarOcorrencias(prescricao, hoje);

        assertThat(realizada.getStatus()).isEqualTo(StatusAdesao.REALIZADO);
        verify(ocorrenciaRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deveGerarAgendaInicialComUmaOcorrenciaPorDiaEDose() {
        LocalDate inicio = LocalDate.now().plusDays(1);
        PrescricaoMedicamento prescricao = prescricao(inicio, inicio.plusDays(2), 2);
        when(ocorrenciaRepository.findByPrescricaoId(prescricao.getId())).thenReturn(List.of());
        ArgumentCaptor<OcorrenciaMedicamento> captor =
                ArgumentCaptor.forClass(OcorrenciaMedicamento.class);

        service.geradorOcorrencias(prescricao);

        verify(ocorrenciaRepository, times(6)).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(OcorrenciaMedicamento::getDataPrevista)
                .containsOnly(inicio, inicio.plusDays(1), inicio.plusDays(2));
        assertThat(captor.getAllValues())
                .extracting(OcorrenciaMedicamento::getOrdemNoDia)
                .containsOnly(1, 2);
    }

    @Test
    void deveCancelarSomenteDoseFuturaQueDeixouDeSerEsperada() {
        LocalDate hoje = LocalDate.now();
        PrescricaoMedicamento prescricao = prescricao(hoje, hoje.plusDays(1), 1);
        OcorrenciaMedicamento realizadaHoje = ocorrencia(prescricao, hoje, 1, StatusAdesao.REALIZADO);
        OcorrenciaMedicamento futuraOrdemUm = ocorrencia(
                prescricao, hoje.plusDays(1), 1, StatusAdesao.PENDENTE
        );
        OcorrenciaMedicamento futuraOrdemDois = ocorrencia(
                prescricao, hoje.plusDays(1), 2, StatusAdesao.PENDENTE
        );
        when(ocorrenciaRepository.findByPrescricaoId(prescricao.getId()))
                .thenReturn(List.of(realizadaHoje, futuraOrdemUm, futuraOrdemDois));

        service.sincronizarOcorrencias(prescricao, hoje);

        assertThat(realizadaHoje.getStatus()).isEqualTo(StatusAdesao.REALIZADO);
        assertThat(futuraOrdemUm.getStatus()).isEqualTo(StatusAdesao.PENDENTE);
        assertThat(futuraOrdemDois.getStatus()).isEqualTo(StatusAdesao.CANCELADO);
        verify(ocorrenciaRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deveReativarCanceladaFuturaSemReativarCanceladaDoPassado() {
        LocalDate hoje = LocalDate.now();
        PrescricaoMedicamento prescricao = prescricao(hoje.minusDays(1), hoje.plusDays(1), 1);
        OcorrenciaMedicamento passada = ocorrencia(
                prescricao, hoje.minusDays(1), 1, StatusAdesao.CANCELADO
        );
        OcorrenciaMedicamento futura = ocorrencia(
                prescricao, hoje.plusDays(1), 1, StatusAdesao.CANCELADO
        );
        when(ocorrenciaRepository.findByPrescricaoId(prescricao.getId()))
                .thenReturn(List.of(passada, futura));

        service.sincronizarOcorrencias(prescricao, hoje);

        assertThat(passada.getStatus()).isEqualTo(StatusAdesao.CANCELADO);
        assertThat(futura.getStatus()).isEqualTo(StatusAdesao.PENDENTE);
        verify(ocorrenciaRepository, never()).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void deveCriarApenasOcorrenciaFuturaQueNuncaExistiu() {
        LocalDate hoje = LocalDate.now();
        PrescricaoMedicamento prescricao = prescricao(hoje.minusDays(1), hoje.plusDays(1), 1);
        when(ocorrenciaRepository.findByPrescricaoId(prescricao.getId())).thenReturn(List.of());
        ArgumentCaptor<OcorrenciaMedicamento> captor =
                ArgumentCaptor.forClass(OcorrenciaMedicamento.class);

        service.sincronizarOcorrencias(prescricao, hoje);

        verify(ocorrenciaRepository).save(captor.capture());
        assertThat(captor.getValue().getDataPrevista()).isEqualTo(hoje.plusDays(1));
        assertThat(captor.getValue().getStatus()).isEqualTo(StatusAdesao.PENDENTE);
    }

    @Test
    void deveManterIdDoItemAtualizadoEInativarItemRemovido() {
        LocalDate hoje = LocalDate.now();
        PrescricaoMedicamento prescricao = prescricao(hoje, hoje.plusDays(1), 1);
        ItemMedicacao mantido = prescricao.getMedicacoes().getFirst();
        ItemMedicacao removido = item(prescricao, 2L, 1);
        prescricao.getMedicacoes().add(removido);

        ItemMedicacaoDTO request = itemRequest(mantido.getId(), "Losartana atualizada", 2);
        Medicamento medicamento = new Medicamento();
        medicamento.setNome("Losartana atualizada");
        when(medicamentoService.buscarOuCriar("Losartana atualizada")).thenReturn(medicamento);

        service.sincronizarItens(prescricao, List.of(request));

        assertThat(prescricao.getMedicacoes().getFirst()).isSameAs(mantido);
        assertThat(mantido.getId()).isEqualTo(1L);
        assertThat(mantido.getQuantidadeDoses()).isEqualTo(2);
        assertThat(mantido.isAtivo()).isTrue();
        assertThat(removido.isAtivo()).isFalse();
    }

    @Test
    void deveAtualizarItemComIdSemCriarOutroItemMedicacao() {
        LocalDate hoje = LocalDate.now();
        PrescricaoMedicamento prescricao = prescricao(hoje, hoje.plusDays(1), 1);
        ItemMedicacao itemOriginal = prescricao.getMedicacoes().getFirst();
        ItemMedicacaoDTO request = itemRequest(itemOriginal.getId(), "Losartana Potássica", 2);
        Medicamento medicamento = new Medicamento();
        medicamento.setNome("Losartana Potássica");
        when(medicamentoService.buscarOuCriar("Losartana Potássica")).thenReturn(medicamento);

        service.sincronizarItens(prescricao, List.of(request));

        assertThat(prescricao.getMedicacoes()).hasSize(1);
        assertThat(prescricao.getMedicacoes().getFirst()).isSameAs(itemOriginal);
        assertThat(itemOriginal.getId()).isEqualTo(1L);
        assertThat(itemOriginal.getNomeMedicamento()).isEqualTo("Losartana Potássica");
        assertThat(itemOriginal.getQuantidadeDoses()).isEqualTo(2);
    }

    @Test
    void deveRetornarIdDoItemMedicacaoParaProximasAtualizacoes() {
        PrescricaoMedicamento prescricao = prescricao(LocalDate.now(), LocalDate.now().plusDays(1), 1);
        ItemMedicacao item = prescricao.getMedicacoes().getFirst();
        PrescricaoMedicamentoMapper mapper = new PrescricaoMedicamentoMapper(medicamentoService);

        ItemMedicacaoDTO response = mapper.toItemMedicacaoDTO(item);

        assertThat(response.getId()).isEqualTo(item.getId());
    }

    @Test
    void deveCriarNovoItemMedicacaoSomenteQuandoIdNaoForInformado() {
        LocalDate hoje = LocalDate.now();
        PrescricaoMedicamento prescricao = prescricao(hoje, hoje.plusDays(1), 1);
        ItemMedicacao itemOriginal = prescricao.getMedicacoes().getFirst();
        ItemMedicacaoDTO request = itemRequest(null, "Hidroclorotiazida", 1);
        Medicamento medicamento = new Medicamento();
        medicamento.setNome("Hidroclorotiazida");
        when(medicamentoService.buscarOuCriar("Hidroclorotiazida")).thenReturn(medicamento);

        service.sincronizarItens(prescricao, List.of(request));

        assertThat(prescricao.getMedicacoes()).hasSize(2);
        assertThat(itemOriginal.isAtivo()).isFalse();
        ItemMedicacao novoItem = prescricao.getMedicacoes().get(1);
        assertThat(novoItem.getId()).isNull();
        assertThat(novoItem.getNomeMedicamento()).isEqualTo("Hidroclorotiazida");
        assertThat(novoItem.isAtivo()).isTrue();
    }

    @Test
    void deveUsarIdOriginalDoItemAoEstenderPrescricaoParaOFuturo() {
        LocalDate hoje = LocalDate.now();
        PrescricaoMedicamento prescricao = prescricao(hoje.minusDays(10), hoje.plusDays(2), 1);
        ItemMedicacao itemOriginal = prescricao.getMedicacoes().getFirst();
        OcorrenciaMedicamento realizada = ocorrencia(
                prescricao, hoje.minusDays(9), 1, StatusAdesao.REALIZADO
        );
        when(ocorrenciaRepository.findByPrescricaoId(prescricao.getId()))
                .thenReturn(List.of(realizada));
        ArgumentCaptor<OcorrenciaMedicamento> captor =
                ArgumentCaptor.forClass(OcorrenciaMedicamento.class);

        service.sincronizarOcorrencias(prescricao, hoje);

        verify(ocorrenciaRepository, times(2)).save(captor.capture());
        assertThat(captor.getAllValues())
                .extracting(ocorrencia -> ocorrencia.getItemMedicacao().getId())
                .containsOnly(itemOriginal.getId());
        assertThat(realizada.getStatus()).isEqualTo(StatusAdesao.REALIZADO);
    }

    @Test
    void deveRejeitarMesmoIdDeItemRepetidoNaAtualizacao() {
        LocalDate hoje = LocalDate.now();
        PrescricaoMedicamento prescricao = prescricao(hoje, hoje.plusDays(1), 1);
        ItemMedicacaoDTO primeiro = itemRequest(1L, "Losartana", 1);
        ItemMedicacaoDTO repetido = itemRequest(1L, "Losartana", 2);

        assertThatThrownBy(() -> service.sincronizarItens(prescricao, List.of(primeiro, repetido)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("mais de uma vez");
    }

    @Test
    void naoDevePermitirEstenderInicioDaPrescricaoParaOPassado() {
        LocalDate hoje = LocalDate.now();
        PrescricaoMedicamento prescricao = prescricao(hoje, hoje.plusDays(2), 1);
        com.hdc.hdc.prescricao_medicamentos.dto.PrescricaoMedicamentoRequestDTO request =
                new com.hdc.hdc.prescricao_medicamentos.dto.PrescricaoMedicamentoRequestDTO();
        request.setDataInicio(hoje.minusDays(1));
        request.setDataFim(hoje.plusDays(2));

        assertThatThrownBy(() -> service.validarAtualizacaoDoPeriodo(prescricao, request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("passado");
    }

    private PrescricaoMedicamento prescricao(LocalDate inicio, LocalDate fim, int doses) {
        PrescricaoMedicamento prescricao = new PrescricaoMedicamento();
        prescricao.setId(UUID.randomUUID());
        prescricao.setDataInicio(inicio);
        prescricao.setDataFim(fim);
        prescricao.setAtivo(true);
        prescricao.setMedicacoes(new ArrayList<>());
        prescricao.getMedicacoes().add(item(prescricao, 1L, doses));
        return prescricao;
    }

    private ItemMedicacao item(PrescricaoMedicamento prescricao, Long id, int doses) {
        ItemMedicacao item = new ItemMedicacao();
        item.setId(id);
        item.setPrescricao(prescricao);
        item.setNomeMedicamento("Losartana");
        item.setQuantidadeDoses(doses);
        item.setIntervaloValor(1);
        item.setIntervaloTipo(IntervaloTipo.DIA);
        item.setAtivo(true);
        return item;
    }

    private OcorrenciaMedicamento ocorrencia(
            PrescricaoMedicamento prescricao,
            LocalDate data,
            int ordem,
            StatusAdesao status
    ) {
        OcorrenciaMedicamento ocorrencia = new OcorrenciaMedicamento();
        ocorrencia.setPrescricao(prescricao);
        ocorrencia.setItemMedicacao(prescricao.getMedicacoes().getFirst());
        ocorrencia.setDataPrevista(data);
        ocorrencia.setOrdemNoDia(ordem);
        ocorrencia.setStatus(status);
        return ocorrencia;
    }

    private ItemMedicacaoDTO itemRequest(Long id, String nome, int doses) {
        ItemMedicacaoDTO dto = new ItemMedicacaoDTO();
        dto.setId(id);
        dto.setNomeMedicamento(nome);
        dto.setDosagemValor(50.0);
        dto.setDosagemUnidade(DosagemUnidade.MG);
        dto.setQuantidadeDoses(doses);
        dto.setIntervaloValor(1);
        dto.setIntervaloTipo(IntervaloTipo.DIA);
        dto.setViaAdministracao(ViaAdministracao.ORAL);
        return dto;
    }
}
