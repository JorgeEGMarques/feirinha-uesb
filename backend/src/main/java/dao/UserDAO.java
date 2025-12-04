package dao;

import model.entities.User; // Importa seu modelo
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    /**
     * Cria um novo usuário no banco de dados.
     * Mapeia os métodos do seu 'User.java' para as colunas do 'usuario'.
     */
    public void create(User user) throws SQLException {
        String sql = "INSERT INTO public.usuario (cpf_usuario, nome_usuario, tel_usuario, email, senha, foto_perfil) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getCpf());
            stmt.setString(2, user.getNome());
            stmt.setString(3, user.getTelefone());
            stmt.setString(4, user.getEmail());
            stmt.setString(5, user.getSenha());
            if (user.getFotoPerfil() != null) {
                stmt.setBytes(6, user.getFotoPerfil());
            } else {
                stmt.setNull(6, java.sql.Types.BINARY);
            }
            
            stmt.executeUpdate();
        }
    }

    /**
     * Busca um usuário pelo CPF.
     */
    public User getByCpf(String cpf) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM public.usuario WHERE cpf_usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cpf);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapRowToUser(rs);
                }
            }
        }
        return user;
    }

    /**
     * Busca todos os usuários.
     */
    public List<User> getAll() throws SQLException {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM public.usuario";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }
        }
        return users;
    }

    /**
     * Atualiza um usuário.
     */
    public void update(User user) throws SQLException {
        String sql = "UPDATE public.usuario SET nome_usuario = ?, tel_usuario = ?, email = ?, senha = ?, foto_perfil = ? WHERE cpf_usuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, user.getNome());
            stmt.setString(2, user.getTelefone());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, user.getSenha());
            if (user.getFotoPerfil() != null) {
                stmt.setBytes(5, user.getFotoPerfil());
            } else {
                stmt.setNull(5, java.sql.Types.BINARY);
            }
            stmt.setString(6, user.getCpf());
            
            stmt.executeUpdate();
        }
    }

    /**
     * Deleta um usuário pelo CPF.
     */
    public void delete(String cpf) throws SQLException {
        String sql = "DELETE FROM public.usuario WHERE cpf_usuario = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cpf);
            stmt.executeUpdate();
        }
    }

    /**
     * Método Ajudante: Converte uma linha do SQL (ResultSet) para um objeto Java (User).
     */
    private User mapRowToUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setCpf(rs.getString("cpf_usuario"));
        user.setNome(rs.getString("nome_usuario"));
        user.setTelefone(rs.getString("tel_usuario"));
        user.setEmail(rs.getString("email"));
        user.setSenha(rs.getString("senha"));
        byte[] foto = rs.getBytes("foto_perfil");
        user.setFotoPerfil(foto);
        return user;
    }
}