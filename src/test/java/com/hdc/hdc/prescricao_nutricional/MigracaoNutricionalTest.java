package com.hdc.hdc.prescricao_nutricional;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.dao.DataIntegrityViolationException;
import static org.junit.jupiter.api.Assertions.*;

class MigracaoNutricionalTest {
    @Test void migracoesNutricionaisCriamEstruturaEProtegemChaves() {
        var db = new DriverManagerDataSource("jdbc:h2:mem:nutri-migracao;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", "");
        var jdbc = new JdbcTemplate(db);
        jdbc.execute("CREATE TABLE usuarios (id INTEGER PRIMARY KEY)");
        jdbc.update("INSERT INTO usuarios VALUES (1), (2)");
        new ResourceDatabasePopulator(
            new ClassPathResource("db/migration/V13__create_tables_prescricao_nutricional.sql"),
            new ClassPathResource("db/migration/V18__create_table_ocorrencia_refeicao.sql"),
            new ClassPathResource("db/migration/V20__substituicoes_nutricionais.sql")
        ).execute(db);
        jdbc.update("INSERT INTO prescricao_nutricional (paciente_id, profissional_id, data_inicio, data_fim, status) VALUES (1,2,'2026-12-01','2026-12-02','ATIVA')");
        jdbc.update("INSERT INTO refeicao (prescricao_id,nome,ordem,horario) VALUES (1,'Almoço',1,'12:00:00')");
        jdbc.update("INSERT INTO opcao_refeicao (refeicao_id,ordem,orientacoes_preparo) VALUES (1,1,'Assar')");
        jdbc.update("INSERT INTO alimento_prescrito (opcao_id,descricao,quantidade,unidade,medida_caseira) VALUES (1,'Arroz',125,'G','5 colheres')");
        jdbc.update("INSERT INTO substituicao_alimentar (alimento_id,descricao,quantidade,unidade,ordem) VALUES (1,'Cuscuz',120,'G',1)");
        jdbc.update("INSERT INTO ocorrencia_refeicao (prescricao_id,refeicao_id,opcao_id,data_prevista,ordem_no_dia,status) VALUES (1,1,1,'2026-12-01',1,'REALIZADO')");
        assertEquals("5 colheres", jdbc.queryForObject("SELECT medida_caseira FROM alimento_prescrito WHERE id=1", String.class));
        assertThrows(DataIntegrityViolationException.class, () -> jdbc.update("DELETE FROM opcao_refeicao WHERE id=1"));
        assertThrows(DataIntegrityViolationException.class, () -> jdbc.update("INSERT INTO substituicao_alimentar (alimento_id,descricao,quantidade,unidade,ordem) VALUES (999,'Invalida',1,'G',1)"));
    }
}
