package com.hdc.hdc.util.formatter;

import com.hdc.hdc.usuarios.enums.Status;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class StatusFormatter {

    @Named("statusToString")
    public String statusToString(Status status) {
        return status != null ? status.getStatus() : null;
    }
}