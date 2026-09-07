package com.hdc.hdc.util.formatter;

import com.hdc.hdc.prescricao_nutricional.alimento.enums.UnidadeDeMedida;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class UnidadeMedidaFormatter {

    @Named("unidadeMedidaToString")
    public String toString(UnidadeDeMedida unidade) {

        return unidade == null ? null : unidade.name();
    }
}