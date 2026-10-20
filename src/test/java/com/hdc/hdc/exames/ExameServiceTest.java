package com.hdc.hdc.exames;

import com.hdc.hdc.pacientes.PacienteRepository;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.usuarios.enums.Role;
import com.hdc.hdc.util.exception.DeniedAccessException;
import com.hdc.hdc.util.exception.InvalidValueException;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExameServiceTest {
    @Mock ExameRepository exames;
    @Mock ArquivoExameRepository arquivos;
    @Mock EventoExameRepository eventos;
    @Mock PacienteRepository pacientes;
    @Mock ArmazenamentoExame armazenamento;

    private ExameService service() {
        return new ExameService(exames, arquivos, eventos, pacientes, armazenamento, 5242880);
    }

    private Usuario paciente(int id) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setRole(Role.PACIENTE);
        return usuario;
    }

    private Exame exame(StatusExame status, Instant disponibilidade) {
        Exame exame = new Exame();
        exame.setId(10L);
        exame.setPacienteId(1);
        exame.setDataColeta(LocalDate.now());
        exame.setStatus(status);
        exame.setDisponibilizacaoEm(disponibilidade);
        return exame;
    }

    @Test
    void participanteNaoLeRascunhoNemOutroParticipante() {
        when(exames.findById(10L)).thenReturn(Optional.of(exame(StatusExame.RASCUNHO, null)));
        assertThrows(ResourceNotFoundException.class, () -> service().baixar(10L, paciente(1)));
        assertThrows(DeniedAccessException.class, () -> service().baixar(10L, paciente(2)));
        verifyNoInteractions(armazenamento);
    }

    @Test
    void agendamentoLiberaLeituraAoChegarHorarioMesmoSemRotina() {
        Exame exame = exame(StatusExame.AGENDADO, Instant.now().minusSeconds(1));
        when(exames.findById(10L)).thenReturn(Optional.of(exame));
        ArquivoExame arquivo = new ArquivoExame();
        arquivo.setNomeOriginal("laudo.pdf");
        arquivo.setChaveArmazenamento("exames/arquivo.pdf");
        when(arquivos.findByExameIdAndAtivoTrue(10L)).thenReturn(Optional.of(arquivo));
        when(armazenamento.ler("exames/arquivo.pdf")).thenReturn(new byte[]{1, 2});
        assertArrayEquals(new byte[]{1, 2}, service().baixar(10L, paciente(1)).conteudo());
    }

    @Test
    void rotinaRepetidaNaoDuplicaPublicacao() {
        Exame exame = exame(StatusExame.PUBLICADO, Instant.now().minusSeconds(10));
        when(exames.findWithLockById(10L)).thenReturn(Optional.of(exame));
        service().publicarAgendado(10L);
        verifyNoInteractions(eventos);
    }

    @Test
    void naoPermiteReagendarExameJaVisivel() {
        Exame exame = exame(StatusExame.AGENDADO, Instant.now().minusSeconds(1));
        when(exames.findWithLockById(10L)).thenReturn(Optional.of(exame));
        Usuario profissional = new Usuario();
        profissional.setRole(Role.PROFISSIONAL_DA_SAUDE);
        assertThrows(InvalidValueException.class,
                () -> service().agendar(10L, Instant.now().plusSeconds(3600), profissional));
    }
    @Test
    void arquivoInvalidoNaoCriaExameNemObjeto() {
        Usuario profissional = new Usuario();
        profissional.setRole(Role.PROFISSIONAL_DA_SAUDE);
        when(pacientes.existsById(1)).thenReturn(true);
        MockMultipartFile arquivo = new MockMultipartFile("arquivo", "laudo.pdf", "application/pdf",
                "conteudo incorreto".getBytes());
        assertThrows(InvalidValueException.class, () -> service().cadastrar(1,
                new DadosExame(LocalDate.now(), null, null), arquivo, profissional));
        verifyNoInteractions(exames, armazenamento);
    }

}
