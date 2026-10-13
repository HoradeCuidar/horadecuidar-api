package com.hdc.hdc.orientacao_funcional.orientacao;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdc.hdc.infra.bucket.service.R2Service;
import com.hdc.hdc.orientacao_funcional.categoria.*;
import com.hdc.hdc.orientacao_funcional.orientacao.dto.*;
import com.hdc.hdc.orientacao_funcional.tag.TagFuncionalRepository;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.usuarios.enums.Role;
import com.hdc.hdc.util.exception.InvalidValueException;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrientacaoFuncionalServiceTest {
    @Mock R2Service r2Service;
    @Mock TagFuncionalRepository tagFuncionalRepository;
    @Mock OrientacaoFuncionalRepository repository;
    @Mock CategoriaOrientacaoRepository categorias;
    @Mock HistoricoOrientacaoRepository historico;
    @Mock OrientacaoFuncionalMapper mapper;
    @Mock ObjectMapper objectMapper;
    @InjectMocks OrientacaoFuncionalService service;
    OrientacaoFuncional orientacao;
    Usuario usuario;

    @BeforeEach void preparar() {
        orientacao = new OrientacaoFuncional(); orientacao.setId(1L);
        usuario = new Usuario(); usuario.setId(10); usuario.setRole(Role.PROFISSIONAL_DA_SAUDE);
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities()));
    }
    @AfterEach void limpar() { SecurityContextHolder.clearContext(); }
    void carregar() { when(repository.findById(1L)).thenReturn(Optional.of(orientacao)); }
    void completa() {
        orientacao.setNome("Orientação revisada"); orientacao.setResumo("Resumo");
        CategoriaOrientacao categoria = new CategoriaOrientacao(); categoria.setId(2L); orientacao.setCategoria(categoria);
        PassoOrientacao passo = new PassoOrientacao(); passo.setId(3L); passo.setInstrucao("Instrução revisada"); orientacao.getPassos().add(passo);
        for (TipoCuidado tipo : TipoCuidado.values()) { CuidadoOrientacao c = new CuidadoOrientacao(); c.setTipo(tipo); c.setTexto("Texto revisado"); orientacao.getCuidados().add(c); }
        MidiaOrientacao capa = new MidiaOrientacao(); capa.setCapa(true); orientacao.getMidias().add(capa);
    }
    @Test void inativacaoPreservaEntidadeEHistorico() {
        carregar(); orientacao.setStatus(StatusOrientacao.PUBLICADA); orientacao.setAtivo(true);
        service.deletar(1L);
        verify(repository).saveAndFlush(orientacao);
        verify(repository, never()).deleteById(anyLong());
        verify(historico).save(any(HistoricoOrientacao.class));
        assertEquals(StatusOrientacao.INATIVA, orientacao.getStatus()); assertFalse(orientacao.getAtivo());
    }
    @Test void naoPublicaRascunhoIncompleto() {
        carregar(); assertThrows(InvalidValueException.class, () -> service.publicar(1L, true));
        verify(repository, never()).saveAndFlush(any());
    }
    @Test void exigeRevisaoExplicita() {
        carregar(); completa(); assertThrows(InvalidValueException.class, () -> service.publicar(1L, false));
    }
    @Test void profissionalPodePublicarConteudoCompleto() {
        carregar(); completa(); service.publicar(1L, true);
        assertEquals(StatusOrientacao.PUBLICADA, orientacao.getStatus());
        assertEquals(usuario, orientacao.getRevisadoPor()); assertTrue(orientacao.isCuidadosRevisados()); assertNotNull(orientacao.getPublicadoEm());
    }
    @Test void naoPublicaGrupoDeCuidadosVazio() {
        carregar(); completa(); orientacao.getCuidados().removeLast();
        assertThrows(InvalidValueException.class, () -> service.publicar(1L, true));
    }
    @Test void pacienteNaoAcessaRascunhoPorId() {
        carregar(); usuario.setRole(Role.PACIENTE);
        assertThrows(ResourceNotFoundException.class, () -> service.buscarPorId(1L));
    }
    @Test void edicaoPreservaIdDosPassosERetornaParaRevisao() throws Exception {
        carregar(); completa(); orientacao.setStatus(StatusOrientacao.PUBLICADA); orientacao.setCuidadosRevisados(true);
        when(tagFuncionalRepository.findAllById(any())).thenReturn(List.of());
        service.atualizarOrientacao(1L, new OrientacaoFuncionalRequestDTO("Atualizada", null, null, List.of(), "Resumo", null, null, null, List.of(new PassoDTO(3L, "Novo texto")), List.of()), null);
        assertEquals(3L, orientacao.getPassos().getFirst().getId());
        assertEquals(StatusOrientacao.RASCUNHO, orientacao.getStatus()); assertFalse(orientacao.isCuidadosRevisados());
        verify(historico).save(any());
    }
    @Test void naoAceitaImagemDisfarcada() {
        carregar(); MockMultipartFile arquivo = new MockMultipartFile("arquivo", "foto.png", "image/png", "isto nao e imagem".getBytes());
        assertThrows(InvalidValueException.class, () -> service.adicionarMidia(1L, arquivo, new MidiaRequestDTO(null, 0, null, null, true)));
        verifyNoInteractions(r2Service);
    }
    @Test void naoPermiteReativarSemPublicacao() {
        assertThrows(InvalidValueException.class, () -> service.alterarStatus(1L, true));
        verifyNoInteractions(repository);
    }
}
