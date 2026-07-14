package com.hdc.hdc.orientacao_exercicio;

import com.hdc.hdc.infra.bucket.service.R2Service;
import com.hdc.hdc.orientacao_exercicio.dto.ExercicioOrientacaoRequestDTO;
import com.hdc.hdc.orientacao_exercicio.dto.ExercicioOrientacaoResponseDTO;
import com.hdc.hdc.orientacao_exercicio.enums.CategoriaExercicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ExercicioOrientacaoService {

    private final ExercicioOrientacaoRepository repository;
    private final ExercicioOrientacaoMapper mapper;
    private final R2Service r2Service;

    @Transactional
    public ExercicioOrientacaoResponseDTO criar(ExercicioOrientacaoRequestDTO dto) {
        ExercicioOrientacao exercicio = mapper.toEntity(dto);

        if (dto.getImagem() != null && !dto.getImagem().isEmpty()) {
            try {
                String url = r2Service.upload(dto.getImagem());
                exercicio.setUrlImagem(url);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao fazer upload da imagem", e);
            }
        }

        ExercicioOrientacao salvo = repository.save(exercicio);
        return mapper.toResponseDTO(salvo);
    }

    public List<ExercicioOrientacaoResponseDTO> listar(CategoriaExercicio categoria) {
        List<ExercicioOrientacao> lista;
        if (categoria != null) {
            lista = repository.findByCategoria(categoria);
        } else {
            lista = repository.findAll();
        }
        return lista.stream()
                .map(mapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ExercicioOrientacaoResponseDTO atualizar(UUID id, ExercicioOrientacaoRequestDTO dto) {
        ExercicioOrientacao exercicio = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado."));

        exercicio.setNome(dto.getNome());
        exercicio.setFinalidade(dto.getFinalidade());
        exercicio.setCategoria(dto.getCategoria());
        exercicio.setDescricao(dto.getDescricao());

        if (dto.getImagem() != null && !dto.getImagem().isEmpty()) {
            try {
                // Deletar a antiga
                if (exercicio.getUrlImagem() != null) {
                    r2Service.delete(exercicio.getUrlImagem());
                }
                // Upload da nova
                String url = r2Service.upload(dto.getImagem());
                exercicio.setUrlImagem(url);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao processar imagem", e);
            }
        }

        ExercicioOrientacao salvo = repository.save(exercicio);
        return mapper.toResponseDTO(salvo);
    }

    @Transactional
    public void deletar(UUID id) {
        ExercicioOrientacao exercicio = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Exercício não encontrado."));

        if (exercicio.getUrlImagem() != null) {
            try {
                r2Service.delete(exercicio.getUrlImagem());
            } catch (IOException e) {
                throw new RuntimeException("Erro ao deletar imagem do bucket. Deleção abortada.", e);
            }
        }

        repository.delete(exercicio);
    }
}
