package dao;

import model.entities.User; 
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Acesso a Dados (DAO) para a entidade Usuário.
 * Responsável por realizar operações de CRUD na tabela 'usuario'.
 */
public class UserDAO {

    /**
     * Cria um novo usuário no banco de dados.
     * 
     * @param user O objeto User contendo os dados a serem inseridos.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
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
     * 
     * @param cpf O CPF do usuário a ser buscado.
     * @return O objeto User encontrado, ou null se não existir.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
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
     * Busca todos os usuários cadastrados.
     * 
     * @return Uma lista contendo todos os usuários.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
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
     * Atualiza os dados de um usuário existente.
     * 
     * @param user O objeto User com os dados atualizados.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
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
     * Valida o login do usuário (por Email).
     * 
     * @param email O Email do usuário.
     * @param senha A senha do usuário.
     * @return O objeto User se as credenciais estiverem corretas, ou null caso contrário.
     * @throws SQLException Se ocorrer um erro no banco.
     */
    public User validateLogin(String email, String senha) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM public.usuario WHERE email = ? AND senha = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, email);
            stmt.setString(2, senha);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    user = mapRowToUser(rs);
                }
            }
        }
        return user;
    }

    /**
     * Deleta um usuário pelo CPF.
     * 
     * @param cpf O CPF do usuário a ser deletado.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
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
     * Método auxiliar para converter uma linha do ResultSet em um objeto User.
     * 
     * @param rs O ResultSet posicionado na linha a ser lida.
     * @return O objeto User preenchido.
     * @throws SQLException Se ocorrer um erro ao ler o ResultSet.
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

        try {
            dao.TentDAO tentDAO = new dao.TentDAO();
            user.setTents(tentDAO.getByOwnerCpf(user.getCpf()));
        } catch (SQLException e) {
            throw e;
        }
        return user;
    }
}
