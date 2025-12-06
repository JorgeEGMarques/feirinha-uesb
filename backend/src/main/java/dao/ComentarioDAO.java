package dao;

import model.entities.Comentario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Acesso a Dados (DAO) para a entidade Comentário.
 * Responsável por realizar operações de CRUD na tabela 'comentario'.
 */
public class ComentarioDAO {

    /**
     * Cria um novo comentário no banco de dados.
     * 
     * @param c O objeto Comentario contendo os dados a serem inseridos.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
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

    /**
     * Busca um comentário pelo ID.
     * 
     * @param id O ID do comentário a ser buscado.
     * @return O objeto Comentario encontrado, ou null se não existir.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
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

    /**
     * Busca todos os comentários cadastrados, ordenados por data de postagem decrescente.
     * 
     * @return Uma lista contendo todos os comentários.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
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

    /**
     * Busca todos os comentários de um produto específico.
     * 
     * @param prodId O código do produto.
     * @return Uma lista de comentários do produto.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
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

    /**
     * Deleta um comentário pelo ID.
     * 
     * @param id O ID do comentário a ser deletado.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.comentario WHERE id_comentario = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Método auxiliar para converter uma linha do ResultSet em um objeto Comentario.
     * 
     * @param rs O ResultSet posicionado na linha a ser lida.
     * @return O objeto Comentario preenchido.
     * @throws SQLException Se ocorrer um erro ao ler o ResultSet.
     */
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
