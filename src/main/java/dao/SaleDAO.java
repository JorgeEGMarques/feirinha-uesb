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
        
        // 1. SQL CORRETO para a tabela 'venda' (4 parâmetros)
        String sqlSale = "INSERT INTO public.venda (id_venda, data_venda, cod_barraca, cod_usuario) VALUES (?, ?, ?, ?)";
        
        // 2. SQL CORRETO para a tabela 'item_venda' (4 parâmetros)
        String sqlItem = "INSERT INTO public.item_venda (cod_prod, id_venda, qntd_venda, preco_venda) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmtSale = null;
        PreparedStatement stmtItem = null;

        try {
            conn = DatabaseConnection.getConnection();
            
            // --- INÍCIO DA TRANSAÇÃO ---
            conn.setAutoCommit(false); // Desliga o Auto-Commit

            // --- PASSO 1: Salvar a Venda (Pai) ---
            stmtSale = conn.prepareStatement(sqlSale);
            
            // Mapeamento CORRETO dos 4 parâmetros
            stmtSale.setInt(1, sale.getId()); // Pega o ID que veio do JSON
            stmtSale.setObject(2, sale.getSaleDate());
            stmtSale.setInt(3, sale.getTentCode());
            stmtSale.setString(4, sale.getUserCode()); // 'String userCode' (Java) -> 'char(11) cod_usuario' (SQL)
            
            int rowsAffected = stmtSale.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Falha ao criar venda, nenhuma linha afetada.");
            }
            // Não precisamos de "RETURN_GENERATED_KEYS" aqui, pois o ID foi fornecido.

            // --- PASSO 2: Salvar os Itens (Filhos) ---
            if (sale.getItems() != null && !sale.getItems().isEmpty()) {
                
                stmtItem = conn.prepareStatement(sqlItem);
                
                for (SaleItem item : sale.getItems()) {
                    // Mapeamento CORRETO dos 4 parâmetros do item
                    stmtItem.setInt(1, item.getProductCode());
                    stmtItem.setInt(2, sale.getId()); // Usa o ID do Pai
                    stmtItem.setShort(3, item.getSaleQuantity());
                    stmtItem.setBigDecimal(4, item.getSalePrice());
                    stmtItem.addBatch(); // Adiciona o INSERT ao "lote"
                }
                stmtItem.executeBatch(); // Executa todos os INSERTs dos itens de uma vez
            }

            // --- FIM DA TRANSAÇÃO ---
            conn.commit(); // Se tudo deu certo, COMITA
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Se deu erro, DESFAZ TUDO
            }
            throw new SQLException("Erro de transação ao salvar Venda: " + e.getMessage(), e);
        } finally {
            // Fecha tudo
            if (stmtItem != null) stmtItem.close();
            if (stmtSale != null) stmtSale.close();
            if (conn != null) {
                conn.setAutoCommit(true); // Devolve a conexão ao normal
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
        String sqlItems = "SELECT * FROM public.item_venda WHERE id_venda = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtSale = conn.prepareStatement(sqlSale);
             PreparedStatement stmtItems = conn.prepareStatement(sqlItems)) {
            
            // --- PASSO 1: Buscar a Venda (Pai) ---
            stmtSale.setInt(1, id);
            try (ResultSet rs = stmtSale.executeQuery()) {
                if (rs.next()) {
                    sale = mapRowToSale(rs); // Usa o ajudante
                }
            }
            
            // --- PASSO 2: Buscar os Itens (Filhos) ---
            if (sale != null) {
                stmtItems.setInt(1, id);
                List<SaleItem> items = new ArrayList<>();
                
                try (ResultSet rsItems = stmtItems.executeQuery()) {
                    while (rsItems.next()) {
                        items.add(mapRowToSaleItem(rsItems)); // Usa o novo ajudante
                    }
                }
                sale.setItems(items); // Adiciona a lista de itens ao objeto
            }
        }
        return sale; // Retorna a Venda completa (com itens), ou null
    }

    /**
     * Busca todas as Vendas (sem seus itens, para ser mais rápido).
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
    
    // --- MÉTODO AJUDANTE (PAI) ---
    // Agora usa Setters, pois Sale.java foi corrigido!
    private Sale mapRowToSale(ResultSet rs) throws SQLException {
        Sale sale = new Sale();
        sale.setId(rs.getInt("id_venda"));
        sale.setSaleDate(rs.getObject("data_venda", LocalDate.class));
        sale.setTentCode(rs.getInt("cod_barraca"));
        sale.setUserCode(rs.getString("cod_usuario")); // 'char(11)' (SQL) -> 'String' (Java)
        return sale;
    }

    // --- MÉTODO AJUDANTE (FILHO) ---
    // (Presume que SaleItem.java tem setters, ou um construtor que bate)
    private SaleItem mapRowToSaleItem(ResultSet rs) throws SQLException {
        SaleItem item = new SaleItem();
        
        // (Assumindo que SaleItem.java tem os setters abaixo)
        item.setProductCode(rs.getInt("cod_prod"));
        item.setSaleId(rs.getInt("id_venda"));
        item.setSaleQuantity(rs.getShort("qntd_venda"));
        item.setSalePrice(rs.getBigDecimal("preco_venda"));
        
        return item;
    }
    
    // ... (Implementar update e delete se necessário) ...
}