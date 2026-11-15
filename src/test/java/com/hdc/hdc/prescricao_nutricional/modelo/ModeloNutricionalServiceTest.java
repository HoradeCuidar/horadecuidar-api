package com.hdc.hdc.prescricao_nutricional.modelo;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.hdc.hdc.prescricao_nutricional.alimento.dto.AlimentoPrescritoCreateDTO;
import com.hdc.hdc.prescricao_nutricional.alimento.enums.UnidadeDeMedida;
import com.hdc.hdc.prescricao_nutricional.alimento.substituicao.SubstituicaoAlimentarCreateDTO;
import com.hdc.hdc.prescricao_nutricional.refeicao.dto.RefeicaoCreateDTO;
import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.dto.OpcaoRefeicaoCreateDTO;
import com.hdc.hdc.util.exception.InvalidValueException;
import jakarta.validation.Validation;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ModeloNutricionalServiceTest {
    private final ModeloNutricionalRepository repository = mock(ModeloNutricionalRepository.class);
    private final ModeloNutricionalService service = new ModeloNutricionalService(repository,
            new ObjectMapper().registerModule(new JavaTimeModule()));

    private ModeloNutricionalRequestDTO pedido() {
        var sub = new SubstituicaoAlimentarCreateDTO(); sub.setDescricao("Cuscuz");
        sub.setQuantidade(new BigDecimal("120")); sub.setUnidade(UnidadeDeMedida.G);
        sub.setOrdem(1); sub.setMedidaCaseira("6 colheres de sopa");
        var alimento = new AlimentoPrescritoCreateDTO(); alimento.setDescricao("Pão francês");
        alimento.setQuantidade(new BigDecimal("50")); alimento.setUnidade(UnidadeDeMedida.G);
        alimento.setMedidaCaseira("1 unidade"); alimento.setSubstituicoes(List.of(sub));
        var opcao = new OpcaoRefeicaoCreateDTO(); opcao.setDescricao("Padrão"); opcao.setOrdem(1);
        opcao.setOrientacoesPreparo("Servir sem açúcar"); opcao.setAlimentos(List.of(alimento));
        var refeicao = new RefeicaoCreateDTO(); refeicao.setNome("Café da manhã");
        refeicao.setOrdem(1); refeicao.setHorario(LocalTime.of(7, 0)); refeicao.setOpcoes(List.of(opcao));
        var dto = new ModeloNutricionalRequestDTO(); dto.setNome("Plano de referência");
        dto.setReferenciaKcal(1200); dto.setRefeicoes(List.of(refeicao)); return dto;
    }

    @Test void criaERecuperaModeloSemPacienteOuVigencia() {
        when(repository.save(any())).thenAnswer(invocacao -> {
            ModeloNutricional modelo = invocacao.getArgument(0); modelo.setId(10); return modelo;
        });
        var resposta = service.criar(pedido(), 2);
        assertEquals(1, resposta.getVersao());
        assertEquals(1200, resposta.getReferenciaKcal());
        assertEquals(LocalTime.of(7, 0), resposta.getRefeicoes().get(0).getHorario());
        assertEquals("6 colheres de sopa", resposta.getRefeicoes().get(0).getOpcoes().get(0)
                .getAlimentos().get(0).getSubstituicoes().get(0).getMedidaCaseira());
        verify(repository).save(argThat(modelo -> modelo.getCriadoPorId() == 2
                && !modelo.getRefeicoesJson().contains("pacienteId")
                && !modelo.getRefeicoesJson().contains("dataInicio")));
    }

    @Test void edicaoIncrementaVersaoEDuplicacaoCriaRegistroIndependente() {
        var origem = new ModeloNutricional(); origem.setId(10); origem.setVersao(1); origem.setNome("Base");
        origem.setRefeicoesJson("[]"); origem.setCriadoPorId(2);
        when(repository.findById(10)).thenReturn(Optional.of(origem));
        when(repository.save(any())).thenAnswer(invocacao -> invocacao.getArgument(0));
        var atualizacao = pedido(); atualizacao.setVersao(1);
        var editado = service.atualizar(10, atualizacao);
        assertEquals(2, editado.getVersao());
        var copia = service.duplicar(10, 3);
        assertNull(copia.getId());
        assertEquals(1, copia.getVersao());
        assertEquals(3, copia.getCriadoPorId());
        assertEquals(2, origem.getVersao());
    }

    @Test void edicaoRejeitaVersaoDesatualizada() {
        var origem = new ModeloNutricional(); origem.setVersao(3);
        when(repository.findById(10)).thenReturn(Optional.of(origem));
        var atualizacao = pedido(); atualizacao.setVersao(2);
        assertThrows(InvalidValueException.class, () -> service.atualizar(10, atualizacao));
        verify(repository, never()).save(any());
    }

    @Test void validacaoRejeitaModeloSemRefeicao() {
        var dto = pedido(); dto.setRefeicoes(List.of());
        try (var factory = Validation.buildDefaultValidatorFactory()) {
            assertFalse(factory.getValidator().validate(dto).isEmpty());
        }
    }
}
