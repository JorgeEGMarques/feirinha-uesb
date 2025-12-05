package dao;

import model.entities.Stock;
import model.entities.Tent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Acesso a Dados (DAO) para a entidade Barraca.
 * Responsável por realizar operações de CRUD na tabela 'barraca'.
 */
public class TentDAO {

    /**
     * Cria uma nova barraca no banco de dados.
     * 
     * @param tent O objeto Tent contendo os dados a serem inseridos.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void create(Tent tent) throws SQLException {
        String sql = "INSERT INTO public.barraca (cod_barraca, cpf_dono, nome_barraca, licensa_usuario) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tent.getCode());
            stmt.setString(2, tent.getCpfHolder());
            stmt.setString(3, tent.getName());
            stmt.setBytes(4, tent.getUserLicense()); 
            
            stmt.executeUpdate();
        }
    }

    /**
     * Busca uma barraca pelo ID.
     * Também carrega os itens (estoque) associados à barraca.
     * 
     * @param id O código da barraca a ser buscada.
     * @return O objeto Tent encontrado, ou null se não existir.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public Tent getById(int id) throws SQLException {
        Tent tent = null;
        String sql = "SELECT * FROM public.barraca WHERE cod_barraca = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    tent = mapRowToTent(rs);
                    StockDAO stockDAO = new StockDAO();
                    tent.setItems(stockDAO.getByTentId(tent.getCode()));
                }
            }
        }
        return tent;
    }

    /**
     * Busca todas as barracas cadastradas.
     * Também carrega os itens (estoque) para cada barraca.
     * 
     * @return Uma lista contendo todas as barracas.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public List<Tent> getAll() throws SQLException {
        List<Tent> tents = new ArrayList<>();
        String sql = "SELECT * FROM public.barraca";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            StockDAO stockDAO = new StockDAO();
            while (rs.next()) {
                Tent tent = mapRowToTent(rs);
                tent.setItems(stockDAO.getByTentId(tent.getCode()));
                tents.add(tent);
            }
        }
        return tents;
    }

    /**
     * Atualiza os dados de uma barraca existente.
     * 
     * @param tent O objeto Tent com os dados atualizados.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void update(Tent tent) throws SQLException {
        String sql = "UPDATE public.barraca SET nome_barraca = ?, licensa_usuario = ?, cpf_dono = ? WHERE cod_barraca = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tent.getName());
            stmt.setBytes(2, tent.getUserLicense());
            stmt.setString(3, tent.getCpfHolder());
            stmt.setInt(4, tent.getCode()); 
            
            stmt.executeUpdate();
        }
    }

    /**
     * Deleta uma barraca pelo ID.
     * 
     * @param id O código da barraca a ser deletada.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.barraca WHERE cod_barraca = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Método auxiliar para converter uma linha do ResultSet em um objeto Tent.
     * 
     * @param rs O ResultSet posicionado na linha a ser lida.
     * @return O objeto Tent preenchido.
     * @throws SQLException Se ocorrer um erro ao ler o ResultSet.
     */
    private Tent mapRowToTent(ResultSet rs) throws SQLException {
        Tent t = new Tent();
        t.setCode(rs.getInt("cod_barraca"));
        t.setCpfHolder(rs.getString("cpf_dono"));
        t.setName(rs.getString("nome_barraca"));
        t.setUserLicense(rs.getBytes("licensa_usuario"));
        return t;
    }

    /**
     * Insere ou atualiza o estoque de um produto em uma barraca.
     * 
     * @param stockItem O objeto Stock contendo os dados do estoque.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void updateStock(Stock stockItem) throws SQLException {
        String sql = "INSERT INTO public.estoque (cod_prod, cod_barraca, qntd_estoque) " +
                     "VALUES (?, ?, ?) " +
                     "ON CONFLICT (cod_prod, cod_barraca) DO UPDATE SET qntd_estoque = EXCLUDED.qntd_estoque";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, stockItem.getProductCode());
            stmt.setInt(2, stockItem.getTentCode());
            stmt.setShort(3, stockItem.getStockQuantity());

            stmt.executeUpdate();
        }
    }
}
