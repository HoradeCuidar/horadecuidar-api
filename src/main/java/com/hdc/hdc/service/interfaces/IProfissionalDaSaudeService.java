package com.hdc.hdc.service.interfaces;

import com.hdc.hdc.model.ProfissionalDaSaude;

public interface IProfissionalDaSaudeService {

    ProfissionalDaSaude cadastrar(ProfissionalDaSaude profissionalDaSaude);
    ProfissionalDaSaude visualizar(Integer id_profissional);
}