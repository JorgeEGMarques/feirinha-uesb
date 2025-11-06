package dao;

import model.entities.Product; // Importa seu modelo
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // C - CREATE (do doPost)
    // Note que usamos o seu construtor, que espera um 'long'
    public void create(Product product) throws SQLException {
        String sql = "INSERT INTO produto (cod_produto, nome_produto, preco_produto, descricao_produto) VALUES (?, ?, ?, ?)";
        
        // O try-with-resources fecha a conexão e o statement para você
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // "Hidrata" o SQL com os dados do objeto
            stmt.setLong(1, product.getCode()); // Assumindo que o código é 'long'
            stmt.setString(2, product.getName());
            stmt.setBigDecimal(3, product.getPrice()); // Use setBigDecimal para 'Numeric'
            stmt.setString(4, product.getDescription());
            
            stmt.executeUpdate(); // Executa o INSERT
        }
    }

    // R - READ (do doGet - Buscar todos)
    public List<Product> getAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM produto";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            // Itera sobre todos os resultados do banco
            while (rs.next()) {
                products.add(mapRowToProduct(rs)); // Usa o método ajudante
            }
        }
        return products;
    }

    // R - READ (do doGet - Buscar um por ID)
    public Product getById(long id) throws SQLException {
        String sql = "SELECT * FROM produto WHERE cod_produto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) { // Se encontrou
                    return mapRowToProduct(rs); // Retorna o produto
                }
            }
        }
        return null; // Retorna null se não encontrou
    }

    // U - UPDATE (do doPut)
    public void update(Product product) throws SQLException {
        String sql = "UPDATE produto SET nome_produto = ?, preco_produto = ?, descricao_produto = ? WHERE cod_produto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setString(3, product.getDescription());
            stmt.setLong(4, product.getCode());
            
            stmt.executeUpdate();
        }
    }

    // D - DELETE (do doDelete)
    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM produto WHERE cod_produto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setLong(1, id);
            stmt.executeUpdate();
        }
    }

    // MÉTODO AJUDANTE: Converte uma linha do ResultSet em um objeto Product
    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        // Use seu construtor vazio
        Product p = new Product(); 
        
        // E preencha com os setters
        p.setCode(rs.getLong("cod_produto"));
        p.setName(rs.getString("nome_produto"));
        p.setPrice(rs.getBigDecimal("preco_produto")); // Use getBigDecimal para 'Numeric'
        p.setDescription(rs.getString("descricao_produto"));
        return p;
    }
}