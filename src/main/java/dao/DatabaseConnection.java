package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // --- CONFIGURE SEUS DADOS AQUI ---
    
    // 1. Altere "feirinha_db" para o nome exato do banco que você criou
    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/feirinha_db"; 
    
    // 2. Coloque o usuário do seu Postgres (o padrão é "postgres")
    private static final String JDBC_USER = "carlos"; 
    
    // 3. Coloque a senha que você definiu ao instalar o Postgres
    private static final String JDBC_PASSWORD = "65676861"; // <-- MUDE ISSO
    
    // --- FIM DA CONFIGURAÇÃO ---

    /**
     * Obtém uma nova conexão com o banco de dados.
     */
    public static Connection getConnection() throws SQLException {
        try {
            // 1. Carrega o driver do PostgreSQL
            // (Isso força o Java a reconhecer o driver que veio do pom.xml)
            Class.forName("org.postgresql.Driver"); 
        } catch (ClassNotFoundException e) {
            // Isso só falha se a dependência do pom.xml estiver faltando/corrompida
            throw new SQLException("Driver JDBC do PostgreSQL não encontrado.", e);
        }
        
        // 2. Tenta conectar e retornar a conexão
        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}