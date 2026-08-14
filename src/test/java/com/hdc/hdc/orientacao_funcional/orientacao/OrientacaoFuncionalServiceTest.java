package com.hdc.hdc.orientacao_funcional.orientacao;

import com.hdc.hdc.infra.bucket.service.R2Service;
import com.hdc.hdc.orientacao_funcional.tag.TagFuncionalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrientacaoFuncionalServiceTest {

    @Mock
    private R2Service r2Service;

    @Mock
    private TagFuncionalRepository tagFuncionalRepository;

    @Mock
    private OrientacaoFuncionalRepository orientacaoFuncionalRepository;

    @Mock
    private OrientacaoFuncionalMapper mapper;

    @InjectMocks
    private OrientacaoFuncionalService service;

    private OrientacaoFuncional orientacao;

    @BeforeEach
    void setUp() {
        orientacao = new OrientacaoFuncional();
        orientacao.setId(1L);
        orientacao.setAtivo(true);
        orientacao.setDataCriacao(LocalDateTime.now());
    }

    @Test
    void deletarDeveInativarOrientacaoSemExcluirFisicamente() {
        when(orientacaoFuncionalRepository.findById(1L)).thenReturn(Optional.of(orientacao));
        when(orientacaoFuncionalRepository.save(any(OrientacaoFuncional.class))).thenAnswer(invocation -> invocation.getArgument(0));

        service.deletar(1L);

        verify(orientacaoFuncionalRepository).save(orientacao);
        verify(orientacaoFuncionalRepository, never()).deleteById(anyLong());
        assertFalse(orientacao.getAtivo());
        assertNotNull(orientacao.getDataAtualizacao());
    }
}
