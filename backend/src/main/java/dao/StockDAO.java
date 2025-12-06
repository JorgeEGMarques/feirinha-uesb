package dao;

import model.entities.Stock;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Acesso a Dados (DAO) para a entidade Estoque.
 * Responsável por realizar operações de CRUD na tabela 'estoque'.
 */
public class StockDAO {

    /**
     * Insere ou Atualiza o estoque (Upsert).
     * Se o produto já existir na barraca, atualiza a quantidade.
     * 
     * @param stock O objeto Stock contendo os dados do estoque.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void save(Stock stock) throws SQLException {
        String sql = "INSERT INTO public.estoque (cod_prod, cod_barraca, qntd_estoque) " +
                     "VALUES (?, ?, ?) " +
                     "ON CONFLICT (cod_prod, cod_barraca) " +
                     "DO UPDATE SET qntd_estoque = EXCLUDED.qntd_estoque";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, stock.getProductCode());
            stmt.setInt(2, stock.getTentCode());
            stmt.setShort(3, stock.getStockQuantity());

            stmt.executeUpdate();
        }
    }

    /**
     * Busca todo o estoque de uma barraca específica, INCLUINDO detalhes do produto.
     * 
     * @param tentId O código da barraca.
     * @return Uma lista de objetos Stock, cada um contendo os detalhes do produto associado.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public List<Stock> getByTentId(int tentId) throws SQLException {
        List<Stock> stocks = new ArrayList<>();

        String sql = "SELECT e.*, p.* FROM public.estoque e " +
                     "JOIN public.produto p ON e.cod_prod = p.cod_produto " +
                     "WHERE e.cod_barraca = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Stock s = mapRow(rs);

                    model.entities.Product p = new model.entities.Product();
                    p.setCode(rs.getInt("cod_produto"));
                    p.setName(rs.getString("nome_produto"));
                    p.setPrice(rs.getBigDecimal("preco_produto"));
                    p.setDescription(rs.getString("descricao_produto"));
                    p.setImagem(rs.getBytes("imagem_produto"));
                    
                    s.setProduct(p);
                    
                    stocks.add(s);
                }
            }
        }
        return stocks;
    }

    /**
     * Remove um produto do estoque de uma barraca.
     * 
     * @param productCode O código do produto.
     * @param tentCode O código da barraca.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void delete(int productCode, int tentCode) throws SQLException {
        String sql = "DELETE FROM public.estoque WHERE cod_prod = ? AND cod_barraca = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, productCode);
            stmt.setInt(2, tentCode);
            stmt.executeUpdate();
        }
    }

    /**
     * Método auxiliar para converter uma linha do ResultSet em um objeto Stock.
     * 
     * @param rs O ResultSet posicionado na linha a ser lida.
     * @return O objeto Stock preenchido.
     * @throws SQLException Se ocorrer um erro ao ler o ResultSet.
     */
    private Stock mapRow(ResultSet rs) throws SQLException {
        Stock s = new Stock();
        s.setProductCode(rs.getInt("cod_prod"));
        s.setTentCode(rs.getInt("cod_barraca"));
        s.setStockQuantity(rs.getShort("qntd_estoque"));
        return s;
    }
}
