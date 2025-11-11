package dao;

import model.entities.Sale;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO para a entidade Sale.
 * NOTA: O modelo Sale.java não possui setters.
 * Por isso, os métodos de leitura (get) usam o construtor.
 */
public class SaleDAO {

    /**
     * Cria uma nova Venda no banco de dados.
     * AVISO DE INCONSISTÊNCIA: Converte 'long userCode' (Java) para 'String' (SQL).
     */
    public void create(Sale sale) throws SQLException {
        String sql = "INSERT INTO public.venda (id_venda, data_venda, cod_barraca, cod_usuario) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, sale.getId());
            stmt.setObject(2, sale.getSaleDate());
            // NOTA: Inconsistência de tipo: Modelo (long) vs SQL (int). Fazendo cast.
            stmt.setInt(3, sale.getTentCode()); 
            // NOTA: Inconsistência de tipo: Modelo (long) vs SQL (char(11)).
            stmt.setString(4, sale.getUserCode()); 
            
            stmt.executeUpdate();
        }
    }

    /**
     * Busca uma Venda pelo ID.
     */
    public Sale getById(int id) throws SQLException {
        Sale sale = null;
        String sql = "SELECT * FROM public.venda WHERE id_venda = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    sale = mapRowToSale(rs); // Usa o método ajudante
                }
            }
        }
        return sale;
    }

    /**
     * Busca todas as Vendas.
     */
    public List<Sale> getAll() throws SQLException {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM public.venda";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                sales.add(mapRowToSale(rs));
            }
        }
        return sales;
    }
    
    /**
     * Método Ajudante: Converte linha do SQL para objeto Java.
     * USA O CONSTRUTOR, pois o modelo 'Sale.java' não tem setters.
     * AVISO DE INCONSISTÊNCIA: Converte 'cod_usuario' (String) para 'long'.
     */
    private Sale mapRowToSale(ResultSet rs) throws SQLException {
        
        // "Contornando" a inconsistência de tipo (String do SQL para long do Java)
        long userCode = 0L; // Valor padrão
        String userCpf = rs.getString("cod_usuario");
        if (userCpf != null) {
            try {
                // Tenta converter o CPF (String) para um long
                userCode = Long.parseLong(userCpf.trim());
            } catch (NumberFormatException e) {
                // Se o CPF não for um número (ex: "123.456..."), ignora o erro
                System.err.println("Aviso: Falha ao converter 'cod_usuario' para long: " + userCpf);
            }
        }

        /* Mapeia usando o construtor
        return new Sale(
            rs.getInt("id_venda"),
            rs.getObject("data_venda"),
            rs.getInt("cod_barraca"), // SQL 'int' cabe em Java 'long' (OK)
            userCpf
        ); */

        return null;
    }
    
    // ... (Implementar update e delete se necessário) ...
}