package com.hdc.hdc.prescricao_nutricional.modelo;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdc.hdc.prescricao_nutricional.refeicao.dto.RefeicaoCreateDTO;
import com.hdc.hdc.util.exception.InvalidValueException;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ModeloNutricionalService {
    private final ModeloNutricionalRepository repository;
    private final ObjectMapper objectMapper;

    public List<ModeloNutricionalResumoDTO> listar() {
        return repository.findAllByOrderByNomeAsc().stream().map(this::resumo).toList();
    }

    public ModeloNutricionalResponseDTO buscar(Integer id) {
        return resposta(obter(id));
    }

    @Transactional
    public ModeloNutricionalResponseDTO criar(ModeloNutricionalRequestDTO dto, Integer profissionalId) {
        ModeloNutricional model = new ModeloNutricional();
        model.setCriadoPorId(profissionalId);
        model.setCriadoEm(Instant.now());
        model.setVersao(1);
        aplicar(model, dto);
        return resposta(repository.save(model));
    }

    @Transactional
    public ModeloNutricionalResponseDTO atualizar(Integer id, ModeloNutricionalRequestDTO dto) {
        ModeloNutricional model = obter(id);
        if (dto.getVersao() == null || !dto.getVersao().equals(model.getVersao())) {
            throw new InvalidValueException("O modelo foi alterado por outro profissional. Reabra-o antes de salvar.");
        }
        aplicar(model, dto);
        model.setVersao(model.getVersao() + 1);
        return resposta(repository.save(model));
    }

    @Transactional
    public ModeloNutricionalResponseDTO duplicar(Integer id, Integer profissionalId) {
        ModeloNutricional origem = obter(id);
        ModeloNutricional copia = new ModeloNutricional();
        copia.setNome("Cópia de " + origem.getNome());
        if (copia.getNome().length() > 160) copia.setNome(copia.getNome().substring(0, 160));
        copia.setReferenciaKcal(origem.getReferenciaKcal());
        copia.setDescricao(origem.getDescricao());
        copia.setRefeicoesJson(origem.getRefeicoesJson());
        copia.setCriadoPorId(profissionalId);
        copia.setCriadoEm(Instant.now());
        copia.setAtualizadoEm(copia.getCriadoEm());
        copia.setVersao(1);
        return resposta(repository.save(copia));
    }

    @Transactional
    public void excluir(Integer id) {
        repository.delete(obter(id));
    }

    private ModeloNutricional obter(Integer id) {
        return repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Modelo nutricional não encontrado."));
    }

    private void aplicar(ModeloNutricional model, ModeloNutricionalRequestDTO dto) {
        if (dto.getRefeicoes() == null || dto.getRefeicoes().isEmpty()) {
            throw new InvalidValueException("O modelo deve conter pelo menos uma refeição.");
        }
        model.setNome(dto.getNome().trim());
        model.setReferenciaKcal(dto.getReferenciaKcal());
        model.setDescricao(dto.getDescricao());
        try {
            model.setRefeicoesJson(objectMapper.writeValueAsString(dto.getRefeicoes()));
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Não foi possível serializar as refeições do modelo.", e);
        }
        model.setAtualizadoEm(Instant.now());
    }

    private List<RefeicaoCreateDTO> refeicoes(ModeloNutricional model) {
        try {
            return objectMapper.readValue(model.getRefeicoesJson(), new TypeReference<>() {});
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Conteúdo do modelo nutricional inválido.", e);
        }
    }

    private ModeloNutricionalResponseDTO resposta(ModeloNutricional model) {
        ModeloNutricionalResponseDTO dto = new ModeloNutricionalResponseDTO();
        dto.setId(model.getId()); dto.setNome(model.getNome()); dto.setDescricao(model.getDescricao());
        dto.setReferenciaKcal(model.getReferenciaKcal()); dto.setVersao(model.getVersao());
        dto.setCriadoPorId(model.getCriadoPorId()); dto.setCriadoEm(model.getCriadoEm());
        dto.setAtualizadoEm(model.getAtualizadoEm()); dto.setRefeicoes(refeicoes(model));
        return dto;
    }

    private ModeloNutricionalResumoDTO resumo(ModeloNutricional model) {
        ModeloNutricionalResumoDTO dto = new ModeloNutricionalResumoDTO();
        dto.setId(model.getId()); dto.setNome(model.getNome()); dto.setDescricao(model.getDescricao());
        dto.setReferenciaKcal(model.getReferenciaKcal()); dto.setVersao(model.getVersao());
        dto.setAtualizadoEm(model.getAtualizadoEm()); dto.setQuantidadeRefeicoes(refeicoes(model).size());
        return dto;
    }
}
