package com.hdc.hdc.prescricao_nutricional.profissional;

import com.hdc.hdc.prescricao_nutricional.PrescricaoNutricional;
import com.hdc.hdc.prescricao_nutricional.PrescricaoNutricionalMapper;
import com.hdc.hdc.prescricao_nutricional.PrescricaoNutricionalRepository;
import com.hdc.hdc.prescricao_nutricional.adesao_refeicoes.OcorrenciaRefeicaoService;
import com.hdc.hdc.prescricao_nutricional.adesao_refeicoes.OcorrenciaRefeicaoRepository;
import com.hdc.hdc.prescricao_nutricional.alimento.substituicao.SubstituicaoAlimentar;
import com.hdc.hdc.prescricao_nutricional.alimento.AlimentoPrescrito;
import com.hdc.hdc.prescricao_nutricional.dto.PrescricaoNutricionalResponseDTO;
import com.hdc.hdc.prescricao_nutricional.dto.PrescricaoNutricionalResumoDTO;
import com.hdc.hdc.prescricao_nutricional.enums.StatusPrescricao;
import com.hdc.hdc.prescricao_nutricional.refeicao.Refeicao;
import com.hdc.hdc.prescricao_nutricional.refeicao.opcao.OpcaoRefeicao;
import com.hdc.hdc.util.exception.InvalidValueException;
import com.hdc.hdc.util.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Service
public class PrescricaoNutricionalProfissionalService {

    private final PrescricaoNutricionalRepository prescricaoNutricionalRepository;
    private final PrescricaoNutricionalMapper prescricaoNutricionalMapper;
    private final OcorrenciaRefeicaoService ocorrenciaRefeicaoService;
    private final OcorrenciaRefeicaoRepository ocorrenciaRefeicaoRepository;

    @Autowired
    PrescricaoNutricionalProfissionalService(PrescricaoNutricionalRepository prescricaoNutricionalRepository,
                                             PrescricaoNutricionalMapper prescricaoNutricionalMapper,
                                             OcorrenciaRefeicaoService ocorrenciaRefeicaoService,
                                             OcorrenciaRefeicaoRepository ocorrenciaRefeicaoRepository){
        this.prescricaoNutricionalRepository = prescricaoNutricionalRepository;
        this.prescricaoNutricionalMapper = prescricaoNutricionalMapper;
        this.ocorrenciaRefeicaoService = ocorrenciaRefeicaoService;
        this.ocorrenciaRefeicaoRepository = ocorrenciaRefeicaoRepository;
    }

    @Transactional
    public PrescricaoNutricionalResponseDTO cadastrar(PrescricaoNutricional prescricaoNutricional) {

        if (prescricaoNutricional.getDataInicio().isAfter(prescricaoNutricional.getDataFim())) {
            throw new InvalidValueException(
                    "A data de início não pode ser posterior à data final."
            );
        }

        if (prescricaoNutricionalRepository.existsByPacienteIdAndStatus(
                prescricaoNutricional.getPacienteId(), StatusPrescricao.ATIVA)) {
            throw new InvalidValueException(
                    "Já existe uma prescrição nutricional ativa para este paciente."
            );
        }

        vincularEntidades(prescricaoNutricional);

        prescricaoNutricional.setStatus(StatusPrescricao.ATIVA);

        PrescricaoNutricional prescricaoSalva = prescricaoNutricionalRepository.save(prescricaoNutricional);
        prescricaoNutricionalRepository.flush();

        ocorrenciaRefeicaoService.gerarOcorrencias(prescricaoSalva);

        return prescricaoNutricionalMapper.modeltoResponseDTO(prescricaoSalva);
    }

    @Transactional()
    public PrescricaoNutricionalResponseDTO visualizar(Integer id_prescricao){
        return prescricaoNutricionalMapper.modeltoResponseDTO(
                prescricaoNutricionalRepository.findById(id_prescricao)
                .orElseThrow(() -> new ResourceNotFoundException("Prescrição não encontrada"))
                );
    }

    public List<PrescricaoNutricionalResumoDTO> visualizarTodos(Integer id_paciente){
        return prescricaoNutricionalMapper.modeltoResumoDTO(
                prescricaoNutricionalRepository.findAllByPacienteId(id_paciente));
    }

    @Transactional
    public PrescricaoNutricionalResumoDTO ativarPrescricao(Integer id_prescricao){
        PrescricaoNutricional prescricaoNutricional = prescricaoNutricionalRepository.findById(id_prescricao)
                .orElseThrow(() -> new ResourceNotFoundException("Prescrição não encontrada"));

        if (prescricaoNutricionalRepository.existsByPacienteIdAndStatus(
                prescricaoNutricional.getPacienteId(), StatusPrescricao.ATIVA)) {
            throw new InvalidValueException(
                    "Já existe uma prescrição nutricional ativa para este paciente."
            );
        }

        prescricaoNutricional.setStatus(StatusPrescricao.ATIVA);
        PrescricaoNutricional salva = prescricaoNutricionalRepository.save(prescricaoNutricional);

        ocorrenciaRefeicaoService.reativarOcorrenciasFuturas(
                salva, LocalDate.now(ZoneId.systemDefault()));

        return prescricaoNutricionalMapper.modeltoResumoDTO(salva);
    }

