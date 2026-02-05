package com.hdc.hdc.util.formatter;

import com.hdc.hdc.model.enums.Genero;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

@Component
public class GeneroFormatter {

    @Named("generoToString")
    public String generoToString(Genero genero) {
        return genero != null ? genero.getGenero() : null;
    }
}