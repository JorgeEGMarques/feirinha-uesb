package dao;

import model.entities.Reservation;
import model.entities.ReservationItem;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    /**
     * Cria uma nova RESERVA e seus ITENS em uma única transação.
     */
    public void create(Reservation reservation) throws SQLException {
        
        String sqlReserva = "INSERT INTO public.reserva (cpf_titular, data_reserva, status_reserva) VALUES (?, ?, ?)";
        String sqlItem = "INSERT INTO public.item_reserva (cod_res, cod_prod, qntd_item_reserva, preco_reserva) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmtReserva = null;
        PreparedStatement stmtItem = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Inicia a transação

            // --- PASSO 1: Salvar a Reserva (Pai) ---
            stmtReserva = conn.prepareStatement(sqlReserva, Statement.RETURN_GENERATED_KEYS);
            
            stmtReserva.setString(1, reservation.getHolderCpf());
            stmtReserva.setObject(2, reservation.getReservationDate());
            stmtReserva.setString(3, reservation.getStatus());
            
            int rowsAffected = stmtReserva.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Falha ao criar reserva, nenhuma linha afetada.");
            }

            // --- PASSO 2: Pegar o ID (serial) da Reserva criada ---
            long newReservationId = -1;
            try (ResultSet generatedKeys = stmtReserva.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newReservationId = generatedKeys.getLong(1);
                    // Atualiza o objeto Java com o ID do banco
                    reservation.setCode((int) newReservationId); 
                } else {
                    throw new SQLException("Falha ao criar reserva, não obteve o ID.");
                }
            }

            // --- PASSO 3: Salvar os Itens (Filhos) ---
            if (reservation.getItems() != null && !reservation.getItems().isEmpty()) {
                
                stmtItem = conn.prepareStatement(sqlItem);
                
                for (ReservationItem item : reservation.getItems()) {
                    stmtItem.setInt(1, (int) newReservationId); // Usa o ID do Pai (cod_res é INT)
                    // CORREÇÃO: Modelo ReservationItem usa 'int', não 'long'
                    stmtItem.setInt(2, item.getProductCode()); 
                    stmtItem.setShort(3, item.getReservationitemQuantity());
                    stmtItem.setBigDecimal(4, item.getReservationPrice());
                    stmtItem.addBatch();
                }
                stmtItem.executeBatch();
            }

            conn.commit(); // Salva permanentemente
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Desfaz tudo
            }
            throw new SQLException("Erro de transação ao salvar reserva: " + e.getMessage(), e);
        } finally {
            if (stmtItem != null) stmtItem.close();
            if (stmtReserva != null) stmtReserva.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    /**
     * Busca uma reserva E SEUS ITENS pelo ID.
     */
    public Reservation getById(int code) throws SQLException {
        Reservation reservation = null;
        String sqlReserva = "SELECT * FROM public.reserva WHERE cod_reserva = ?";
        String sqlItems = "SELECT * FROM public.item_reserva WHERE cod_res = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtReserva = conn.prepareStatement(sqlReserva);
             PreparedStatement stmtItems = conn.prepareStatement(sqlItems)) {
            
            // --- PASSO 1: Buscar a Reserva (Pai) ---
            stmtReserva.setInt(1, code);
            try (ResultSet rs = stmtReserva.executeQuery()) {
                if (rs.next()) {
                    reservation = mapRowToReservation(rs);
                }
            }
            
            // --- PASSO 2: Buscar os Itens (Filhos) ---
            if (reservation != null) {
                stmtItems.setInt(1, code);
                List<ReservationItem> items = new ArrayList<>();
                
                // CORREÇÃO: Bloco estava comentado
                try (ResultSet rsItems = stmtItems.executeQuery()) {
                    while (rsItems.next()) {
                        items.add(mapRowToItem(rsItems)); 
                    }
                }
                reservation.setItems(items);
            }
        }
        return reservation;
    }

    public List<Reservation> getAll() throws SQLException {
        List<Reservation> reservations = new ArrayList<>();
        String sql = "SELECT * FROM public.reserva";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                reservations.add(mapRowToReservation(rs));
            }
        }
        return reservations;
    }

    /**
     * Atualiza APENAS o status de uma reserva.
     */
    public void updateStatus(int reservationId, String newStatus) throws SQLException {
        // CORREÇÃO: SQL e parâmetros estavam errados
        String sql = "UPDATE public.reserva SET status_reserva = ? WHERE cod_reserva = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, newStatus);
        
            stmt.setInt(2, reservationId);
            
            stmt.executeUpdate();
        }
    }

    /**
     * Deleta uma reserva e seus itens (precisa de transação).
     */
    public void delete(int code) throws SQLException { // CORREÇÃO: Era 'String code'
        String sqlItems = "DELETE FROM public.item_reserva WHERE cod_res = ?";
        String sqlReserva = "DELETE FROM public.reserva WHERE cod_reserva = ?";
        
        Connection conn = null;
        PreparedStatement stmtItems = null;
        PreparedStatement stmtReserva = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); // Inicia transação

            // 1. Deleta os "Filhos" (item_reserva)
            stmtItems = conn.prepareStatement(sqlItems);
            stmtItems.setInt(1, code);
            stmtItems.executeUpdate();

            // 2. Deleta o "Pai" (reserva)
            stmtReserva = conn.prepareStatement(sqlReserva);
            stmtReserva.setInt(1, code); // CORREÇÃO: Era 'setString'
            stmtReserva.executeUpdate();
            
            conn.commit(); // Salva
            
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); // Desfaz
            throw new SQLException("Erro de transação ao deletar reserva: " + e.getMessage(), e);
        } finally {
            if (stmtItems != null) stmtItems.close();
            if (stmtReserva != null) stmtReserva.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    // --- MÉTODO AJUDANTE (PAI) ---
    private Reservation mapRowToReservation(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setCode(rs.getInt("cod_reserva"));
        r.setHolderCpf(rs.getString("cpf_titular"));
        r.setReservationDate(rs.getObject("data_reserva", LocalDate.class));
        r.setStatus(rs.getString("status_reserva"));
        return r;
    }
    
    // --- MÉTODO AJUDANTE (FILHO) ---
    private ReservationItem mapRowToItem(ResultSet rs) throws SQLException {
        ReservationItem item = new ReservationItem();
        // CORREÇÃO: Tipos no modelo são 'int'
        item.setReservationCode(rs.getInt("cod_res"));
        item.setProductCode(rs.getInt("cod_prod"));
        item.setReservationItemQuantity(rs.getShort("qntd_item_reserva"));
        item.setReservationPrice(rs.getBigDecimal("preco_reserva"));
        return item;
    }
}