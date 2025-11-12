package dao;

import model.entities.Product; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // C - CREATE (do doPost)
    // Note que usamos o seu construtor, que espera um 'long'
    public Product create(Product product) throws SQLException {
    // 1. Remova "cod_produto" do INSERT
         String sql = "INSERT INTO produto (nome_produto, preco_produto, descricao_produto) VALUES (?, ?, ?)";

         // 2. Adicione Statement.RETURN_GENERATED_KEYS
         try (Connection conn = DatabaseConnection.getConnection();
              PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

             // 3. Ajuste os índices dos parâmetros
             stmt.setString(1, product.getName());
             stmt.setBigDecimal(2, product.getPrice());
             stmt.setString(3, product.getDescription());

             int affectedRows = stmt.executeUpdate();
             
             if (affectedRows == 0) {
                 throw new SQLException("Falha ao criar produto, nenhuma linha afetada.");
             }

             // 4. Recupere o ID gerado
             try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                 if (generatedKeys.next()) {
                     // 5. Defina o ID no objeto e retorne-o
                     product.setCode(generatedKeys.getLong(1));
                     return product; 
                 } else {
                     throw new SQLException("Falha ao criar produto, nenhum ID obtido.");
                 }
             }
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