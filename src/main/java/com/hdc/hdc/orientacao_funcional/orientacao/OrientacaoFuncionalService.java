package com.hdc.hdc.orientacao_funcional.orientacao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdc.hdc.infra.bucket.service.R2Service;
import com.hdc.hdc.orientacao_funcional.categoria.CategoriaOrientacaoRepository;
import com.hdc.hdc.orientacao_funcional.orientacao.dto.*;
import com.hdc.hdc.orientacao_funcional.tag.TagFuncionalRepository;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.util.exception.InvalidValueException;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrientacaoFuncionalService {
    private final R2Service r2Service;
    private final TagFuncionalRepository tagFuncionalRepository;
    private final OrientacaoFuncionalRepository repository;
    private final CategoriaOrientacaoRepository categorias;
    private final HistoricoOrientacaoRepository historico;
    private final OrientacaoFuncionalMapper mapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public OrientacaoFuncionalResponseDTO criarOrientacao(OrientacaoFuncionalRequestDTO dto, MultipartFile imagem, Usuario usuario) throws IOException {
        OrientacaoFuncional o = new OrientacaoFuncional();
        o.setResponsavel(usuario);
        o.setDataCriacao(LocalDateTime.now());
        aplicar(o, dto);
        if (imagem != null && !imagem.isEmpty()) adicionarArquivo(o, imagem, new MidiaRequestDTO(null, 0, null, null, true));
        return mapper.toDto(repository.saveAndFlush(o));
    }

    @Transactional
    public OrientacaoFuncionalResponseDTO atualizarOrientacao(Long id, OrientacaoFuncionalRequestDTO dto, MultipartFile imagem) throws IOException {
        OrientacaoFuncional o = buscarEntidade(id);
        registrarHistorico(o, "EDICAO");
        aplicar(o, dto);
        if (imagem != null && !imagem.isEmpty()) adicionarArquivo(o, imagem, new MidiaRequestDTO(null, o.getMidias().size(), null, null, true));
        return mapper.toDto(repository.saveAndFlush(o));
    }

    private void aplicar(OrientacaoFuncional o, OrientacaoFuncionalRequestDTO dto) {
        o.setNome(dto.nome() == null || dto.nome().isBlank() ? "Orientação sem título" : dto.nome().trim());
        o.setResumo(dto.resumo() != null ? dto.resumo() : dto.descricao());
        o.setDescricao(o.getResumo()); // Compatibilidade com clientes anteriores.
        o.setFinalidade(dto.finalidade());
        o.setMateriais(dto.materiais());
        o.setDuracaoEstimada(dto.duracaoEstimada());
        o.setCategoria(dto.categoriaId() == null ? null : categorias.findById(dto.categoriaId())
                .filter(c -> c.isAtivo()).orElseThrow(() -> invalido("categoria", "Selecione uma categoria ativa.")));
        var ids = dto.tagsIds() == null ? List.<Long>of() : dto.tagsIds().stream().distinct().toList();
        var tags = tagFuncionalRepository.findAllById(ids);
        if (tags.size() != ids.size() || tags.stream().anyMatch(t -> !t.isAtivo()))
            throw invalido("tags", "Selecione apenas tags existentes e ativas.");
        o.setTags(new ArrayList<>(tags));
        Map<Long, PassoOrientacao> existentes = new HashMap<>();
        o.getPassos().forEach(p -> existentes.put(p.getId(), p));
        List<PassoOrientacao> passos = new ArrayList<>();
        Set<Long> usados = new HashSet<>();
        for (PassoDTO p : dto.passos() == null ? List.<PassoDTO>of() : dto.passos()) {
            PassoOrientacao passo = p.id() == null ? new PassoOrientacao() : existentes.get(p.id());
            if (passo == null || (p.id() != null && !usados.add(p.id())))
                throw invalido("passos", "Passo inválido ou repetido nesta orientação.");
            passo.setOrientacao(o);
            passo.setOrdem(passos.size());
            passo.setInstrucao(p.instrucao());
            passos.add(passo);
        }
        o.getMidias().stream().filter(m -> m.getPassoId() != null && !usados.contains(m.getPassoId())).forEach(m -> m.setPassoId(null));
        o.getPassos().removeIf(p -> !passos.contains(p));
        passos.stream().filter(p -> !o.getPassos().contains(p)).forEach(o.getPassos()::add);
        o.getPassos().sort(Comparator.comparingInt(PassoOrientacao::getOrdem));
        o.getCuidados().clear();
        for (CuidadoDTO c : dto.cuidados() == null ? List.<CuidadoDTO>of() : dto.cuidados()) {
            CuidadoOrientacao cuidado = new CuidadoOrientacao();
            cuidado.setOrientacao(o); cuidado.setTipo(c.tipo()); cuidado.setTexto(c.texto());
            cuidado.setOrdem(o.getCuidados().size()); o.getCuidados().add(cuidado);
        }
        rascunho(o);
    }

    @Transactional
    public OrientacaoFuncionalResponseDTO publicar(Long id, boolean revisado) {
        OrientacaoFuncional o = buscarEntidade(id);
        if (!revisado) throw invalido("revisao", "Confirme a revisão profissional do conteúdo e dos cuidados.");
        if (vazio(o.getNome()) || o.getNome().equals("Orientação sem título") || vazio(o.getResumo()) || o.getCategoria() == null || !o.getCategoria().isAtivo())
            throw invalido("conteudo", "Informe nome, resumo e categoria ativa antes de publicar.");
        if (o.getPassos().isEmpty() || o.getPassos().stream().anyMatch(p -> vazio(p.getInstrucao())))
            throw invalido("passos", "Informe as instruções de todos os passos.");
        for (TipoCuidado tipo : TipoCuidado.values()) {
            var itens = o.getCuidados().stream().filter(c -> c.getTipo() == tipo).toList();
            if (itens.isEmpty() || itens.stream().anyMatch(c -> vazio(c.getTexto())))
                throw invalido("cuidados", "Preencha e revise os três grupos de cuidados antes de publicar.");
        }
        if (o.getMidias().stream().noneMatch(MidiaOrientacao::isCapa))
            throw invalido("capa", "Selecione uma imagem de capa antes de publicar.");
        if (o.getTags().stream().anyMatch(t -> !t.isAtivo())) throw invalido("tags", "Remova as tags inativas antes de publicar.");
        registrarHistorico(o, "PUBLICACAO");
        o.setRevisadoPor(usuarioAtual()); o.setCuidadosRevisados(true);
        o.setStatus(StatusOrientacao.PUBLICADA); o.setAtivo(true);
        o.setPublicadoEm(LocalDateTime.now()); o.setDataAtualizacao(LocalDateTime.now());
        return mapper.toDto(repository.saveAndFlush(o));
    }

    @Transactional
    public OrientacaoFuncionalResponseDTO alterarStatus(Long id, Boolean ativo) {
        if (Boolean.TRUE.equals(ativo)) throw invalido("status", "Utilize a revisão e publicação para disponibilizar a orientação.");
        OrientacaoFuncional o = buscarEntidade(id);
        registrarHistorico(o, "INATIVACAO");
        o.setStatus(StatusOrientacao.INATIVA); o.setAtivo(false); o.setDataAtualizacao(LocalDateTime.now());
        return mapper.toDto(repository.saveAndFlush(o));
    }

    @Transactional
    public void deletar(Long id) { alterarStatus(id, false); }

    public Page<OrientacaoFuncionalResponseDTO> listar(boolean participante, int pagina, int tamanho, Long categoriaId, Long tagId) {
        if (pagina < 0 || tamanho < 1 || tamanho > 100) throw invalido("paginacao", "Página deve ser não negativa e tamanho entre 1 e 100.");
        Specification<OrientacaoFuncional> filtro = (root, query, cb) -> {
            var predicados = new ArrayList<jakarta.persistence.criteria.Predicate>();
            if (participante) predicados.add(cb.equal(root.get("status"), StatusOrientacao.PUBLICADA));
            if (categoriaId != null) predicados.add(cb.equal(root.get("categoria").get("id"), categoriaId));
            if (tagId != null) { query.distinct(true); predicados.add(cb.equal(root.join("tags").get("id"), tagId)); }
            return cb.and(predicados.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
        return repository.findAll(filtro, PageRequest.of(pagina, tamanho, Sort.by(Sort.Direction.DESC, "dataCriacao"))).map(mapper::toDto);
    }

    public OrientacaoFuncionalResponseDTO buscarPorId(Long id) {
        OrientacaoFuncional o = buscarEntidade(id);
        if (usuarioAtual().getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_PACIENTE")) && o.getStatus() != StatusOrientacao.PUBLICADA)
            throw new ResourceNotFoundException("orientacao", "Orientação não encontrada no catálogo publicado.");
        return mapper.toDto(o);
    }

    public List<HistoricoOrientacao> historico(Long id) {
        buscarEntidade(id);
        return historico.findByOrientacaoIdOrderByDataAlteracaoDesc(id);
    }

    @Transactional
    public OrientacaoFuncionalResponseDTO adicionarMidia(Long id, MultipartFile arquivo, MidiaRequestDTO dto) throws IOException {
        OrientacaoFuncional o = buscarEntidade(id);
        registrarHistorico(o, "ADICAO_MIDIA");
        adicionarArquivo(o, arquivo, dto); rascunho(o);
        return mapper.toDto(repository.saveAndFlush(o));
    }

    @Transactional
    public OrientacaoFuncionalResponseDTO atualizarMidia(Long id, Long midiaId, MidiaRequestDTO dto) {
        OrientacaoFuncional o = buscarEntidade(id);
        MidiaOrientacao midia = midia(o, midiaId);
        registrarHistorico(o, "EDICAO_MIDIA");
        aplicarMidia(o, midia, dto); rascunho(o);
        return mapper.toDto(repository.saveAndFlush(o));
    }

    @Transactional
    public OrientacaoFuncionalResponseDTO removerMidia(Long id, Long midiaId) {
        OrientacaoFuncional o = buscarEntidade(id);
        MidiaOrientacao midia = midia(o, midiaId);
        registrarHistorico(o, "REMOCAO_MIDIA");
        o.getMidias().remove(midia);
        o.setUrlImagem(o.getMidias().stream().filter(MidiaOrientacao::isCapa).map(MidiaOrientacao::getUrl).findFirst().orElse(null));
        rascunho(o);
        // Arquivos anteriores são retidos no armazenamento para preservar o histórico.
        return mapper.toDto(repository.saveAndFlush(o));
    }

    private void adicionarArquivo(OrientacaoFuncional o, MultipartFile arquivo, MidiaRequestDTO dto) throws IOException {
        if (o.getMidias().size() >= 10) throw invalido("midias", "São permitidas até 10 imagens por orientação.");
        validarImagem(arquivo);
        validarPasso(o, dto.passoId());
        String url = r2Service.upload(arquivo);
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override public void afterCompletion(int status) {
                    if (status != STATUS_COMMITTED) { try { r2Service.delete(url); } catch (IOException ignored) { /* Limpeza posterior no armazenamento. */ } }
                }
            });
        }
        MidiaOrientacao midia = new MidiaOrientacao(); midia.setOrientacao(o); midia.setUrl(url);
        o.getMidias().add(midia); aplicarMidia(o, midia, dto);
    }

    private void aplicarMidia(OrientacaoFuncional o, MidiaOrientacao m, MidiaRequestDTO dto) {
        validarPasso(o, dto.passoId());
        if (dto.capa()) o.getMidias().forEach(item -> item.setCapa(false));
        m.setCapa(dto.capa()); m.setLegenda(dto.legenda()); m.setTextoAlternativo(dto.textoAlternativo()); m.setPassoId(dto.passoId());
        o.getMidias().remove(m);
        o.getMidias().add(Math.min(dto.ordem(), o.getMidias().size()), m);
        for (int i = 0; i < o.getMidias().size(); i++) o.getMidias().get(i).setOrdem(i);
        o.setUrlImagem(o.getMidias().stream().filter(MidiaOrientacao::isCapa).map(MidiaOrientacao::getUrl).findFirst().orElse(null));
    }

    private void validarPasso(OrientacaoFuncional o, Long passoId) {
        if (passoId != null && o.getPassos().stream().noneMatch(p -> passoId.equals(p.getId())))
            throw invalido("passoId", "A imagem deve pertencer a um passo desta orientação.");
    }

    private void validarImagem(MultipartFile arquivo) throws IOException {
        if (arquivo == null || arquivo.isEmpty() || arquivo.getSize() > 5 * 1024 * 1024)
            throw invalido("imagem", "Envie uma imagem PNG ou JPEG com até 5 MB.");
        if (!Set.of("image/png", "image/jpeg").contains(Objects.toString(arquivo.getContentType(), "")))
            throw invalido("imagem", "Apenas imagens PNG e JPEG são permitidas.");
        try (var input = ImageIO.createImageInputStream(arquivo.getInputStream())) {
            var readers = ImageIO.getImageReaders(input);
            if (!readers.hasNext()) throw invalido("imagem", "Arquivo de imagem inválido.");
            var reader = readers.next();
            try {
                reader.setInput(input);
                String formato = reader.getFormatName().toLowerCase(Locale.ROOT);
                if (!Set.of("png", "jpeg").contains(formato) || !arquivo.getContentType().equals("image/" + formato)
                        || (long) reader.getWidth(0) * reader.getHeight(0) > 20_000_000)
                    throw invalido("imagem", "Formato inválido ou imagem acima de 20 megapixels.");
                reader.read(0); // Verifica também os dados da imagem, após limitar suas dimensões.
            } finally { reader.dispose(); }
        } catch (IOException ex) {
            throw invalido("imagem", "Arquivo de imagem inválido ou incompleto.");
        }
    }

    private void rascunho(OrientacaoFuncional o) {
        o.setStatus(StatusOrientacao.RASCUNHO); o.setAtivo(false); o.setCuidadosRevisados(false); o.setRevisadoPor(null);
        o.setDataAtualizacao(LocalDateTime.now());
    }
    private void registrarHistorico(OrientacaoFuncional o, String acao) {
        HistoricoOrientacao h = new HistoricoOrientacao();
        h.setOrientacaoId(o.getId()); h.setAutorId(usuarioAtual().getId()); h.setAcao(acao); h.setDataAlteracao(LocalDateTime.now());
        try { h.setConteudoAnterior(objectMapper.writeValueAsString(mapper.toDto(o))); }
        catch (JsonProcessingException ex) { throw new IllegalStateException("Não foi possível registrar o histórico.", ex); }
        historico.save(h);
    }
    private Usuario usuarioAtual() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Usuario usuario)) throw new AccessDeniedException("Autenticação necessária.");
        return usuario;
    }
    private OrientacaoFuncional buscarEntidade(Long id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("orientacao", "Orientação funcional não encontrada."));
    }
    private MidiaOrientacao midia(OrientacaoFuncional o, Long id) {
        return o.getMidias().stream().filter(m -> id.equals(m.getId())).findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("midia", "Imagem não encontrada nesta orientação."));
    }
    private boolean vazio(String s) { return s == null || s.isBlank(); }
    private InvalidValueException invalido(String campo, String mensagem) { return new InvalidValueException(campo, mensagem); }
}
