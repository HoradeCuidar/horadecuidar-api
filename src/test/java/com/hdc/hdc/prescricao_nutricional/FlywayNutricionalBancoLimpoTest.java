package com.hdc.hdc.prescricao_nutricional;

import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import java.sql.DriverManager;
import static org.junit.jupiter.api.Assertions.*;

class FlywayNutricionalBancoLimpoTest {
    @Test
    @EnabledIfEnvironmentVariable(named = "HDC_NUTRI_TEST_JDBC_URL", matches = ".+")
    void todasAsMigrationsAplicamEmPostgresqlLimpo() throws Exception {
        String url = System.getenv("HDC_NUTRI_TEST_JDBC_URL");
        var result = Flyway.configure().dataSource(url, "postgres", "nutri_test")
                .locations("classpath:db/migration").load().migrate();
        assertTrue(result.success);
        try (var connection = DriverManager.getConnection(url, "postgres", "nutri_test");
             var statement = connection.createStatement();
             var rows = statement.executeQuery("SELECT COUNT(*) FROM information_schema.tables WHERE table_name IN ('prescricao_nutricional','ocorrencia_refeicao','substituicao_alimentar','modelo_nutricional')")) {
            assertTrue(rows.next()); assertEquals(4, rows.getInt(1));
        }
    }
}
