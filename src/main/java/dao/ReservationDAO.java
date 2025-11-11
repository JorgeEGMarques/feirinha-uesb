package dao;

import model.entities.Reservation;
import model.entities.ReservationItem; // Importe o Item
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement; // Importe para pegar o ID gerado
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReservationDAO {

    /**
     * Cria uma nova RESERVA e seus ITENS em uma única transação.
     * Isso usa a lógica de Transação que eu te mostrei antes.
     */
    public void create(Reservation reservation) throws SQLException {
        
        // 1. SQL CORRETO para a tabela 'reserva'
        // Note que o 'cod_reserva' (serial) não está aqui, pois o banco o gera.
        String sqlReserva = "INSERT INTO public.reserva (cpf_titular, data_reserva, status_reserva) VALUES (?, ?, ?)";
        
        // 2. SQL CORRETO para a tabela 'item_reserva'
        String sqlItem = "INSERT INTO public.item_reserva (cod_res, cod_prod, qntd_item_reserva, preco_reserva) VALUES (?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement stmtReserva = null;
        PreparedStatement stmtItem = null;

        try {
            conn = DatabaseConnection.getConnection();
            
            // --- INÍCIO DA TRANSAÇÃO ---
            conn.setAutoCommit(false); // Desliga o Auto-Commit

            // --- PASSO 1: Salvar a Reserva (Pai) ---
            // Pedimos ao JDBC para nos retornar a chave gerada (o 'cod_reserva')
            stmtReserva = conn.prepareStatement(sqlReserva, Statement.RETURN_GENERATED_KEYS);
            
            // Mapeamento correto dos parâmetros (agora com índices corretos)
            stmtReserva.setString(1, reservation.getHolderCpf());
            stmtReserva.setObject(2, reservation.getReservationDate());
            stmtReserva.setString(3, reservation.getStatus()); // Usa o novo campo 'status'
            
            int rowsAffected = stmtReserva.executeUpdate();
            
            if (rowsAffected == 0) {
                throw new SQLException("Falha ao criar reserva, nenhuma linha afetada.");
            }

            // --- PASSO 2: Pegar o ID da Reserva que acabou de ser criada ---
            long newReservationId = -1;
            try (ResultSet generatedKeys = stmtReserva.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    newReservationId = generatedKeys.getLong(1);
                    // Atualiza o objeto Java com o novo ID gerado pelo banco
                    reservation.setCode((int) newReservationId); 
                } else {
                    throw new SQLException("Falha ao criar reserva, não obteve o ID.");
                }
            }

            // --- PASSO 3: Salvar os Itens (Filhos) ---
            // Verifica se a lista de itens não é nula e não está vazia
            if (reservation.getItems() != null && !reservation.getItems().isEmpty()) {
                
                stmtItem = conn.prepareStatement(sqlItem);
                
                for (ReservationItem item : reservation.getItems()) {
                    stmtItem.setLong(1, newReservationId); // Usa o ID do Pai
                    stmtItem.setLong(2, item.getProductCode());
                    stmtItem.setShort(3, item.getReservationitemQuantity());
                    stmtItem.setBigDecimal(4, item.getReservationPrice());
                    stmtItem.addBatch(); // Adiciona o INSERT ao "lote"
                }
                stmtItem.executeBatch(); // Executa todos os INSERTs dos itens de uma vez
            }

            // --- FIM DA TRANSAÇÃO ---
            conn.commit(); // Se tudo deu certo, COMITA (salva permanentemente)
            
        } catch (SQLException e) {
            if (conn != null) {
                conn.rollback(); // Se deu erro, DESFAZ TUDO (rollback)
            }
            // Re-lança o erro para o Servlet saber que falhou
            throw new SQLException("Erro de transação ao salvar reserva: " + e.getMessage(), e);
        } finally {
            // Fecha tudo na ordem inversa
            if (stmtItem != null) stmtItem.close();
            if (stmtReserva != null) stmtReserva.close();
            if (conn != null) {
                conn.setAutoCommit(true); // Devolve a conexão ao normal
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

        // Usamos um único try-with-resources para a conexão e os dois statements
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmtReserva = conn.prepareStatement(sqlReserva);
             PreparedStatement stmtItems = conn.prepareStatement(sqlItems)) {
            
            // --- PASSO 1: Buscar a Reserva (Pai) ---
            stmtReserva.setInt(1, code);
            try (ResultSet rs = stmtReserva.executeQuery()) {
                if (rs.next()) {
                    reservation = mapRowToReservation(rs); // Usa o método ajudante
                }
            }
            
            // --- PASSO 2: Buscar os Itens (Filhos) ---
            // Se encontramos a reserva, buscamos seus itens
            if (reservation != null) {
                stmtItems.setInt(1, code);
                List<ReservationItem> items = new ArrayList<>();
                
                /*try (ResultSet rsItems = stmtItems.executeQuery()) {
                    while (rsItems.next()) {
                        items.add(mapRowToItem(rsItems)); // Usa o novo ajudante de item
                    }
                }*/
                reservation.setItems(items); // Adiciona a lista de itens ao objeto
            }
        }
        return reservation; // Retorna a reserva completa (com itens), ou null
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
    
    /*  --- MÉTODO AJUDANTE (FILHO) ---
    // (Presume que você tem a classe ReservationItem.java com os setters corretos)
    private ReservationItem mapRowToItem(ResultSet rs) throws SQLException {
        ReservationItem item = new ReservationItem();
        item.setReservationCode(rs.getLong("cod_res"));
        item.setProductCode(rs.getLong("cod_prod"));
        item.setReservationitemQuantity(rs.getShort("qntd_item_reserva"));
        item.setReservationPrice(rs.getBigDecimal("preco_reserva"));
        return item;
    }*/
}