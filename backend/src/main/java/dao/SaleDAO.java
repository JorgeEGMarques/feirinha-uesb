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

public class SaleDAO {

    /**
     * Cria uma nova Venda e seus Itens em uma única transação.
     * Assume que o ID da Venda (sale.getId()) JÁ VEM no objeto.
     */
    public void create(Sale sale) throws SQLException {
        
        String sqlSale = "INSERT INTO public.venda (id_venda, data_venda, cod_barraca, cod_usuario) VALUES (?, ?, ?, ?)";
        String sqlItem = "INSERT INTO public.item_venda (cod_prod, id_venda, qntd_venda, preco_venda) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmtSale = null;
        PreparedStatement stmtItem = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Inicia transação

            // --- PASSO 1: Salvar a Venda (Pai) ---
            stmtSale = conn.prepareStatement(sqlSale);
            
            stmtSale.setInt(1, sale.getId());
            stmtSale.setObject(2, sale.getSaleDate());
            stmtSale.setInt(3, sale.getTentCode());
            stmtSale.setString(4, sale.getUserCode()); 
            
            int rowsAffected = stmtSale.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Falha ao criar venda, nenhuma linha afetada.");
            }

            // --- PASSO 2: Salvar os Itens (Filhos) ---
            if (sale.getItems() != null && !sale.getItems().isEmpty()) {
                
                stmtItem = conn.prepareStatement(sqlItem);
                
                for (SaleItem item : sale.getItems()) {
                    stmtItem.setInt(1, item.getProductCode());
                    stmtItem.setInt(2, sale.getId()); // Usa o ID do Pai
                    stmtItem.setShort(3, item.getSaleQuantity());
                    stmtItem.setBigDecimal(4, item.getSalePrice());
                    stmtItem.addBatch();
                }
                stmtItem.executeBatch();
            }

            conn.commit(); // Salva
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Desfaz
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
     */
    public Sale getById(int id) throws SQLException {
        Sale sale = null;
        String sqlSale = "SELECT * FROM public.venda WHERE id_venda = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtSale = conn.prepareStatement(sqlSale)) {
            
            // --- PASSO 1: Buscar a Venda (Pai) ---
            stmtSale.setInt(1, id);
            try (ResultSet rs = stmtSale.executeQuery()) {
                if (rs.next()) {
                    sale = mapRowToSale(rs);
                }
            }
            
            // --- PASSO 2: Buscar os Itens (Filhos) ---
            if (sale != null) {
                loadItemsForSale(conn, sale);
            }
        }
        return sale;
    }

    /**
     * Busca todas as compras feitas por um usuário específico (Histórico de Compras).
     */
    public List<Sale> getByUserId(String userId) throws SQLException {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM public.venda WHERE cod_usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Sale sale = mapRowToSale(rs);
                    loadItemsForSale(conn, sale); // Carrega os itens
                    sales.add(sale);
                }
            }
        }
        return sales;
    }

    /**
     * Busca todas as vendas realizadas por uma barraca específica (Histórico de Vendas).
     */
    public List<Sale> getByTentId(int tentId) throws SQLException {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT * FROM public.venda WHERE cod_barraca = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tentId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Sale sale = mapRowToSale(rs);
                    loadItemsForSale(conn, sale); // Carrega os itens
                    sales.add(sale);
                }
            }
        }
        return sales;
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
                Sale sale = mapRowToSale(rs);
                // Opcional: carregar itens aqui se necessário, mas pode ser pesado para getAll()
                // loadItemsForSale(conn, sale); 
                sales.add(sale);
            }
        }
        return sales;
    }
    
    /**
     * Deleta uma venda e seus itens.
     */
    public void delete(int id) throws SQLException { 
        String sqlItems = "DELETE FROM public.item_venda WHERE id_venda = ?";
        String sqlSale = "DELETE FROM public.venda WHERE id_venda = ?";
        
        Connection conn = null;
        PreparedStatement stmtItems = null;
        PreparedStatement stmtSale = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Inicia transação

            // 1. Deleta os "Filhos"
            stmtItems = conn.prepareStatement(sqlItems);
            stmtItems.setInt(1, id);
            stmtItems.executeUpdate();

            // 2. Deleta o "Pai"
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
     * Atualiza uma Venda.
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

            // Update sale row
            stmtUpdate = conn.prepareStatement(sqlUpdateSale);
            stmtUpdate.setObject(1, sale.getSaleDate());
            stmtUpdate.setInt(2, sale.getTentCode());
            stmtUpdate.setString(3, sale.getUserCode());
            stmtUpdate.setInt(4, sale.getId());
            stmtUpdate.executeUpdate();

            // Delete existing items
            stmtDeleteItems = conn.prepareStatement(sqlDeleteItems);
            stmtDeleteItems.setInt(1, sale.getId());
            stmtDeleteItems.executeUpdate();

            // Insert new items
            if (sale.getItems() != null && !sale.getItems().isEmpty()) {
                stmtInsertItem = conn.prepareStatement(sqlInsertItem);
                for (SaleItem item : sale.getItems()) {
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
    
    // --- MÉTODOS AJUDANTES ---

    private void loadItemsForSale(Connection conn, Sale sale) throws SQLException {
        String sqlItems = "SELECT * FROM public.item_venda WHERE id_venda = ?";
        try (PreparedStatement stmtItems = conn.prepareStatement(sqlItems)) {
            stmtItems.setInt(1, sale.getId());
            try (ResultSet rsItems = stmtItems.executeQuery()) {
                List<SaleItem> items = new ArrayList<>();
                while (rsItems.next()) {
                    items.add(mapRowToSaleItem(rsItems)); 
                }
                sale.setItems(items);
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