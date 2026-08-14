package com.hdc.hdc.util.formatter;

import com.hdc.hdc.prescricao_nutricional.enums.StatusPrescricao;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class StatusPrescricaoFormatter {

    @Named("statusPrescricaoToString")
    public String toString(StatusPrescricao status) {

        return status == null ? null : status.name();
    }
}