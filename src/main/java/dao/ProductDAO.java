package dao;

import model.entities.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // C - CREATE (do doPost)
    public void create(Product product) throws SQLException {
        // SQL corrigido para usar a tabela 'produto'
        String sql = "INSERT INTO public.produto (cod_produto, nome_produto, preco_produto, descricao_produto) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // CORREÇÃO: cod_produto é 'int' no seu banco, não 'long'
            stmt.setInt(1, product.getCode()); 
            stmt.setString(2, product.getName());
            stmt.setBigDecimal(3, product.getPrice());
            stmt.setString(4, product.getDescription());
            
            stmt.executeUpdate();
        }
    }

    // R - READ (do doGet - Listar todos)
    public List<Product> getAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM public.produto"; // Corrigido para 'public.produto'
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                products.add(mapRowToProduct(rs));
            }
        }
        return products;
    }

    // R - READ (do doGet - Buscar um por ID)
    public Product getById(int id) throws SQLException { // CORREÇÃO: ID é 'int'
        String sql = "SELECT * FROM public.produto WHERE cod_produto = ?"; // Corrigido para 'public.produto'
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id); // CORREÇÃO: ID é 'int'
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProduct(rs);
                }
            }
        }
        return null;
    }

    // U - UPDATE (do doPut)
    public void update(Product product) throws SQLException {
        String sql = "UPDATE public.produto SET nome_produto = ?, preco_produto = ?, descricao_produto = ? WHERE cod_produto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setString(3, product.getDescription());
            stmt.setInt(4, product.getCode()); // CORREÇÃO: ID é 'int'
            
            stmt.executeUpdate();
        }
    }

    // D - DELETE (do doDelete)
    public void delete(int id) throws SQLException { // CORREÇÃO: ID é 'int'
        String sql = "DELETE FROM public.produto WHERE cod_produto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id); // CORREÇÃO: ID é 'int'
            stmt.executeUpdate();
        }
    }

    // MÉTODO AJUDANTE: Converte uma linha do ResultSet em um objeto Product
    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        Product p = new Product(); 
        
        p.setCode(rs.getInt("cod_produto")); // CORREÇÃO: ID é 'int'
        p.setName(rs.getString("nome_produto"));
        p.setPrice(rs.getBigDecimal("preco_produto"));
        p.setDescription(rs.getString("descricao_produto"));
        return p;
    }
}