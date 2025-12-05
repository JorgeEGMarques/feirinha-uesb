package dao;

import model.entities.Payment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate; 
import java.util.ArrayList;
import java.util.List;

public class PaymentDAO {

    /**
     * Cria um novo pagamento no banco de dados.
     */
    public void create(Payment payment) throws SQLException {
        String sql = "INSERT INTO public.pagamento (id_pagamento, id_venda, cod_reserva, cpf_comprador, cod_barraca, forma_pagamento, data_pagamento) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, payment.getId());
            
            // Lógica correta para campos NULÁVEIS (Integer)
            if (payment.getSaleId() == null) {
                stmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(2, payment.getSaleId());
            }

            if (payment.getReservationCode() == null) {
                 stmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                 // CORREÇÃO: O modelo Payment usa 'Integer', não 'Long'
                 stmt.setInt(3, payment.getReservationCode());
            }
            
            stmt.setString(4, payment.getBuyerCpf());
            stmt.setInt(5, payment.getTentCode());
            stmt.setString(6, payment.getPaymentForm());
            stmt.setObject(7, payment.getPaymentDate()); // setObject é o correto para LocalDate
            
            stmt.executeUpdate();
        }
    }

    /**
     * Busca um pagamento pelo ID.
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
     * Busca todos os pagamentos.
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
     * Atualiza um pagamento. (Não é comum, mas completa o CRUD)
     */
    public void update(Payment payment) throws SQLException {
        String sql = "UPDATE public.pagamento SET id_venda = ?, cod_reserva = ?, cpf_comprador = ?, cod_barraca = ?, forma_pagamento = ?, data_pagamento = ? WHERE id_pagamento = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            if (payment.getSaleId() == null) {
                stmt.setNull(1, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(1, payment.getSaleId());
            }
            if (payment.getReservationCode() == null) {
                 stmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                 stmt.setInt(2, payment.getReservationCode());
            }
            stmt.setString(3, payment.getBuyerCpf());
            stmt.setInt(4, payment.getTentCode());
            stmt.setString(5, payment.getPaymentForm());
            stmt.setObject(6, payment.getPaymentDate());
            stmt.setInt(7, payment.getId()); // WHERE clause
            
            stmt.executeUpdate();
        }
    }

    /**
     * Deleta um pagamento pelo ID.
     */
    public void delete(int id) throws SQLException { // CORREÇÃO: Era 'String id'
        String sql = "DELETE FROM public.pagamento WHERE id_pagamento = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id); // CORREÇÃO: Era 'setString'
            stmt.executeUpdate();
        }
    }

    // MÉTODO AJUDANTE
    private Payment mapRowToPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getInt("id_pagamento"));
        p.setSaleId(rs.getObject("id_venda", Integer.class));
        // CORREÇÃO: O modelo Payment usa 'Integer'
        p.setReservationCode(rs.getObject("cod_reserva", Integer.class)); 
        p.setBuyerCpf(rs.getString("cpf_comprador"));
        p.setTentCode(rs.getInt("cod_barraca"));
        p.setPaymentForm(rs.getString("forma_pagamento"));
        p.setPaymentDate(rs.getObject("data_pagamento", LocalDate.class));
        return p;
    }
}