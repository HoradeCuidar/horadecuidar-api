package com.hdc.hdc.mapper;

import com.hdc.hdc.dto.create.PacienteCreateDto;
import com.hdc.hdc.dto.response.PacienteResponseDto;
import com.hdc.hdc.model.Doenca;
import com.hdc.hdc.model.Paciente;
import com.hdc.hdc.model.associacoes.PacienteDoencas;
import com.hdc.hdc.repository.DoencaRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring")
public abstract class PacienteMapper {

    @Autowired
    private DoencaRepository doencaRepository;

    @Autowired
    private DoencaMapper doencaMapper;

    public Paciente toEntity(PacienteCreateDto dto) {
        Paciente paciente = this.toEntityParcial(dto);

        if (dto.doencas() != null && !dto.doencas().isEmpty()) {

            List<Doenca> doencas = doencaRepository.findAllById(dto.doencas());

            List<PacienteDoencas> relacoes = doencas.stream()
                    .map(doenca -> {
                        PacienteDoencas pd = new PacienteDoencas();
                        pd.setPaciente(paciente);
                        pd.setDoenca(doenca);
                        pd.setDataDiagnostico(LocalDate.now());
                        return pd;
                    })
                    .toList();
            paciente.setDoencas(relacoes);
        }

        return paciente;
    }

    @Mapping(target = "doencas", source = "doencas")
    public abstract PacienteResponseDto toDto(Paciente paciente);

    public abstract List<PacienteResponseDto> toDto(List<Paciente> entity);

    protected List<Doenca> mapDoencas(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return doencaRepository.findAllById(ids);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "doencas", ignore = true)
    public abstract Paciente toEntityParcial(PacienteCreateDto dto);

    protected List<Doenca> map(List<PacienteDoencas> lista) {
        if (lista == null) return null;

        return lista.stream()
                .map(pd -> doencaMapper.toDto(pd.getDoenca()))
                .toList();
    }
}
