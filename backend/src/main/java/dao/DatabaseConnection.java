package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitária para gerenciar a conexão com o banco de dados PostgreSQL.
 */
public class DatabaseConnection {

    private static final String JDBC_URL = "jdbc:postgresql://localhost:5432/feirinha_db2";    
    private static final String JDBC_USER = "carlos";
    private static final String JDBC_PASSWORD = "65676861"; 

    /**
     * Obtém uma nova conexão com o banco de dados.
     * 
     * @return Uma instância de {@link Connection} conectada ao banco.
     * @throws SQLException Se ocorrer um erro ao conectar ou se o driver não for encontrado.
     */
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver"); 
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC do PostgreSQL não encontrado.", e);
        }

        return DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
    }
}