    @Transactional
    public PrescricaoNutricionalResumoDTO inativarPrescricao(Integer id_prescricao){
        PrescricaoNutricional prescricaoNutricional = prescricaoNutricionalRepository.findById(id_prescricao)
                .orElseThrow(() -> new ResourceNotFoundException("Prescrição não encontrada"));

        prescricaoNutricional.setStatus(StatusPrescricao.INATIVA);
        PrescricaoNutricional salva = prescricaoNutricionalRepository.save(prescricaoNutricional);

        ocorrenciaRefeicaoService.cancelarOcorrenciasFuturas(
                salva, LocalDate.now(ZoneId.systemDefault()));

        return prescricaoNutricionalMapper.modeltoResumoDTO(salva);
    }

    @Transactional
    public PrescricaoNutricionalResponseDTO editar(Integer id, PrescricaoNutricional proposta) {
        PrescricaoNutricional atual = prescricaoNutricionalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Prescrição nutricional não encontrada"));
        LocalDate hoje = LocalDate.now(ZoneId.systemDefault());
        if (atual.getStatus() == StatusPrescricao.ENCERRADA || !atual.getDataInicio().isAfter(hoje)) {
            throw new InvalidValueException("Prescrição iniciada ou encerrada não pode ser editada. Emita uma nova prescrição para alterações clínicas.");
        }
        if (proposta.getDataFim().isBefore(atual.getDataInicio())) {
            throw new InvalidValueException("A data final não pode preceder o início da prescrição.");
        }
        if (proposta.getRefeicoes() == null || proposta.getRefeicoes().isEmpty()) {
            throw new InvalidValueException("A prescrição deve ter refeições.");
        }
        // Só há ocorrências futuras pendentes/canceladas: excluí-las antes de retirar
        // refeições/opções referenciadas por FK; recriá-las depois da sincronização.
        if (ocorrenciaRefeicaoRepository.findByPrescricaoId(id).stream()
                .anyMatch(o -> o.getStatus() == com.hdc.hdc.prescricao_nutricional.enums.StatusAdesao.REALIZADO
                        || o.getStatus() == com.hdc.hdc.prescricao_nutricional.enums.StatusAdesao.NAO_REALIZADO)) {
            throw new InvalidValueException("Existem registros de adesão; a prescrição não pode ser editada.");
        }
        // Validar IDs e pertencimento antes de qualquer exclusão.
        validarIdentificadores(atual, proposta);
        ocorrenciaRefeicaoRepository.deleteByPrescricaoId(id);
        ocorrenciaRefeicaoRepository.flush();
        atual.setDataFim(proposta.getDataFim());
        atual.setObservacoes(proposta.getObservacoes());
        sincronizarRefeicoes(atual, proposta.getRefeicoes());
        prescricaoNutricionalRepository.flush();
        if (atual.getStatus() == StatusPrescricao.ATIVA) ocorrenciaRefeicaoService.gerarOcorrencias(atual);
        return prescricaoNutricionalMapper.modeltoResponseDTO(atual);
    }

    private void validarIdentificadores(PrescricaoNutricional atual, PrescricaoNutricional proposta) {
        Set<String> vistos = new HashSet<>();
        for (Refeicao r : proposta.getRefeicoes()) {
            if (r.getId() != null && (atual.getRefeicoes().stream().noneMatch(x -> x.getId().equals(r.getId())) || !vistos.add("r" + r.getId())))
                throw new InvalidValueException("ID de refeição inválido ou repetido.");
            for (OpcaoRefeicao o : r.getOpcoes()) {
                if (o.getId() != null && (r.getId() == null || atual.getRefeicoes().stream().filter(x -> x.getId().equals(r.getId()))
                        .flatMap(x -> x.getOpcoes().stream()).noneMatch(x -> x.getId().equals(o.getId())) || !vistos.add("o" + o.getId())))
                    throw new InvalidValueException("ID de opção inválido ou repetido.");
                for (AlimentoPrescrito a : o.getAlimentos()) {
                    if (a.getId() != null && (o.getId() == null || atual.getRefeicoes().stream().flatMap(x -> x.getOpcoes().stream())
                            .filter(x -> x.getId().equals(o.getId())).flatMap(x -> x.getAlimentos().stream())
                            .noneMatch(x -> x.getId().equals(a.getId())) || !vistos.add("a" + a.getId())))
                        throw new InvalidValueException("ID de alimento inválido ou repetido.");
                    for (SubstituicaoAlimentar sub : lista(a.getSubstituicoes())) {
                        if (sub.getId() != null && (a.getId() == null || atual.getRefeicoes().stream().flatMap(x -> x.getOpcoes().stream())
                                .flatMap(x -> x.getAlimentos().stream()).filter(x -> x.getId().equals(a.getId()))
                                .flatMap(x -> x.getSubstituicoes().stream()).noneMatch(x -> x.getId().equals(sub.getId())) || !vistos.add("s" + sub.getId())))
                            throw new InvalidValueException("ID de substituição inválido ou repetido.");
                    }
                }
            }
        }
    }

