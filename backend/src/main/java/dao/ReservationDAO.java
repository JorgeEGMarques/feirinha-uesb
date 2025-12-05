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

/**
 * Classe de Acesso a Dados (DAO) para a entidade Reserva.
 * Responsável por realizar operações de CRUD na tabela 'reserva' e 'item_reserva'.
 */
public class ReservationDAO {

    /**
     * Cria uma nova RESERVA e seus ITENS em uma única transação.
     * 
     * @param reservation O objeto Reservation contendo os dados da reserva e seus itens.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void create(Reservation reservation) throws SQLException {
        
        String sqlReserva = "INSERT INTO public.reserva (cpf_titular, data_reserva, status_reserva) VALUES (?, ?, ?)";
        String sqlItem = "INSERT INTO public.item_reserva (cod_res, cod_prod, qntd_item_reserva, preco_reserva) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmtReserva = null;
        PreparedStatement stmtItem = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); 

            stmtReserva = conn.prepareStatement(sqlReserva, Statement.RETURN_GENERATED_KEYS);
            
            stmtReserva.setString(1, reservation.getHolderCpf());
            stmtReserva.setObject(2, reservation.getReservationDate());
            stmtReserva.setString(3, reservation.getStatus());
            
            int rowsAffected = stmtReserva.executeUpdate();
            if (rowsAffected == 0) {
                throw new SQLException("Falha ao criar reserva, nenhuma linha afetada.");
            }

            long newReservationId = -1;
            try (ResultSet generatedKeys = stmtReserva.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newReservationId = generatedKeys.getLong(1);
                    reservation.setCode((int) newReservationId); 
                } else {
                    throw new SQLException("Falha ao criar reserva, não obteve o ID.");
                }
            }

            if (reservation.getItems() != null && !reservation.getItems().isEmpty()) {
                
                stmtItem = conn.prepareStatement(sqlItem);
                
                for (ReservationItem item : reservation.getItems()) {
                    stmtItem.setInt(1, (int) newReservationId); 
                    stmtItem.setInt(2, item.getProductCode()); 
                    stmtItem.setShort(3, item.getReservationitemQuantity());
                    stmtItem.setBigDecimal(4, item.getReservationPrice());
                    stmtItem.addBatch();
                }
                stmtItem.executeBatch();
            }

            conn.commit(); 
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); 
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
     * 
     * @param code O código da reserva a ser buscada.
     * @return O objeto Reservation encontrado, ou null se não existir.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public Reservation getById(int code) throws SQLException {
        Reservation reservation = null;
        String sqlReserva = "SELECT * FROM public.reserva WHERE cod_reserva = ?";
        String sqlItems = "SELECT * FROM public.item_reserva WHERE cod_res = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtReserva = conn.prepareStatement(sqlReserva);
             PreparedStatement stmtItems = conn.prepareStatement(sqlItems)) {

            stmtReserva.setInt(1, code);
            try (ResultSet rs = stmtReserva.executeQuery()) {
                if (rs.next()) {
                    reservation = mapRowToReservation(rs);
                }
            }

            if (reservation != null) {
                stmtItems.setInt(1, code);
                List<ReservationItem> items = new ArrayList<>();

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

    /**
     * Busca todas as reservas cadastradas.
     * 
     * @return Uma lista contendo todas as reservas.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
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
     * 
     * @param reservationId O ID da reserva.
     * @param newStatus O novo status da reserva.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void updateStatus(int reservationId, String newStatus) throws SQLException {

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
     * 
     * @param code O código da reserva a ser deletada.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void delete(int code) throws SQLException { // CORREÇÃO: Era 'String code'
        String sqlItems = "DELETE FROM public.item_reserva WHERE cod_res = ?";
        String sqlReserva = "DELETE FROM public.reserva WHERE cod_reserva = ?";
        
        Connection conn = null;
        PreparedStatement stmtItems = null;
        PreparedStatement stmtReserva = null;
        
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false); 

            stmtItems = conn.prepareStatement(sqlItems);
            stmtItems.setInt(1, code);
            stmtItems.executeUpdate();

            stmtReserva = conn.prepareStatement(sqlReserva);
            stmtReserva.setInt(1, code); 
            stmtReserva.executeUpdate();
            
            conn.commit(); 
            
        } catch (SQLException e) {
            if (conn != null) conn.rollback(); 
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

    /**
     * Atualiza a reserva (pai) e substitui todos os itens (filhos) em uma única transação.
     * 
     * @param reservation O objeto Reservation com os dados atualizados.
     * @throws SQLException Se ocorrer um erro ao acessar o banco de dados.
     */
    public void update(Reservation reservation) throws SQLException {
        String sqlUpdateReserva = "UPDATE public.reserva SET cpf_titular = ?, data_reserva = ?, status_reserva = ? WHERE cod_reserva = ?";
        String sqlDeleteItems = "DELETE FROM public.item_reserva WHERE cod_res = ?";
        String sqlInsertItem = "INSERT INTO public.item_reserva (cod_res, cod_prod, qntd_item_reserva, preco_reserva) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmtUpdate = null;
        PreparedStatement stmtDeleteItems = null;
        PreparedStatement stmtInsertItem = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            stmtUpdate = conn.prepareStatement(sqlUpdateReserva);
            stmtUpdate.setString(1, reservation.getHolderCpf());
            stmtUpdate.setObject(2, reservation.getReservationDate());
            stmtUpdate.setString(3, reservation.getStatus());
            stmtUpdate.setInt(4, reservation.getCode());
            stmtUpdate.executeUpdate();

            stmtDeleteItems = conn.prepareStatement(sqlDeleteItems);
            stmtDeleteItems.setInt(1, reservation.getCode());
            stmtDeleteItems.executeUpdate();

            if (reservation.getItems() != null && !reservation.getItems().isEmpty()) {
                stmtInsertItem = conn.prepareStatement(sqlInsertItem);
                for (ReservationItem item : reservation.getItems()) {
                    stmtInsertItem.setInt(1, reservation.getCode());
                    stmtInsertItem.setInt(2, item.getProductCode());
                    stmtInsertItem.setShort(3, item.getReservationitemQuantity());
                    stmtInsertItem.setBigDecimal(4, item.getReservationPrice());
                    stmtInsertItem.addBatch();
                }
                stmtInsertItem.executeBatch();
            }

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) conn.rollback();
            throw new SQLException("Erro de transação ao atualizar reserva: " + e.getMessage(), e);
        } finally {
            if (stmtInsertItem != null) stmtInsertItem.close();
            if (stmtDeleteItems != null) stmtDeleteItems.close();
            if (stmtUpdate != null) stmtUpdate.close();
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        }
    }

    private Reservation mapRowToReservation(ResultSet rs) throws SQLException {
        Reservation r = new Reservation();
        r.setCode(rs.getInt("cod_reserva"));
        r.setHolderCpf(rs.getString("cpf_titular"));
        r.setReservationDate(rs.getObject("data_reserva", LocalDate.class));
        r.setStatus(rs.getString("status_reserva"));
        return r;
    }
    
    private ReservationItem mapRowToItem(ResultSet rs) throws SQLException {
        ReservationItem item = new ReservationItem();
        item.setReservationCode(rs.getInt("cod_res"));
        item.setProductCode(rs.getInt("cod_prod"));
        item.setReservationItemQuantity(rs.getShort("qntd_item_reserva"));
        item.setReservationPrice(rs.getBigDecimal("preco_reserva"));
        return item;
    }
}
