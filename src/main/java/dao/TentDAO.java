package dao;

import model.entities.Stock;
import model.entities.Tent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TentDAO {

    /**
     * Cria uma nova barraca no banco de dados.
     */
    public void create(Tent tent) throws SQLException {
        // CORREÇÃO: SQL estava faltando 'licensa_usuario'
        String sql = "INSERT INTO public.barraca (cod_barraca, cpf_dono, nome_barraca, licensa_usuario) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tent.getCode());
            stmt.setString(2, tent.getCpfHolder());
            stmt.setString(3, tent.getName());
            // CORREÇÃO: Parâmetro 4 estava faltando
            stmt.setBytes(4, tent.getUserLicense()); 
            
            stmt.executeUpdate();
        }
    }

    /**
     * Busca uma barraca pelo ID.
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
                }
            }
        }
        return tent;
    }

    /**
     * Busca todas as barracas.
     */
    public List<Tent> getAll() throws SQLException {
        List<Tent> tents = new ArrayList<>();
        String sql = "SELECT * FROM public.barraca";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                tents.add(mapRowToTent(rs));
            }
        }
        return tents;
    }

    /**
     * Atualiza uma barraca (nome e licença).
     */
    public void update(Tent tent) throws SQLException {
        // CORREÇÃO: SQL e parâmetros estavam errados
        String sql = "UPDATE public.barraca SET nome_barraca = ?, licensa_usuario = ?, cpf_dono = ? WHERE cod_barraca = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tent.getName());
            stmt.setBytes(2, tent.getUserLicense());
            stmt.setString(3, tent.getCpfHolder());
            stmt.setInt(4, tent.getCode()); // Parâmetro do WHERE
            
            stmt.executeUpdate();
        }
    }

    /**
     * Deleta uma barraca pelo ID.
     */
    public void delete(int id) throws SQLException { // CORREÇÃO: Era 'String code'
        String sql = "DELETE FROM public.barraca WHERE cod_barraca = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id); // CORREÇÃO: Era 'setString'
            stmt.executeUpdate();
        }
    }

    // MÉTODO AJUDANTE: Mapeia a linha para o objeto
    private Tent mapRowToTent(ResultSet rs) throws SQLException {
        Tent t = new Tent();
        t.setCode(rs.getInt("cod_barraca"));
        t.setCpfHolder(rs.getString("cpf_dono"));
        t.setName(rs.getString("nome_barraca"));
        // CORREÇÃO: Mapeamento estava faltando
        t.setUserLicense(rs.getBytes("licensa_usuario"));
        return t;
    }

    // Este método vai INSERIR o estoque se não existir, ou ATUALIZAR se já existir.
    public void updateStock(Stock stockItem) throws SQLException {
        String sql = "INSERT INTO public.estoque (cod_prod, cod_barraca, qntd_estoque) " +
                     "VALUES (?, ?, ?) " +
                     "ON CONFLICT (cod_prod, cod_barraca) DO UPDATE SET qntd_estoque = EXCLUDED.qntd_estoque";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // CORREÇÃO: Modelo 'Stock' usa 'int', não 'long'
            stmt.setInt(1, stockItem.getProductCode());
            stmt.setInt(2, stockItem.getTentCode());
            stmt.setShort(3, stockItem.getStockQuantity());

            stmt.executeUpdate();
        }
    }
}