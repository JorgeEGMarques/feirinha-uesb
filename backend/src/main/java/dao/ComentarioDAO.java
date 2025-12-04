package dao;

import model.entities.Comentario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ComentarioDAO {

    public void create(Comentario c) throws SQLException {
        String sql = "INSERT INTO public.comentario (texto_comentario, cod_produto, cpf_usuario) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, c.getTexto());
            stmt.setInt(2, c.getCodProd());
            stmt.setString(3, c.getCpfUsuario());

            int rows = stmt.executeUpdate();
            if (rows == 0) throw new SQLException("Falha ao inserir comentário");

            try (ResultSet gk = stmt.getGeneratedKeys()) {
                if (gk.next()) {
                    c.setId(gk.getInt(1));
                }
            }
        }
    }

    public Comentario getById(int id) throws SQLException {
        String sql = "SELECT * FROM public.comentario WHERE id_comentario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        }
        return null;
    }

    public List<Comentario> getAll() throws SQLException {
        List<Comentario> list = new ArrayList<>();
        String sql = "SELECT * FROM public.comentario ORDER BY data_postagem DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public List<Comentario> getByProduct(int prodId) throws SQLException {
        List<Comentario> list = new ArrayList<>();
        String sql = "SELECT * FROM public.comentario WHERE cod_produto = ? ORDER BY data_postagem DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, prodId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.comentario WHERE id_comentario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    private Comentario mapRow(ResultSet rs) throws SQLException {
        Comentario c = new Comentario();
        c.setId(rs.getInt("id_comentario"));
        c.setTexto(rs.getString("texto_comentario"));
        c.setCodProd(rs.getInt("cod_produto"));
        c.setCpfUsuario(rs.getString("cpf_usuario"));
        java.sql.Timestamp ts = rs.getTimestamp("data_postagem");
        if (ts != null) c.setDataPostagem(ts.toLocalDateTime());
        return c;
    }
}
