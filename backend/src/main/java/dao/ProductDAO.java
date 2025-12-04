package dao;

import model.entities.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // 1. IMPORTAR O STATEMENT (Necessário para RETURN_GENERATED_KEYS)
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // C - CREATE (Corrigido para colunas SERIAL / GENERATED ALWAYS)
    public void create(Product product) throws SQLException {
        
        // 2. SQL CORRIGIDO: Remova 'cod_produto' do INSERT.
        // O banco vai gerar esse valor.
        String sql = "INSERT INTO public.produto (nome_produto, preco_produto, descricao_produto, imagem_produto) VALUES (?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            
            // 3. Peça ao banco para retornar o ID gerado (a chave)
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            // 4. Mapeamento dos parâmetros (agora 3, não 4)
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setString(3, product.getDescription());
            if (product.getImagem() != null) {
                stmt.setBytes(4, product.getImagem());
            } else {
                stmt.setNull(4, java.sql.Types.BINARY);
            }
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Falha ao criar produto, nenhuma linha afetada.");
            }

            // 5. Pegue o ID gerado pelo banco e atualize seu objeto Java
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    // Use getInt() pois seu SQL 'cod_produto' é INT
                    product.setCode(generatedKeys.getInt(1)); 
                } else {
                    throw new SQLException("Falha ao criar produto, não obteve o ID.");
                }
            }
            
        } finally {
            // Fechamento manual é necessário aqui por causa do generatedKeys
            if (stmt != null) stmt.close();
            if (conn != null) conn.close();
        }
    }

    // R - READ (do doGet - Listar todos)
    public List<Product> getAll() throws SQLException {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM public.produto";
        
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
    public Product getById(int id) throws SQLException {
        String sql = "SELECT * FROM public.produto WHERE cod_produto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
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
        String sql = "UPDATE public.produto SET nome_produto = ?, preco_produto = ?, descricao_produto = ?, imagem_produto = ? WHERE cod_produto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setString(3, product.getDescription());
            if (product.getImagem() != null) {
                stmt.setBytes(4, product.getImagem());
            } else {
                stmt.setNull(4, java.sql.Types.BINARY);
            }
            stmt.setInt(5, product.getCode());
            
            stmt.executeUpdate();
        }
    }

    // D - DELETE (do doDelete)
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.produto WHERE cod_produto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // MÉTODO AJUDANTE: Converte uma linha do ResultSet em um objeto Product
    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        Product p = new Product(); 
        
        p.setCode(rs.getInt("cod_produto"));
        p.setName(rs.getString("nome_produto"));
        p.setPrice(rs.getBigDecimal("preco_produto"));
        p.setDescription(rs.getString("descricao_produto"));
        byte[] img = rs.getBytes("imagem_produto");
        p.setImagem(img);
        return p;
    }
}