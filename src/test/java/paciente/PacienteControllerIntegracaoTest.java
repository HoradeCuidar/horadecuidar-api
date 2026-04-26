package paciente;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hdc.hdc.HdcApplication;
import com.hdc.hdc.pacientes.dto.PacienteCreateDto;
import com.hdc.hdc.pacientes.dto.PacienteResponseDto;
import com.hdc.hdc.pacientes.PacienteMapper;
import com.hdc.hdc.usuarios.enums.Genero;
import com.hdc.hdc.pacientes.PacienteService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@SpringBootTest(classes = HdcApplication.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PacienteControllerIntegracaoTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        @MockitoBean
        private PacienteService pacienteService;

        @MockitoBean
        private PacienteMapper pacienteMapper;

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldCreateUserSuccessfully() throws Exception {
                // Simula um usuário logado com as permissões corretas
                // Arrange: Criamos os dados que serão enviados na requisição
                PacienteCreateDto dto = new PacienteCreateDto(
                                "João Silva",
                                "joao.silva",
                                "senha12345",
                                LocalDate.of(1990, 5, 20), "11999999999",
                                Genero.MASCULINO,
                                "joao@email.com",
                                "Rua A",
                                "Centro",
                                "SP",
                                "São Paulo",
                                "123",
                                Collections.emptyList(),
                                "");

                PacienteResponseDto responseDto = new PacienteResponseDto(
                                1, "João Silva", "joao@email.com", "joao.silva", "1990-05-20", null,
                                "ATIVO", "11999999999", "Rua A", "Centro", "SP", "São Paulo", "123",
                                "MASCULINO", Collections.emptyList(), "");

                Mockito.when(pacienteService.cadastrar(Mockito.any(PacienteCreateDto.class)))
                                .thenReturn(responseDto);

                // Act & Assert: chama POST para "/api/paciente" passando o "dto" transformado em JSON
                // Esperasse HTTP 201 (Created), o nome e o email
                mockMvc.perform(post("/api/paciente").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.nome").value("João Silva"))
                                .andExpect(jsonPath("$.email").value("joao@email.com"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnUserById() throws Exception {
                // Teste de visualização de paciente por ID
                Long idBuscado = 1L;
                PacienteResponseDto responseDto = new PacienteResponseDto(
                                1,
                                "Maria Silva",
                                "maria@email.com",
                                "maria.silva",
                                "1995-08-15",
                                null,
                                "ATIVO",
                                "11988888888",
                                "Rua B",
                                "Centro",
                                "SP",
                                "São Paulo",
                                "456",
                                "FEMININO",
                                Collections.emptyList(),
                                "Nenhuma");

                Mockito.when(pacienteService.visualizarPorId(idBuscado))
                                .thenReturn(responseDto);

                mockMvc.perform(get("/api/paciente/{id}", idBuscado)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk()) // Esperamos HTTP 200 (Ok)
                                .andExpect(jsonPath("$.id").value(1))
                                .andExpect(jsonPath("$.nome").value("Maria Silva"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturn404WhenUserNotFound() throws Exception {
                // Teste de visualização de paciente por ID quando o paciente não é encontrado
                Long idInexistente = 999L;
                Mockito.when(pacienteService.visualizarPorId(idInexistente))
                                .thenThrow(new com.hdc.hdc.util.exception.ResourceNotFoundException(
                                                "Paciente não encontrado"));

                mockMvc.perform(get("/api/paciente/{id}", idInexistente)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isNotFound());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturn400WhenEmailIsInvalid() throws Exception {
                // Teste de criação de paciente com email inválido
                PacienteCreateDto dto = new PacienteCreateDto(
                                "João Silva",
                                "joao.silva",
                                "senha12345",
                                LocalDate.of(1990, 5, 20), "11999999999",
                                Genero.MASCULINO,
                                "email-invalido", // Email inválido
                                "Rua A",
                                "Centro",
                                "SP",
                                "São Paulo",
                                "123",
                                Collections.emptyList(),
                                "");

                mockMvc.perform(post("/api/paciente").with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnUserByEmail() throws Exception {
                // Teste de visualização de paciente por email
                String emailBuscado = "maria@email.com";
                PacienteResponseDto responseDto = new PacienteResponseDto(
                                1, "Maria Silva", emailBuscado, "maria.silva", "1995-08-15", null,
                                "ATIVO", "11988888888", "Rua B", "Centro", "SP", "São Paulo", "456",
                                "FEMININO", Collections.emptyList(), "Nenhuma");

                Mockito.when(pacienteService.visualizarPorEmail(emailBuscado))
                                .thenReturn(responseDto);

                mockMvc.perform(get("/api/paciente/email")
                                .param("email", emailBuscado)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.email").value(emailBuscado))
                                .andExpect(jsonPath("$.nome").value("Maria Silva"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnAllUsersPaged() throws Exception {
                // Teste de visualização de todos os pacientes paginados
                PacienteResponseDto responseDto = new PacienteResponseDto(
                                1, "Maria Silva", "maria@email.com", "maria.silva", "1995-08-15", null,
                                "ATIVO", "11988888888", "Rua B", "Centro", "SP", "São Paulo", "456",
                                "FEMININO", Collections.emptyList(), "Nenhuma");
                org.springframework.data.domain.Page<PacienteResponseDto> page = new org.springframework.data.domain.PageImpl<>(
                                java.util.List.of(responseDto));

                Mockito.when(pacienteService.visualizarTodos(0, 10))
                                .thenReturn(page);

                mockMvc.perform(get("/api/paciente")
                                .param("pagina", "0")
                                .param("limite", "10")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].id").value(1))
                                .andExpect(jsonPath("$.content[0].nome").value("Maria Silva"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldReturnUsersByName() throws Exception {
                // Teste de visualização de pacientes por nome
                String nomeBuscado = "Maria";
                PacienteResponseDto responseDto = new PacienteResponseDto(
                                1, "Maria Silva", "maria@email.com", "maria.silva", "1995-08-15", null,
                                "ATIVO", "11988888888", "Rua B", "Centro", "SP", "São Paulo", "456",
                                "FEMININO", Collections.emptyList(), "Nenhuma");
                org.springframework.data.domain.Page<PacienteResponseDto> page = new org.springframework.data.domain.PageImpl<>(
                                java.util.List.of(responseDto));

                Mockito.when(pacienteService.encontrarPorNome(nomeBuscado))
                                .thenReturn(page);

                mockMvc.perform(get("/api/paciente/nome")
                                .param("nome", nomeBuscado)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.content[0].id").value(1))
                                .andExpect(jsonPath("$.content[0].nome").value("Maria Silva"));
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldUpdateUserSuccessfully() throws Exception {
                // Teste de atualização de paciente
                Long idAtualizar = 1L;
                PacienteCreateDto dto = new PacienteCreateDto(
                                "João Silva Atualizado", "joao.silva", "senha12345",
                                LocalDate.of(1990, 5, 20), "11999999999", Genero.MASCULINO,
                                "joao@email.com", "Rua A", "Centro", "SP", "São Paulo", "123",
                                Collections.emptyList(), "");

                Mockito.doNothing().when(pacienteService).atualizar(Mockito.any(PacienteCreateDto.class),
                                Mockito.eq(idAtualizar));

                mockMvc.perform(put("/api/paciente/{id}", idAtualizar).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(dto)))
                                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldDeleteUserSuccessfully() throws Exception {
                // Teste de deleção de paciente
                Long idDeletar = 1L;

                Mockito.doNothing().when(pacienteService).deletar(idDeletar);

                mockMvc.perform(delete("/api/paciente/{id}", idDeletar).with(csrf()))
                                .andExpect(status().isNoContent());
        }

        @Test
        @WithMockUser(roles = "ADMIN")
        void shouldChangeUserStatusSuccessfully() throws Exception {
                // Teste de alteração de status de paciente
                Long idAlterar = 1L;
                PacienteResponseDto responseDto = new PacienteResponseDto(
                                1, "Maria Silva", "maria@email.com", "maria.silva", "1995-08-15", null,
                                "INATIVO", "11988888888", "Rua B", "Centro", "SP", "São Paulo", "456",
                                "FEMININO", Collections.emptyList(), "Nenhuma");

                Mockito.when(pacienteService.alterarStatus(idAlterar))
                                .thenReturn(responseDto);

                mockMvc.perform(patch("/api/paciente/status/{id}", idAlterar).with(csrf())
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.status").value("INATIVO"));
        }
}
