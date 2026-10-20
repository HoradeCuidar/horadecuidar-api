package com.hdc.hdc.orientacao_funcional.orientacao;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import static org.junit.jupiter.api.Assertions.*;

class MigracaoCatalogoFuncionalTest {
    @Test void migracaoPreservaIdsTagsImagensERealizacoes() {
        var dataSource = new DriverManagerDataSource("jdbc:h2:mem:catalogo-migracao;MODE=PostgreSQL;DB_CLOSE_DELAY=-1", "sa", "");
        var jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("CREATE TABLE usuarios (id INTEGER PRIMARY KEY)");
        jdbc.execute("CREATE TABLE tag_funcional (id BIGINT PRIMARY KEY, nome VARCHAR(100))");
        jdbc.execute("CREATE TABLE orientacao_funcional (id BIGINT PRIMARY KEY, descricao TEXT, ativo BOOLEAN, url_imagem VARCHAR(500))");
        jdbc.execute("CREATE TABLE orientacao_tag (orientacao_funcional_id BIGINT REFERENCES orientacao_funcional(id), tag_funcional_id BIGINT REFERENCES tag_funcional(id))");
        jdbc.execute("CREATE TABLE realizacao_exercicio (id BIGINT PRIMARY KEY, orientacao_funcional_id BIGINT REFERENCES orientacao_funcional(id))");
        jdbc.update("INSERT INTO tag_funcional VALUES (2, 'Tag legada')");
        jdbc.update("INSERT INTO orientacao_funcional VALUES (7, 'Descrição existente', TRUE, 'https://exemplo.test/capa.png')");
        jdbc.update("INSERT INTO orientacao_tag VALUES (7, 2)");
        jdbc.update("INSERT INTO realizacao_exercicio VALUES (9, 7)");
        new ResourceDatabasePopulator(new ClassPathResource("db/migration/V19__catalogo_orientacoes_revisadas.sql")).execute(dataSource);
        assertEquals("RASCUNHO", jdbc.queryForObject("SELECT status FROM orientacao_funcional WHERE id=7", String.class));
        assertEquals("Descrição existente", jdbc.queryForObject("SELECT resumo FROM orientacao_funcional WHERE id=7", String.class));
        assertFalse(jdbc.queryForObject("SELECT ativo FROM orientacao_funcional WHERE id=7", Boolean.class));
        assertEquals(7L, jdbc.queryForObject("SELECT orientacao_funcional_id FROM realizacao_exercicio WHERE id=9", Long.class));
        assertEquals(2L, jdbc.queryForObject("SELECT tag_funcional_id FROM orientacao_tag WHERE orientacao_funcional_id=7", Long.class));
        assertEquals("https://exemplo.test/capa.png", jdbc.queryForObject("SELECT url FROM midia_orientacao WHERE orientacao_id=7 AND capa=TRUE", String.class));
        assertEquals(3, jdbc.queryForObject("SELECT COUNT(*) FROM categoria_orientacao", Integer.class));
    }
}
