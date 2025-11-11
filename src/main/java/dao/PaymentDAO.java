package dao;

import model.entities.Payment;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate; // Importe o LocalDate

public class PaymentDAO {

    /**
     * Cria um novo pagamento no banco de dados.
     */
    public void create(Payment payment) throws SQLException {
        String sql = "INSERT INTO public.pagamento (id_pagamento, id_venda, cod_reserva, cpf_comprador, cod_barraca, forma_pagamento, data_pagamento) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, payment.getId());
            
            // --- LÓGICA CORRIGIDA ---
            // Agora checamos por 'null' (Objeto), não por '0' (primitivo)
            if (payment.getSaleId() == null) {
                stmt.setNull(2, java.sql.Types.INTEGER);
            } else {
                stmt.setInt(2, payment.getSaleId());
            }

            if (payment.getReservationCode() == null) {
                 stmt.setNull(3, java.sql.Types.INTEGER);
            } else {
                 // SQL 'cod_reserva' é INT, nosso modelo é Long. Fazemos o cast.
                stmt.setInt(3, payment.getReservationCode().intValue());
            }
            
            stmt.setString(4, payment.getBuyerCpf());
            stmt.setInt(5, payment.getTentCode());
            stmt.setString(6, payment.getPaymentForm());
            
            // Use setObject para tipos java.time.LocalDate
            stmt.setObject(7, payment.getPaymentDate());
            
            stmt.executeUpdate(); // Executa o INSERT
        }
    }

    /**
     * Busca um pagamento pelo ID.
     */
    public Payment getById(int id) throws SQLException { // CORREÇÃO: ID é 'int'
        Payment payment = null;
        String sql = "SELECT * FROM public.pagamento WHERE id_pagamento = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id); // CORREÇÃO: ID é 'int'
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    payment = mapRowToPayment(rs); // Usa o método ajudante
                }
            }
        }
        return payment; // Retorna o pagamento encontrado, ou null
    }

    // MÉTODO AJUDANTE: Converte uma linha do ResultSet em um objeto Payment
    private Payment mapRowToPayment(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setId(rs.getInt("id_pagamento"));
        
        // --- LÓGICA CORRIGIDA ---
        // Usa getObject para checar por NULL do banco
        p.setSaleId(rs.getObject("id_venda", Integer.class));
        Long reservationCode = rs.getObject("cod_reserva", Long.class);
        p.setReservationCode(reservationCode);
        // --- FIM DA CORREÇÃO ---

        p.setBuyerCpf(rs.getString("cpf_comprador"));
        p.setTentCode(rs.getInt("cod_barraca"));
        p.setPaymentForm(rs.getString("forma_pagamento"));
        p.setPaymentDate(rs.getObject("data_pagamento", LocalDate.class));
        return p;
    }
    
    // ... (Aqui você criaria os métodos update() e delete() depois) ...
}