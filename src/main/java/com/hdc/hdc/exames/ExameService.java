package com.hdc.hdc.exames;

import com.hdc.hdc.pacientes.PacienteRepository;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.usuarios.enums.Role;
import com.hdc.hdc.util.exception.DeniedAccessException;
import com.hdc.hdc.util.exception.InvalidValueException;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.List;

@Service
public class ExameService {
    private final ExameRepository exames;
    private final ArquivoExameRepository arquivos;
    private final EventoExameRepository eventos;
    private final PacienteRepository pacientes;
    private final ArmazenamentoExame armazenamento;
    private final long limiteBytes;

    public ExameService(ExameRepository exames, ArquivoExameRepository arquivos,
                        EventoExameRepository eventos, PacienteRepository pacientes,
                        ArmazenamentoExame armazenamento,
                        @Value("${exames.arquivo.max-bytes:5242880}") long limiteBytes) {
        this.exames = exames;
        this.arquivos = arquivos;
        this.eventos = eventos;
        this.pacientes = pacientes;
        this.armazenamento = armazenamento;
        this.limiteBytes = limiteBytes;
    }

    @Transactional
    public ExameDTO cadastrar(Integer pacienteId, DadosExame dados, MultipartFile arquivo, Usuario autor) throws IOException {
        exigirProfissional(autor);
        if (!pacientes.existsById(pacienteId)) throw new ResourceNotFoundException("Participante não encontrado");
        validarDados(dados);
        byte[] bytes = validarPdf(arquivo);
        Instant agora = Instant.now();
        Exame exame = new Exame();
        exame.setPacienteId(pacienteId);
        exame.setProfissionalCadastroId(autor.getId());
        aplicarDados(exame, dados);
        exame.setStatus(StatusExame.RASCUNHO);
        exame.setCriadoEm(agora);
        exame.setAtualizadoEm(agora);
        exames.save(exame);
        salvarArquivo(exame, arquivo, bytes, agora);
        evento(exame, autor.getId(), "CADASTRO", null, agora);
        return dto(exame);
    }

    @Transactional
    public ExameDTO corrigir(Long id, DadosExame dados, MultipartFile arquivo, Usuario autor) throws IOException {
        exigirProfissional(autor);
        Exame exame = buscarParaAlterar(id);
        if (exame.getStatus() == StatusExame.INATIVADO) throw new InvalidValueException("status", "Exame inativado não pode ser corrigido");
        validarDados(dados);
        byte[] bytes = arquivo == null ? null : validarPdf(arquivo);
        String alteracoes = "dataColeta: " + exame.getDataColeta() + " -> " + dados.dataColeta() +
                "; laboratorio: " + exame.getLaboratorio() + " -> " + dados.laboratorio() +
                "; observacao: " + exame.getObservacao() + " -> " + dados.observacao();
        aplicarDados(exame, dados);
        Instant agora = Instant.now();
        if (bytes != null) {
            arquivos.findByExameIdAndAtivoTrue(id).ifPresent(anterior -> {
                anterior.setAtivo(false);
                arquivos.saveAndFlush(anterior);
            });
            salvarArquivo(exame, arquivo, bytes, agora);
            evento(exame, autor.getId(), "SUBSTITUICAO_ARQUIVO", null, agora);
        }
        exame.setAtualizadoEm(agora);
        evento(exame, autor.getId(), "CORRECAO", alteracoes, agora);
        return dto(exame);
    }

    @Transactional
    public ExameDTO agendar(Long id, Instant instante, Usuario autor) {
        exigirProfissional(autor);
        Exame exame = buscarParaAlterar(id);
        Instant agora = Instant.now();
        if (exame.getStatus() != StatusExame.RASCUNHO && exame.getStatus() != StatusExame.AGENDADO ||
                exame.getStatus() == StatusExame.AGENDADO && !exame.getDisponibilizacaoEm().isAfter(agora))
            throw new InvalidValueException("status", "Somente rascunho ou agendamento futuro podem ser agendados");
        if (instante == null || !instante.isAfter(agora))
            throw new InvalidValueException("disponibilizacaoEm", "Informe um horário futuro");
        exigirArquivo(exame);
        exame.setStatus(StatusExame.AGENDADO);
        exame.setDisponibilizacaoEm(instante);
        exame.setAtualizadoEm(agora);
        evento(exame, autor.getId(), "AGENDAMENTO", instante.toString(), agora);
        return dto(exame);
    }

