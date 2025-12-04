package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    // Vamos garantir que não tem espaços em branco ocultos
    private static final String JDBC_URL = "jdbc:postgresql://database:5432/feirinha_db"; 
    private static final String JDBC_USER = "postgres"; 
    private static final String JDBC_PASSWORD = "1234"; 

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver"); 
            
            // --- O ESPIÃO: IMPRIME NO CONSOLE DO DOCKER ---
            System.out.println("=================================");
            System.out.println("[DEBUG] TENTANDO CONECTAR...");
            System.out.println("[DEBUG] URL: " + JDBC_URL);
            System.out.println("[DEBUG] USER: " + JDBC_USER);
            // Imprime a senha entre colchetes para vermos se tem espaço em branco (ex: [password ])
            System.out.println("[DEBUG] PASS: [" + JDBC_PASSWORD + "]"); 
            System.out.println("=================================");
            // ----------------------------------------------

            return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do PostgreSQL não encontrado.", e);
        } catch (SQLException e) {
            System.err.println("[ERRO FATAL] Falha ao conectar: " + e.getMessage());
            throw e;
        }
    }
}