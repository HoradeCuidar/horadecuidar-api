package com.hdc.hdc.prescricao_nutricional;

import com.hdc.hdc.prescricao_nutricional.adesao_refeicoes.*;
import com.hdc.hdc.prescricao_nutricional.alimento.dto.AlimentoPrescritoCreateDTO;
import com.hdc.hdc.prescricao_nutricional.alimento.enums.UnidadeDeMedida;
import com.hdc.hdc.prescricao_nutricional.alimento.substituicao.SubstituicaoAlimentarCreateDTO;
import com.hdc.hdc.prescricao_nutricional.dto.PrescricaoNutricionalCreateDTO;
import com.hdc.hdc.prescricao_nutricional.enums.StatusPrescricao;
import com.hdc.hdc.prescricao_nutricional.refeicao.dto.RefeicaoCreateDTO;
import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.dto.OpcaoRefeicaoCreateDTO;
import com.hdc.hdc.util.formatter.DataFormatter;
import com.hdc.hdc.util.formatter.StatusPrescricaoFormatter;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.mockito.ArgumentCaptor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ContratoNutricionalTest {
    @Test void mapeamentoRecuperaEstruturaCompleta() {
        var sub = new SubstituicaoAlimentarCreateDTO(); sub.setDescricao("Cuscuz"); sub.setQuantidade(new BigDecimal("120"));
        sub.setUnidade(UnidadeDeMedida.G); sub.setMedidaCaseira("6 colheres"); sub.setOrdem(1);
        var alimento = new AlimentoPrescritoCreateDTO(); alimento.setDescricao("Arroz"); alimento.setQuantidade(new BigDecimal("125")); alimento.setUnidade(UnidadeDeMedida.G);
        alimento.setMedidaCaseira("5 colheres"); alimento.setSubstituicoes(List.of(sub));
        var opcao = new OpcaoRefeicaoCreateDTO(1, "Padrão", "Cozinhar", List.of(alimento)); opcao.setOrientacoesPreparo("Cozinhar");
        var refeicao = new RefeicaoCreateDTO("Almoço", 1, null, LocalTime.NOON, List.of(opcao)); refeicao.setHorario(LocalTime.NOON);
        var dto = new PrescricaoNutricionalCreateDTO(1, 2, LocalDate.now().plusDays(1), LocalDate.now().plusDays(3), null, List.of(refeicao));
        var mapper = new PrescricaoNutricionalMapperImpl();
        ReflectionTestUtils.setField(mapper, "statusPrescricaoFormatter", new StatusPrescricaoFormatter());
        ReflectionTestUtils.setField(mapper, "dataFormatter", new DataFormatter());
        var model = mapper.createDTOtoModel(dto); model.setStatus(StatusPrescricao.ATIVA);
        var resposta = mapper.modeltoResponseDTO(model);
        assertEquals(LocalTime.NOON, resposta.getRefeicoes().get(0).getHorario());
        var opcaoResposta = resposta.getRefeicoes().get(0).getOpcoes().get(0);
        assertEquals("Cozinhar", opcaoResposta.getOrientacoesPreparo());
        var alimentoResposta = opcaoResposta.getAlimentos().get(0);
        assertEquals("5 colheres", alimentoResposta.getMedidaCaseira());
        assertEquals("6 colheres", alimentoResposta.getSubstituicoes().get(0).getMedidaCaseira());
        assertEquals("Cuscuz", alimentoResposta.getSubstituicoes().get(0).getDescricao());
    }

    @Test void ocorrenciasSaoGeradasPorRefeicaoEDia() {
        var repository = mock(OcorrenciaRefeicaoRepository.class);
        var service = new OcorrenciaRefeicaoService(repository);
        var p = new PrescricaoNutricional(); p.setId(1); p.setDataInicio(LocalDate.of(2026, 12, 1)); p.setDataFim(LocalDate.of(2026, 12, 2));
        for (int i = 0; i < 2; i++) {
            var r = new com.hdc.hdc.prescricao_nutricional.refeicao.Refeicao(); r.setId(i+1); r.setOrdem(i+1);
            p.getRefeicoes().add(r);
        }
        service.gerarOcorrencias(p);
        @SuppressWarnings("unchecked") ArgumentCaptor<List<OcorrenciaRefeicao>> captor = ArgumentCaptor.forClass(List.class);
        verify(repository).saveAll(captor.capture());
        assertEquals(4, captor.getValue().size());
        assertTrue(captor.getValue().stream().allMatch(o -> o.getOpcao() == null && o.getStatus() == com.hdc.hdc.prescricao_nutricional.enums.StatusAdesao.PENDENTE));
    }
}