    @Transactional
    public ExameDTO suspender(Long id, Usuario autor) {
        exigirProfissional(autor);
        Exame exame = buscarParaAlterar(id);
        if (exame.getStatus() != StatusExame.AGENDADO || !exame.getDisponibilizacaoEm().isAfter(Instant.now()))
            throw new InvalidValueException("status", "Somente agendamento futuro pode ser suspenso");
        exame.setStatus(StatusExame.RASCUNHO);
        exame.setDisponibilizacaoEm(null);
        exame.setAtualizadoEm(Instant.now());
        evento(exame, autor.getId(), "SUSPENSAO", null, exame.getAtualizadoEm());
        return dto(exame);
    }

    @Transactional
    public ExameDTO publicar(Long id, Usuario autor) {
        exigirProfissional(autor);
        Exame exame = buscarParaAlterar(id);
        if (exame.getStatus() != StatusExame.RASCUNHO && exame.getStatus() != StatusExame.AGENDADO)
            throw new InvalidValueException("status", "Exame não pode ser publicado");
        exigirArquivo(exame);
        publicarInterno(exame, autor.getId(), Instant.now());
        return dto(exame);
    }

    @Transactional
    public ExameDTO inativar(Long id, Usuario autor) {
        exigirProfissional(autor);
        Exame exame = buscarParaAlterar(id);
        if (exame.getStatus() != StatusExame.PUBLICADO &&
                !(exame.getStatus() == StatusExame.AGENDADO && !exame.getDisponibilizacaoEm().isAfter(Instant.now())))
            throw new InvalidValueException("status", "Somente exame disponibilizado pode ser inativado");
        Instant agora = Instant.now();
        exame.setStatus(StatusExame.INATIVADO);
        exame.setAtualizadoEm(agora);
        evento(exame, autor.getId(), "INATIVACAO", null, agora);
        return dto(exame);
    }

    @Transactional(readOnly = true)
    public List<ExameDTO> listar(Integer pacienteId, Usuario autor) {
        if (!pacientes.existsById(pacienteId)) throw new ResourceNotFoundException("Participante não encontrado");
        autorizarPacienteOuProfissional(pacienteId, autor);
        Instant agora = Instant.now();
        return exames.findByPacienteIdOrderByDataColetaDesc(pacienteId).stream()
                .filter(e -> autor.getRole() != Role.PACIENTE || e.disponivelParaPaciente(agora))
                .map(this::dto).toList();
    }

    @Transactional(readOnly = true)
    public ExameDTO consultar(Long id, Usuario autor) {
        return dto(buscarAutorizado(id, autor));
    }

