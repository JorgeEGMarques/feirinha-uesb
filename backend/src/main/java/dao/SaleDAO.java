package dao;

import model.entities.Sale;
import model.entities.SaleItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Acesso a Dados (DAO) para a entidade Venda.
 * Responsável por realizar operações de CRUD na tabela 'venda' e 'item_venda'.
 */
public class SaleDAO {

    /**
     * Cria uma nova Venda e seus Itens em uma única transação.
     * Assume que o ID da Venda (sale.getId()) JÁ VEM no objeto.
     * 
     * @param sale O objeto Sale contendo os dados da venda e seus itens.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void create(Sale sale) throws SQLException {
        
        String sqlSale = "INSERT INTO public.venda (data_venda, cod_barraca, cod_usuario) VALUES (?, ?, ?)";
        String sqlItem = "INSERT INTO public.item_venda (cod_prod, id_venda, qntd_venda, preco_venda) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmtSale = null;
        PreparedStatement stmtItem = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            stmtSale = conn.prepareStatement(sqlSale, java.sql.Statement.RETURN_GENERATED_KEYS);
            
            stmtSale.setObject(1, sale.getSaleDate());
            stmtSale.setInt(2, sale.getTentCode());
            stmtSale.setString(3, sale.getUserCode()); 
            
            int rowsAffected = stmtSale.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Falha ao criar venda, nenhuma linha afetada.");
            }

            try (ResultSet generatedKeys = stmtSale.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    sale.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao criar venda, não obteve o ID.");
                }
            }

            if (sale.getItems() != null && !sale.getItems().isEmpty()) {
                
                stmtItem = conn.prepareStatement(sqlItem);
                
                for (SaleItem item : sale.getItems()) {
                    stmtItem.setInt(1, item.getProductCode());
                    stmtItem.setInt(2, sale.getId());
                    stmtItem.setShort(3, item.getSaleQuantity());
                    stmtItem.setBigDecimal(4, item.getSalePrice());
                    stmtItem.addBatch();
                }
                stmtItem.executeBatch();
            }

            conn.commit(); 
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback();
            }
            throw new SQLException("Erro de transação ao salvar Venda: " + e.getMessage(), e);
        } finally {
            if (stmtItem != null) stmtItem.close();
            if (stmtSale != null) stmtSale.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Busca uma Venda (e seus itens) pelo ID.
     * 
     * @param id O ID da venda a ser buscada.
     * @return O objeto Sale encontrado, ou null se não existir.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public Sale getById(int id) throws SQLException {
        Sale sale = null;
        String sqlSale = "SELECT * FROM public.venda WHERE id_venda = ?";
        String sqlItems = "SELECT * FROM public.item_venda WHERE id_venda = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtSale = conn.prepareStatement(sqlSale);
             PreparedStatement stmtItems = conn.prepareStatement(sqlItems)) {

            stmtSale.setInt(1, id);
            try (ResultSet rs = stmtSale.executeQuery()) {
                if (rs.next()) {
                    sale = mapRowToSale(rs);
                }
            }

            if (sale != null) {
                stmtItems.setInt(1, id);
                List<SaleItem> items = new ArrayList<>();

                try (ResultSet rsItems = stmtItems.executeQuery()) {
                    while (rsItems.next()) {
                        items.add(mapRowToSaleItem(rsItems)); 
                    }
                }
                sale.setItems(items);
            }
        }
        return sale;
    }

    /**
     * Busca todas as Vendas (com seus itens).
     * 
     * @return Uma lista contendo todas as vendas.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public List<Sale> getAll() throws SQLException {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM public.venda";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                Sale sale = mapRowToSale(rs);
                sale.setItems(getItemsForSale(sale.getId()));
                sales.add(sale);
            }
        }
        return sales;
    }

    /**
     * Método auxiliar privado para buscar os itens de uma venda específica.
     * 
     * @param saleId O ID da venda.
     * @return Uma lista de itens da venda.
     */
    private List<SaleItem> getItemsForSale(int saleId) {
        List<SaleItem> items = new ArrayList<>();
        String sql = "SELECT * FROM item_venda WHERE id_venda = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, saleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapRowToSaleItem(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); 
        }
        return items;
    }
    
    /**
     * Deleta uma venda e seus itens (precisa de transação).
     * 
     * @param id O ID da venda a ser deletada.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void delete(int id) throws SQLException { // CORREÇÃO: Era 'String id'
        String sqlItems = "DELETE FROM public.item_venda WHERE id_venda = ?";
        String sqlSale = "DELETE FROM public.venda WHERE id_venda = ?";
        
        Connection conn = null;
        PreparedStatement stmtItems = null;
        PreparedStatement stmtSale = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            stmtItems = conn.prepareStatement(sqlItems);
            stmtItems.setInt(1, id);
            stmtItems.executeUpdate();

            stmtSale = conn.prepareStatement(sqlSale);
            stmtSale.setInt(1, id);

            stmtSale.executeUpdate();
            
            conn.commit();
            
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); 
            throw new SQLException("Erro de transação ao deletar venda: " + e.getMessage(), e);
        } finally {
            if (stmtItems != null) stmtItems.close();
            if (stmtSale != null) stmtSale.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Atualiza uma Venda e substitui seus itens em uma transação.
     * 
     * @param sale O objeto Sale com os dados atualizados.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void update(Sale sale) throws SQLException {
        String sqlUpdateSale = "UPDATE public.venda SET data_venda = ?, cod_barraca = ?, cod_usuario = ? WHERE id_venda = ?";
        String sqlDeleteItems = "DELETE FROM public.item_venda WHERE id_venda = ?";
        String sqlInsertItem = "INSERT INTO public.item_venda (cod_prod, id_venda, qntd_venda, preco_venda) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmtUpdate = null;
        PreparedStatement stmtDeleteItems = null;
        PreparedStatement stmtInsertItem = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            stmtUpdate = conn.prepareStatement(sqlUpdateSale);
            stmtUpdate.setObject(1, sale.getSaleDate());
            stmtUpdate.setInt(2, sale.getTentCode());
            stmtUpdate.setString(3, sale.getUserCode());
            stmtUpdate.setInt(4, sale.getId());
            stmtUpdate.executeUpdate();

            stmtDeleteItems = conn.prepareStatement(sqlDeleteItems);
            stmtDeleteItems.setInt(1, sale.getId());
            stmtDeleteItems.executeUpdate();

            if (sale.getItems() != null && !sale.getItems().isEmpty()) {
                stmtInsertItem = conn.prepareStatement(sqlInsertItem);
                for (model.entities.SaleItem item : sale.getItems()) {
                    stmtInsertItem.setInt(1, item.getProductCode());
                    stmtInsertItem.setInt(2, sale.getId());
                    stmtInsertItem.setShort(3, item.getSaleQuantity());
                    stmtInsertItem.setBigDecimal(4, item.getSalePrice());
                    stmtInsertItem.addBatch();
                }
                stmtInsertItem.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw new SQLException("Erro de transação ao atualizar venda: " + e.getMessage(), e);
        } finally {
            if (stmtInsertItem != null) stmtInsertItem.close();
            if (stmtDeleteItems != null) stmtDeleteItems.close();
            if (stmtUpdate != null) stmtUpdate.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }
    
    private Sale mapRowToSale(ResultSet rs) throws SQLException {
        Sale sale = new Sale();
        sale.setId(rs.getInt("id_venda"));
        sale.setSaleDate(rs.getObject("data_venda", LocalDate.class));
        sale.setTentCode(rs.getInt("cod_barraca"));
        sale.setUserCode(rs.getString("cod_usuario"));
        return sale;
    }

    private SaleItem mapRowToSaleItem(ResultSet rs) throws SQLException {
        SaleItem item = new SaleItem();
        item.setProductCode(rs.getInt("cod_prod"));
        item.setSaleId(rs.getInt("id_venda"));
        item.setSaleQuantity(rs.getShort("qntd_venda"));
        item.setSalePrice(rs.getBigDecimal("preco_venda"));
        return item;
    }
}
