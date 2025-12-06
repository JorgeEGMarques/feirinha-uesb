package dao;

import model.entities.Product;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; 
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Acesso a Dados (DAO) para a entidade Produto.
 * Responsável por realizar operações de CRUD na tabela 'produto'.
 */
public class ProductDAO {

    /**
     * Cria um novo produto no banco de dados.
     * O ID do produto é gerado automaticamente pelo banco.
     * 
     * @param product O objeto Product contendo os dados a serem inseridos.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void create(Product product) throws SQLException {
        
        String sql = "INSERT INTO public.produto (nome_produto, preco_produto, descricao_produto, imagem_produto, cod_barraca) VALUES (?, ?, ?, ?, ?)";
        
        Connection conn = null;
        PreparedStatement stmt = null;
        PreparedStatement stockStmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            
            stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            
            stmt.setString(1, product.getName());
            stmt.setBigDecimal(2, product.getPrice());
            stmt.setString(3, product.getDescription());
            if (product.getImagem() != null) {
                stmt.setBytes(4, product.getImagem());
            } else {
                stmt.setNull(4, java.sql.Types.BINARY);
            }
            if (product.getTentCode() != null) {
                stmt.setInt(5, product.getTentCode());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Falha ao criar produto, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    product.setCode(generatedKeys.getInt(1)); 
                } else {
                    throw new SQLException("Falha ao criar produto, não obteve o ID.");
                }
            }

            if (product.getTentCode() != null && product.getTentCode() > 0) {
                String stockSql = "INSERT INTO public.estoque (cod_prod, cod_barraca, qntd_estoque) VALUES (?, ?, ?) " +
                                  "ON CONFLICT (cod_prod, cod_barraca) DO UPDATE SET qntd_estoque = EXCLUDED.qntd_estoque";
                stockStmt = conn.prepareStatement(stockSql);
                stockStmt.setInt(1, product.getCode());
                stockStmt.setInt(2, product.getTentCode());
                short initialQty = 1; // garantir quantidade positiva
                stockStmt.setShort(3, initialQty);
                stockStmt.executeUpdate();
            }
            
        } finally {
            if (stmt != null) stmt.close();
            if (stockStmt != null) stockStmt.close();
            if (conn != null) conn.close();
        }
    }

    /**
     * Busca todos os produtos cadastrados.
     * 
     * @return Uma lista contendo todos os produtos.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
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

    /**
     * Busca um produto pelo seu código (ID).
     * 
     * @param id O código do produto a ser buscado.
     * @return O objeto Product encontrado, ou null se não existir.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
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

    /**
     * Atualiza os dados de um produto existente.
     * 
     * @param product O objeto Product com os dados atualizados.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void update(Product product) throws SQLException {
        String sql = "UPDATE public.produto SET nome_produto = ?, preco_produto = ?, descricao_produto = ?, imagem_produto = ?, cod_barraca = ? WHERE cod_produto = ?";
        
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
            if (product.getTentCode() != null) {
                stmt.setInt(5, product.getTentCode());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            stmt.setInt(6, product.getCode());
            
            stmt.executeUpdate();
        }
    }

    /**
     * Deleta um produto pelo seu código (ID).
     * 
     * @param id O código do produto a ser deletado.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.produto WHERE cod_produto = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Método auxiliar para converter uma linha do ResultSet em um objeto Product.
     * 
     * @param rs O ResultSet posicionado na linha a ser lida.
     * @return O objeto Product preenchido.
     * @throws SQLException Se ocorrer um erro ao ler o ResultSet.
     */
    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        Product p = new Product();

        p.setCode(rs.getInt("cod_produto"));
        p.setName(rs.getString("nome_produto"));
        p.setPrice(rs.getBigDecimal("preco_produto"));
        p.setDescription(rs.getString("descricao_produto"));
        byte[] img = rs.getBytes("imagem_produto");
        p.setImagem(img);
        int tent = rs.getInt("cod_barraca");
        if (rs.wasNull()) {
            p.setTentCode(null);
        } else {
            p.setTentCode(tent);
        }
        return p;
    }
}
