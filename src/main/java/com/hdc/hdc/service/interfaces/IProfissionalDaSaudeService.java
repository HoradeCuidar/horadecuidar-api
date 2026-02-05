package com.hdc.hdc.service.interfaces;

import com.hdc.hdc.model.ProfissionalDaSaude;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IProfissionalDaSaudeService {

    ProfissionalDaSaude cadastrar(ProfissionalDaSaude profissionalDaSaude);
    ProfissionalDaSaude visualizar(Integer id_profissional);
    Page<ProfissionalDaSaude> visualizarTodos(Pageable pageable);
    ProfissionalDaSaude ativar(Integer id_profissional);
    ProfissionalDaSaude inativar(Integer id_profissional);
}