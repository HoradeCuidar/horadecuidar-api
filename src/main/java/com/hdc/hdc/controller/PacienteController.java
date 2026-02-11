package com.hdc.hdc.controller;

import com.hdc.hdc.dto.create.PacienteCreateDto;
import com.hdc.hdc.model.Paciente;
import com.hdc.hdc.service.PacienteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/paciente")
public class PacienteController {

    private final PacienteService pacienteService;

//    public Paciente cadastrar(Paciente paciente) {
//
//    public Paciente visualizar(Integer id_profissional) {
//
//    public Page<Paciente> visualizarTodos(Pageable pageable) {
//
//    public void atualizar(PacienteCreateDto paciente, Integer id) {
//
//    public void deletar(Integer id) {
//
//    public Paciente ativar(Integer id_profissional) {
//
//    public Paciente inativar(Integer id_profissional) {
}
