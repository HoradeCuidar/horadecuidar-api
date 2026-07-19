package com.hdc.hdc.orientacao_funcional.orientacao;

import com.hdc.hdc.infra.bucket.service.R2Service;
import com.hdc.hdc.orientacao_funcional.orientacao.dto.OrientacaoFuncionalRequestDTO;
import com.hdc.hdc.orientacao_funcional.orientacao.dto.OrientacaoFuncionalResponseDTO;
import com.hdc.hdc.orientacao_funcional.registro.RealizacaoExercicioRepository;
import com.hdc.hdc.orientacao_funcional.tag.TagFuncional;
import com.hdc.hdc.orientacao_funcional.tag.TagFuncionalRepository;
import com.hdc.hdc.usuarios.Usuario;
import com.hdc.hdc.util.exception.EntityInUseException;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrientacaoFuncionalService {

    private final R2Service r2Service;
    private final TagFuncionalRepository tagFuncionalRepository;
    private final OrientacaoFuncionalRepository orientacaoFuncionalRepository;
    private final RealizacaoExercicioRepository realizacaoExercicioRepository;
    private final OrientacaoFuncionalMapper mapper;

    @Transactional
    public OrientacaoFuncionalResponseDTO criarOrientacao(OrientacaoFuncionalRequestDTO requestDTO, MultipartFile imagem, Usuario usuario) throws IOException {
        OrientacaoFuncional orientacao = mapper.toEntity(requestDTO);
        orientacao.setResponsavel(usuario);
        orientacao.setAtivo(true);
        orientacao.setDataCriacao(LocalDateTime.now());

        if (imagem != null && !imagem.isEmpty()) {
            try {
                String url = r2Service.upload(imagem);
                orientacao.setUrlImagem(url);
            } catch (IOException e) {
                throw new IOException("Erro ao fazer upload da imagem", e);
            }
        }

        if (requestDTO.tagsIds() != null && !requestDTO.tagsIds().isEmpty()) {
            List<TagFuncional> tags = tagFuncionalRepository.findAllById(requestDTO.tagsIds());
            orientacao.setTags(tags);
        }

        OrientacaoFuncional salva = orientacaoFuncionalRepository.save(orientacao);
        return mapper.toDto(salva);
    }

    @Transactional
    public OrientacaoFuncionalResponseDTO atualizarOrientacao(Long orientacaoId, OrientacaoFuncionalRequestDTO requestDTO, MultipartFile imagem) throws IOException {
        OrientacaoFuncional orientacao = buscarEntidadePorId(orientacaoId);

        mapper.updateEntityFromDto(requestDTO, orientacao);
        orientacao.setDataAtualizacao(LocalDateTime.now());

        if (imagem != null && !imagem.isEmpty()) {
            try {
                String url = r2Service.upload(imagem);
                orientacao.setUrlImagem(url);
            } catch (IOException e) {
                throw new IOException("Erro ao fazer upload da imagem", e);
            }
        }

        if (requestDTO.tagsIds() != null) {
            List<TagFuncional> tags = tagFuncionalRepository.findAllById(requestDTO.tagsIds());
            orientacao.setTags(tags);
        }

        return mapper.toDto(orientacaoFuncionalRepository.save(orientacao));
    }

    @Transactional
    public OrientacaoFuncionalResponseDTO alterarStatus(Long orientacaoId, Boolean ativo) {
        OrientacaoFuncional orientacao = buscarEntidadePorId(orientacaoId);
        orientacao.setAtivo(ativo);
        orientacao.setDataAtualizacao(LocalDateTime.now());
        return mapper.toDto(orientacaoFuncionalRepository.save(orientacao));
    }

    /**
     * Listagem para pacientes: retorna somente orientações ativas.
     */
    public Page<OrientacaoFuncionalResponseDTO> listarParaPaciente(Integer numberPage, Integer pageSize) {
        Pageable pageable = PageRequest.of(numberPage, pageSize, Sort.by(Sort.Direction.DESC, "dataCriacao"));
        return orientacaoFuncionalRepository.findAllByAtivoTrue(pageable).map(mapper::toDto);
    }

    /**
     * Listagem para profissionais/admin: retorna todas as orientações, incluindo inativas.
     */
    public Page<OrientacaoFuncionalResponseDTO> listarParaProfissional(Integer numberPage, Integer pageSize) {
        Pageable pageable = PageRequest.of(numberPage, pageSize, Sort.by(Sort.Direction.DESC, "dataCriacao"));
        return orientacaoFuncionalRepository.findAll(pageable).map(mapper::toDto);
    }

    public OrientacaoFuncionalResponseDTO buscarPorId(Long id) {
        return mapper.toDto(buscarEntidadePorId(id));
    }

    @Transactional
    public void deletar(Long id) {
        try {
            orientacaoFuncionalRepository.deleteById(id);
            orientacaoFuncionalRepository.flush();
        } catch (DataIntegrityViolationException ex) {log.info("Orientação funcional não pode ser deletada - integridade referencial");
            throw new EntityInUseException("Orientação Funcional");
        }
    }

    private OrientacaoFuncional buscarEntidadePorId(Long id) {
        return orientacaoFuncionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("orientacaoFuncional", "Orientação funcional não encontrada."));
    }
}