    @Transactional(readOnly = true)
    public DownloadExame baixar(Long id, Usuario autor) {
        Exame exame = buscarAutorizado(id, autor);
        ArquivoExame arquivo = arquivos.findByExameIdAndAtivoTrue(exame.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Arquivo não encontrado"));
        return new DownloadExame(arquivo.getNomeOriginal(), armazenamento.ler(arquivo.getChaveArmazenamento()));
    }

    @Transactional(readOnly = true)
    public List<EventoExame> historico(Long id, Usuario autor) {
        exigirProfissional(autor);
        if (!exames.existsById(id)) throw new ResourceNotFoundException("Exame não encontrado");
        return eventos.findByExameIdOrderByOcorridoEmAsc(id);
    }

    @Transactional
    public void publicarAgendado(Long id) {
        Exame exame = buscarParaAlterar(id);
        Instant agora = Instant.now();
        if (exame.getStatus() == StatusExame.AGENDADO && !exame.getDisponibilizacaoEm().isAfter(agora))
            publicarInterno(exame, null, agora);
    }

    private void publicarInterno(Exame exame, Integer autorId, Instant agora) {
        exame.setStatus(StatusExame.PUBLICADO);
        exame.setDisponibilizacaoEm(agora);
        exame.setPublicadoEm(agora);
        exame.setAtualizadoEm(agora);
        evento(exame, autorId, "PUBLICACAO", null, agora);
    }

    private Exame buscarParaAlterar(Long id) {
        return exames.findWithLockById(id).orElseThrow(() -> new ResourceNotFoundException("Exame não encontrado"));
    }

    private Exame buscarAutorizado(Long id, Usuario autor) {
        Exame exame = exames.findById(id).orElseThrow(() -> new ResourceNotFoundException("Exame não encontrado"));
        autorizarPacienteOuProfissional(exame.getPacienteId(), autor);
        if (autor.getRole() == Role.PACIENTE && !exame.disponivelParaPaciente(Instant.now()))
            throw new ResourceNotFoundException("Exame não encontrado");
        return exame;
    }

    private void autorizarPacienteOuProfissional(Integer pacienteId, Usuario autor) {
        if (autor == null || !(autor.getRole() == Role.ADMIN || autor.getRole() == Role.PROFISSIONAL_DA_SAUDE ||
                autor.getRole() == Role.PACIENTE && autor.getId().equals(pacienteId)))
            throw new DeniedAccessException("exame");
    }

    private void exigirProfissional(Usuario autor) {
        if (autor == null || autor.getRole() != Role.ADMIN && autor.getRole() != Role.PROFISSIONAL_DA_SAUDE)
            throw new DeniedAccessException("exame");
    }

    private void exigirArquivo(Exame exame) {
        if (exame.getDataColeta() == null || arquivos.findByExameIdAndAtivoTrue(exame.getId()).isEmpty())
            throw new InvalidValueException("arquivo", "PDF e data da coleta são obrigatórios para disponibilizar");
    }

    private void validarDados(DadosExame dados) {
        if (dados == null || dados.dataColeta() == null)
            throw new InvalidValueException("dataColeta", "Data da coleta é obrigatória");
    }

    private void aplicarDados(Exame exame, DadosExame dados) {
        exame.setDataColeta(dados.dataColeta());
        exame.setLaboratorio(dados.laboratorio());
        exame.setObservacao(dados.observacao());
    }

    private byte[] validarPdf(MultipartFile arquivo) throws IOException {
        if (arquivo == null || arquivo.isEmpty() || arquivo.getSize() > limiteBytes)
            throw new InvalidValueException("arquivo", "PDF vazio ou acima do limite configurado");
        String nome = arquivo.getOriginalFilename();
        if (nome == null || nome.length() > 255 || !nome.toLowerCase(java.util.Locale.ROOT).endsWith(".pdf") ||
                !"application/pdf".equalsIgnoreCase(arquivo.getContentType()))
            throw new InvalidValueException("arquivo", "Envie um arquivo PDF");
        byte[] bytes = arquivo.getBytes();
        if (bytes.length > limiteBytes || bytes.length < 8 ||
                !(bytes[0] == '%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F' && bytes[4] == '-'))
            throw new InvalidValueException("arquivo", "Conteúdo PDF inválido");
        String fim = new String(bytes, Math.max(0, bytes.length - 1024), Math.min(bytes.length, 1024),
                java.nio.charset.StandardCharsets.ISO_8859_1);
        if (!fim.contains("%%EOF")) throw new InvalidValueException("arquivo", "Conteúdo PDF incompleto");
        return bytes;
    }

    private void salvarArquivo(Exame exame, MultipartFile arquivo, byte[] bytes, Instant agora) {
        String chave = armazenamento.gravar(bytes);
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override public void afterCompletion(int status) {
                if (status != STATUS_COMMITTED) armazenamento.apagar(chave);
            }
        });
        ArquivoExame entidade = new ArquivoExame();
        entidade.setExameId(exame.getId());
        entidade.setNomeOriginal(arquivo.getOriginalFilename().replace('\\', '/').replaceAll("^.*/", ""));
        entidade.setChaveArmazenamento(chave);
        entidade.setMimeType("application/pdf");
        entidade.setTamanhoBytes((long) bytes.length);
        try {
            entidade.setChecksum(HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes)));
        } catch (NoSuchAlgorithmException e) { throw new IllegalStateException(e); }
        entidade.setEnviadoEm(agora);
        entidade.setAtivo(true);
        arquivos.save(entidade);
    }

    private void evento(Exame exame, Integer autorId, String acao, String detalhes, Instant agora) {
        EventoExame evento = new EventoExame();
        evento.setExameId(exame.getId());
        evento.setAutorId(autorId);
        evento.setAcao(acao);
        evento.setDetalhes(detalhes);
        evento.setOcorridoEm(agora);
        eventos.save(evento);
    }

    private ExameDTO dto(Exame exame) {
        ArquivoExame arquivo = arquivos.findByExameIdAndAtivoTrue(exame.getId()).orElse(null);
        StatusExame status = exame.getStatus() == StatusExame.AGENDADO && exame.disponivelParaPaciente(Instant.now())
                ? StatusExame.PUBLICADO : exame.getStatus();
        return new ExameDTO(exame.getId(), exame.getPacienteId(), exame.getProfissionalCadastroId(),
                exame.getDataColeta(), exame.getLaboratorio(), exame.getObservacao(), status,
                exame.getDisponibilizacaoEm(), exame.getPublicadoEm(), exame.getCriadoEm(), exame.getAtualizadoEm(),
                arquivo == null ? null : arquivo.getNomeOriginal(), arquivo == null ? null : arquivo.getTamanhoBytes());
    }

    public record DownloadExame(String nome, byte[] conteudo) {}
}
