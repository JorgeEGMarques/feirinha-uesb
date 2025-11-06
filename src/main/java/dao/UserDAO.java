package dao;

import model.entities.User; // Importa seu modelo
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    /**
     * Cria um novo usuário no banco de dados.
     * Chamado pelo doPost() do Servlet.
     */
    public void create(User user) throws SQLException {
        // Usamos "public.usuario" para ter certeza,
        // e os nomes das colunas do seu script SQL.
        String sql = "INSERT INTO public.usuario (cpf_usuario, nome_usuario, tel_usuario) VALUES (?, ?, ?)";
        
        // try-with-resources garante que a conexão e o statement serão fechados.
        // Ele chama o seu conector para pegar a conexão!
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Mapeia os dados do objeto User para os '?' do SQL
            // user.getCpf() (do Java) -> coluna 1 (cpf_usuario)
            stmt.setString(1, user.getCpf());
            // user.getNome() (do Java) -> coluna 2 (nome_usuario)
            stmt.setString(2, user.getNome());
            // user.getTelefone() (do Java) -> coluna 3 (tel_usuario)
            stmt.setString(3, user.getTelefone());
            
            stmt.executeUpdate(); // Executa o INSERT
        }
    }

    /**
     * Busca um usuário pelo CPF.
     * (Você vai precisar disso para o doGet() em breve)
     */
    public User getByCpf(String cpf) throws SQLException {
        User user = null;
        String sql = "SELECT * FROM public.usuario WHERE cpf_usuario = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, cpf);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    // Mapeia o resultado do banco para um objeto Java
                    user = new User();
                    user.setCpf(rs.getString("cpf_usuario"));
                    user.setNome(rs.getString("nome_usuario"));
                    user.setTelefone(rs.getString("tel_usuario"));
                }
            }
        }
        return user; // Retorna o usuário encontrado, ou null se não encontrar
    }
    
    // ... (Aqui você criaria os métodos update() e delete() depois) ...
}