    private void sincronizarRefeicoes(PrescricaoNutricional pai, List<Refeicao> propostas) {
        List<Refeicao> resultado = new ArrayList<>();
        for (Refeicao r : propostas) {
            Refeicao alvo = r.getId() == null ? new Refeicao() : pai.getRefeicoes().stream()
                    .filter(x -> x.getId().equals(r.getId())).findFirst().orElseThrow();
            alvo.setPrescricao(pai); alvo.setNome(r.getNome()); alvo.setOrdem(r.getOrdem());
            alvo.setHorario(r.getHorario()); alvo.setObservacoes(r.getObservacoes());
            sincronizarOpcoes(alvo, r.getOpcoes()); resultado.add(alvo);
        }
        pai.getRefeicoes().clear(); pai.getRefeicoes().addAll(resultado);
    }

    private void sincronizarOpcoes(Refeicao pai, List<OpcaoRefeicao> propostas) {
        List<OpcaoRefeicao> resultado = new ArrayList<>();
        for (OpcaoRefeicao o : propostas) {
            OpcaoRefeicao alvo = o.getId() == null ? new OpcaoRefeicao() : pai.getOpcoes().stream()
                    .filter(x -> x.getId().equals(o.getId())).findFirst().orElseThrow();
            alvo.setRefeicao(pai); alvo.setOrdem(o.getOrdem()); alvo.setDescricao(o.getDescricao());
            alvo.setOrientacoesPreparo(o.getOrientacoesPreparo());
            sincronizarAlimentos(alvo, o.getAlimentos()); resultado.add(alvo);
        }
        pai.getOpcoes().clear(); pai.getOpcoes().addAll(resultado);
    }

    private void sincronizarAlimentos(OpcaoRefeicao pai, List<AlimentoPrescrito> propostas) {
        List<AlimentoPrescrito> resultado = new ArrayList<>();
        for (AlimentoPrescrito a : propostas) {
            AlimentoPrescrito alvo = a.getId() == null ? new AlimentoPrescrito() : pai.getAlimentos().stream()
                    .filter(x -> x.getId().equals(a.getId())).findFirst().orElseThrow();
            alvo.setOpcao(pai); alvo.setDescricao(a.getDescricao()); alvo.setQuantidade(a.getQuantidade());
            alvo.setUnidade(a.getUnidade()); alvo.setMedidaCaseira(a.getMedidaCaseira()); alvo.setObservacao(a.getObservacao());
            List<SubstituicaoAlimentar> substituicoes = new ArrayList<>();
            for (SubstituicaoAlimentar sub : lista(a.getSubstituicoes())) {
                SubstituicaoAlimentar destino = sub.getId() == null ? new SubstituicaoAlimentar() : alvo.getSubstituicoes().stream()
                        .filter(x -> x.getId().equals(sub.getId())).findFirst().orElseThrow();
                destino.setAlimento(alvo); destino.setDescricao(sub.getDescricao()); destino.setQuantidade(sub.getQuantidade());
                destino.setUnidade(sub.getUnidade()); destino.setMedidaCaseira(sub.getMedidaCaseira());
                destino.setObservacao(sub.getObservacao()); destino.setOrdem(sub.getOrdem()); substituicoes.add(destino);
            }
            alvo.getSubstituicoes().clear(); alvo.getSubstituicoes().addAll(substituicoes); resultado.add(alvo);
        }
        pai.getAlimentos().clear(); pai.getAlimentos().addAll(resultado);
    }

    private static <T> List<T> lista(List<T> valor) { return valor == null ? List.of() : valor; }

    // MÉTODOS AUXILIARES
    private void vincularEntidades(PrescricaoNutricional prescricaoNutricional) {

        for (Refeicao refeicao : prescricaoNutricional.getRefeicoes()) {
            refeicao.setPrescricao(prescricaoNutricional);
            for (OpcaoRefeicao opcao : refeicao.getOpcoes()) {
                opcao.setRefeicao(refeicao);
                for (AlimentoPrescrito alimento : opcao.getAlimentos()) {
                    alimento.setOpcao(opcao);
                    if (alimento.getSubstituicoes() == null) alimento.setSubstituicoes(new ArrayList<>());
                    for (SubstituicaoAlimentar sub : alimento.getSubstituicoes()) sub.setAlimento(alimento);
                }
            }
        }
    }

}