package com.hdc.hdc.prescricao_nutricional.profissional;

import com.hdc.hdc.prescricao_nutricional.*;
import com.hdc.hdc.prescricao_nutricional.adesao_refeicoes.*;
import com.hdc.hdc.prescricao_nutricional.alimento.*;
import com.hdc.hdc.prescricao_nutricional.alimento.enums.UnidadeDeMedida;
import com.hdc.hdc.prescricao_nutricional.alimento.substituicao.SubstituicaoAlimentar;
import com.hdc.hdc.prescricao_nutricional.enums.*;
import com.hdc.hdc.prescricao_nutricional.modelo.*;
import com.hdc.hdc.prescricao_nutricional.refeicao.*;
import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.*;
import com.hdc.hdc.util.exception.InvalidValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PrescricaoNutricionalProfissionalServiceTest {
    @Mock PrescricaoNutricionalRepository prescricoes;
    @Mock PrescricaoNutricionalMapper mapper;
    @Mock OcorrenciaRefeicaoService ocorrencias;
    @Mock OcorrenciaRefeicaoRepository ocorrenciaRepository;
    @Mock ModeloNutricionalRepository modelos;
    @InjectMocks PrescricaoNutricionalProfissionalService service;

    private PrescricaoNutricional prescricao(LocalDate inicio) {
        var p = new PrescricaoNutricional(); p.setId(10); p.setPacienteId(1);
        p.setDataInicio(inicio); p.setDataFim(inicio.plusDays(3)); p.setStatus(StatusPrescricao.ATIVA);
        var r = new Refeicao(); r.setId(20); r.setNome("Almoço"); r.setOrdem(1);
        for (int i = 0; i < 2; i++) {
            var o = new OpcaoRefeicao(); o.setId(30+i); o.setOrdem(i+1);
            var a = new AlimentoPrescrito(); a.setId(40+i); a.setDescricao("Arroz");
            a.setQuantidade(new BigDecimal("125")); a.setUnidade(UnidadeDeMedida.G);
            a.setMedidaCaseira("5 colheres de sopa cheias");
            if (i == 0) {
                var sub = new SubstituicaoAlimentar(); sub.setId(50); sub.setDescricao("Cuscuz");
                sub.setQuantidade(new BigDecimal("120")); sub.setUnidade(UnidadeDeMedida.G);
                sub.setMedidaCaseira("6 colheres de sopa"); sub.setOrdem(1);
                a.getSubstituicoes().add(sub);
            }
            o.getAlimentos().add(a); r.getOpcoes().add(o);
        }
        p.getRefeicoes().add(r);
        var outra = new Refeicao(); outra.setId(21); outra.setNome("Jantar"); outra.setOrdem(2);
        var opcao = new OpcaoRefeicao(); opcao.setOrdem(1);
        var alimento = new AlimentoPrescrito(); alimento.setDescricao("Ovo"); alimento.setQuantidade(BigDecimal.ONE);
        alimento.setUnidade(UnidadeDeMedida.UNIDADE); opcao.getAlimentos().add(alimento);
        outra.getOpcoes().add(opcao); p.getRefeicoes().add(outra);
        return p;
    }

    @Test void cadastroPreservaArvoreEAlternativas() {
        var p = prescricao(LocalDate.now().plusDays(2));
        when(prescricoes.save(p)).thenReturn(p);
        service.cadastrar(p);
        assertEquals(2, p.getRefeicoes().size());
        assertEquals(2, p.getRefeicoes().get(0).getOpcoes().size());
        var alimento = p.getRefeicoes().get(0).getOpcoes().get(0).getAlimentos().get(0);
        assertEquals("5 colheres de sopa cheias", alimento.getMedidaCaseira());
        assertEquals("6 colheres de sopa", alimento.getSubstituicoes().get(0).getMedidaCaseira());
        assertSame(alimento, alimento.getSubstituicoes().get(0).getAlimento());
        assertTrue(p.getRefeicoes().get(1).getOpcoes().get(0).getAlimentos().get(0).getSubstituicoes().isEmpty());
        verify(ocorrencias).gerarOcorrencias(p);
    }

    @Test void cadastroRegistraOrigemDoModeloSemDependerDeleParaHistorico() {
        var p = prescricao(LocalDate.now().plusDays(2));
        p.setModeloOrigemId(7);
        p.setModeloOrigemVersao(3);
        var modelo = new ModeloNutricional();
        modelo.setId(7); modelo.setNome("Plano base"); modelo.setVersao(3);
        when(modelos.findById(7)).thenReturn(Optional.of(modelo));
        when(prescricoes.save(p)).thenReturn(p);

        service.cadastrar(p);

        assertEquals("Plano base", p.getModeloOrigemNome());
        assertEquals(3, p.getModeloOrigemVersao());
        verify(ocorrencias).gerarOcorrencias(p);
    }

    @Test void cadastroRejeitaVersaoDesatualizadaDoModelo() {
        var p = prescricao(LocalDate.now().plusDays(2));
        p.setModeloOrigemId(7); p.setModeloOrigemVersao(2);
        var modelo = new ModeloNutricional(); modelo.setVersao(3);
        when(modelos.findById(7)).thenReturn(Optional.of(modelo));

        assertThrows(InvalidValueException.class, () -> service.cadastrar(p));
        verify(prescricoes, never()).save(any());
    }

    @Test void edicaoFuturaPreservaIdsERecriaOcorrencias() {
        var atual = prescricao(LocalDate.now().plusDays(2));
        var proposta = prescricao(atual.getDataInicio()); proposta.setDataFim(atual.getDataFim().plusDays(1));
        when(prescricoes.findById(10)).thenReturn(Optional.of(atual));
        service.editar(10, proposta);
        assertEquals(20, atual.getRefeicoes().get(0).getId());
        assertEquals(30, atual.getRefeicoes().get(0).getOpcoes().get(0).getId());
        assertEquals(40, atual.getRefeicoes().get(0).getOpcoes().get(0).getAlimentos().get(0).getId());
        assertEquals(50, atual.getRefeicoes().get(0).getOpcoes().get(0).getAlimentos().get(0).getSubstituicoes().get(0).getId());
        verify(ocorrenciaRepository).deleteByPrescricaoId(10);
        verify(ocorrencias).gerarOcorrencias(atual);
    }

    @Test void prescricaoIniciadaNaoPodeSerEditada() {
        var atual = prescricao(LocalDate.now());
        when(prescricoes.findById(10)).thenReturn(Optional.of(atual));
        assertThrows(InvalidValueException.class, () -> service.editar(10, prescricao(LocalDate.now())));
        verify(ocorrenciaRepository, never()).deleteByPrescricaoId(anyInt());
    }

    @Test void adesaoExistenteNaoPodeSerApagada() {
        var atual = prescricao(LocalDate.now().plusDays(2));
        var registro = new OcorrenciaRefeicao(); registro.setStatus(StatusAdesao.REALIZADO);
        when(prescricoes.findById(10)).thenReturn(Optional.of(atual));
        when(ocorrenciaRepository.findByPrescricaoId(10)).thenReturn(List.of(registro));
        assertThrows(InvalidValueException.class, () -> service.editar(10, prescricao(atual.getDataInicio())));
        verify(ocorrenciaRepository, never()).deleteByPrescricaoId(anyInt());
    }
}
