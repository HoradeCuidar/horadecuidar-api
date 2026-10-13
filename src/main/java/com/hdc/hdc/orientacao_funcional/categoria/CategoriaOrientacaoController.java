package com.hdc.hdc.orientacao_funcional.categoria;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
@RestController @RequiredArgsConstructor @RequestMapping("/api/categorias-orientacao")
public class CategoriaOrientacaoController {
 private final CategoriaOrientacaoRepository repository;
 @GetMapping @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE','PACIENTE')")
 public List<CategoriaOrientacao> listar() { return repository.findAllByAtivoTrueOrderByNomeAsc(); }

 public record CategoriaRequest(@jakarta.validation.constraints.NotBlank @jakarta.validation.constraints.Size(max=100) String nome) {}
 @PostMapping @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
 public CategoriaOrientacao criar(@RequestBody @jakarta.validation.Valid CategoriaRequest dados) {
   CategoriaOrientacao categoria = new CategoriaOrientacao(); categoria.setNome(dados.nome().trim());
   return repository.save(categoria);
 }
 @PutMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
 public CategoriaOrientacao atualizar(@PathVariable Long id, @RequestBody @jakarta.validation.Valid CategoriaRequest dados) {
   CategoriaOrientacao categoria = buscar(id); categoria.setNome(dados.nome().trim()); return repository.save(categoria);
 }
 @DeleteMapping("/{id}") @PreAuthorize("hasAnyRole('ADMIN','PROFISSIONAL_DA_SAUDE')")
 public void inativar(@PathVariable Long id) {
   CategoriaOrientacao categoria = buscar(id); categoria.setAtivo(false); repository.save(categoria);
 }
 private CategoriaOrientacao buscar(Long id) {
   return repository.findById(id).orElseThrow(() -> new com.hdc.hdc.util.exception.ResourceNotFoundException("categoria", "Categoria não encontrada."));
 }
}
