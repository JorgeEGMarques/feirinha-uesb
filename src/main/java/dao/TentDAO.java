package dao;

import model.entities.Stock;
import model.entities.Tent; // Importa seu modelo
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TentDAO {

    /**
     * Cria uma nova barraca no banco de dados.
     */
    public void create(Tent tent) throws SQLException {
        String sql = "INSERT INTO public.barraca (cod_barraca, cpf_dono, nome_barraca, licensa_usuario) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, tent.getCode());
            stmt.setString(2, tent.getCpfHolder());
            stmt.setString(3, tent.getName());
            
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

    // MÉTODO AJUDANTE: Mapeia a linha para o objeto
    private Tent mapRowToTent(ResultSet rs) throws SQLException {
        Tent t = new Tent();
        t.setCode(rs.getInt("cod_barraca"));
        t.setCpfHolder(rs.getString("cpf_dono"));
        t.setName(rs.getString("nome_barraca"));
        
        return t;
    }

    // Este método vai INSERIR o estoque se não existir, ou ATUALIZAR se já existir.
    public void updateStock(Stock stockItem) throws SQLException {
        // O comando "ON CONFLICT ... DO UPDATE" é o "UPSERT" do PostgreSQL
        String sql = "INSERT INTO public.estoque (cod_prod, cod_barraca, qntd_estoque) " +
                    "VALUES (?, ?, ?) " +
                    "ON CONFLICT (cod_prod, cod_barraca) DO UPDATE SET qntd_estoque = EXCLUDED.qntd_estoque";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setLong(1, stockItem.getProductCode());
            stmt.setLong(2, stockItem.getTentCode());
            stmt.setShort(3, stockItem.getStockQuantity());

            stmt.executeUpdate();
        }
    }
    
    // ... (Aqui você criaria os métodos update() e delete() depois) ...
}