package dao;

import model.entities.Payment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate; 
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe de Acesso a Dados (DAO) para a entidade Pagamento.
 * Responsável por realizar operações de CRUD na tabela 'pagamento'.
 */
public class PaymentDAO {

    /**
     * Cria um novo pagamento no banco de dados.
     * 
     * @param payment O objeto Payment contendo os dados a serem inseridos.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void create(Payment payment) throws SQLException {
        String sql = "INSERT INTO public.pagamento (id_venda, cpf_comprador, cod_barraca, forma_pagamento, data_pagamento) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            if (payment.getSaleId() == null) {
                stmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(1, payment.getSaleId());
            }
            
            stmt.setString(2, payment.getBuyerCpf());
            stmt.setInt(3, payment.getTentCode());
            stmt.setString(4, payment.getPaymentForm());
            stmt.setObject(5, payment.getPaymentDate()); 

            int rows = stmt.executeUpdate();
            if (rows == 0) throw new SQLException("Falha ao criar pagamento, nenhuma linha afetada.");

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payment.setId(generatedKeys.getInt(1));
                } else {
                    throw new SQLException("Falha ao criar pagamento, não obteve o ID.");
                }
            }
        }
    }

    /**
     * Busca um pagamento pelo ID.
     * 
     * @param id O ID do pagamento a ser buscado.
     * @return O objeto Payment encontrado, ou null se não existir.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public Payment getById(int id) throws SQLException {
        Payment payment = null;
        String sql = "SELECT * FROM public.pagamento WHERE id_pagamento = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    payment = mapRowToPayment(rs);
                }
            }
        }
        return payment;
    }

    /**
     * Busca todos os pagamentos cadastrados.
     * 
     * @return Uma lista contendo todos os pagamentos.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public List<Payment> getAll() throws SQLException {
        List<Payment> payments = new ArrayList<>();
        String sql = "SELECT * FROM public.pagamento";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                payments.add(mapRowToPayment(rs));
            }
        }
        return payments;
    }

    /**
     * Atualiza os dados de um pagamento existente.
     * 
     * @param payment O objeto Payment com os dados atualizados.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void update(Payment payment) throws SQLException {
        String sql = "UPDATE public.pagamento SET id_venda = ?, cpf_comprador = ?, cod_barraca = ?, forma_pagamento = ?, data_pagamento = ? WHERE id_pagamento = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (payment.getSaleId() == null) {
                stmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(1, payment.getSaleId());
            }
            stmt.setString(2, payment.getBuyerCpf());
            stmt.setInt(3, payment.getTentCode());
            stmt.setString(4, payment.getPaymentForm());
            stmt.setObject(5, payment.getPaymentDate());
            stmt.setInt(6, payment.getId()); 
            
            stmt.executeUpdate();
        }
    }

    /**
     * Deleta um pagamento pelo ID.
     * 
     * @param id O ID do pagamento a ser deletado.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM public.pagamento WHERE id_pagamento = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    /**
     * Método auxiliar para converter uma linha do ResultSet em um objeto Payment.
     * 
     * @param rs O ResultSet posicionado na linha a ser lida.
     * @return O objeto Payment preenchido.
     * @throws SQLException Se ocorrer um erro ao ler o ResultSet.
     */
    private Payment mapRowToPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getInt("id_pagamento"));
        p.setSaleId(rs.getObject("id_venda", Integer.class));
        p.setBuyerCpf(rs.getString("cpf_comprador"));
        p.setTentCode(rs.getInt("cod_barraca"));
        p.setPaymentForm(rs.getString("forma_pagamento"));
        p.setPaymentDate(rs.getObject("data_pagamento", LocalDate.class));
        return p;
    }
}
