package com.hdc.hdc.exames;

import java.time.Instant;
import java.time.LocalDate;

public record ExameDTO(Long id, Integer pacienteId, Integer profissionalCadastroId,
                       LocalDate dataColeta, String laboratorio, String observacao,
                       StatusExame status, Instant disponibilizacaoEm, Instant publicadoEm,
                       Instant criadoEm, Instant atualizadoEm, String nomeArquivo, Long tamanhoBytes) {}
