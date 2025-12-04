package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/db_feirinha"; 
    private static final String JDBC_USER = "sampaio"; 
    private static final String JDBC_PASSWORD = "12345"; 

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver"); 
            Connection conn = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);

            // 🔹 ESSENCIAL: ativa commit automático
            conn.setAutoCommit(true);

            // 🔹 Apenas para depuração
            System.out.println("[DB] Conexão aberta com sucesso!");
            
            return conn;
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do PostgreSQL não encontrado.", e);
        } catch (SQLException e) {
            System.err.println("[DB] Erro ao conectar: " + e.getMessage());
            throw e;
        }
    }
}
