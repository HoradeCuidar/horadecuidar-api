package com.hdc.hdc.orientacao_funcional.orientacao;
import com.hdc.hdc.orientacao_funcional.orientacao.dto.*;
import org.mapstruct.*;
@Mapper(componentModel="spring")
public interface OrientacaoFuncionalMapper {
 @Mapping(target="revisadoPorId", source="revisadoPor.id")
 OrientacaoFuncionalResponseDTO toDto(OrientacaoFuncional entity);
}
