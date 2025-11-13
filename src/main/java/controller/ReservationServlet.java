package controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
// Imports do Jackson (para JSON)
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dao.ReservationDAO;

// Imports do Servlet (JAKARTA)
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entities.Reservation;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/reservations/*")
public class ReservationServlet extends HttpServlet {

    private ObjectMapper mapper;
    private ReservationDAO reservationDAO;

    @Override
    public void init() throws ServletException {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule()); 
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.reservationDAO = new ReservationDAO(); // DAO é inicializado
    }

    // --- CREATE (Criar) ---
    // POST /api/reservation
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);

        try {
            Reservation newReservation = mapper.readValue(jsonBody, Reservation.class);

            /* 
            // Validação (Barreira 1) - O Servlet protege o DAO
            if (newReservation.getCode() == null || newReservation.getBuyerCpf() == null || newReservation.getReservationForm() == null || newReservation.getReservationDate() == null) {
                 resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                 resp.getWriter().print("{\"erro\": \"Falta de campos obrigatórios.\"}");
                 return;
            }*/

            // --- LÓGICA DO BANCO (ATIVA) ---
            reservationDAO.create(newReservation); 

            // --- FIM DA LÓGICA DO BANCO ---
            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED); // 201
            
            String jsonResposta = mapper.writeValueAsString(newReservation);
            resp.getWriter().print(jsonResposta);

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
            resp.getWriter().print("{\"erro\": \"Erro de Banco de Dados: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
            resp.getWriter().print("{\"erro\": \"JSON inválido. " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    // --- READ (Ler) ---
    // GET /api/reservations  (Listar todos)
    // GET /api/reservations/1 (Buscar por ID)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String pathInfo = req.getPathInfo(); // Pega o que vem depois de "/api/reservations"
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // --- Rota 1: Listar Todos (/api/reservations) ---
                List<Reservation> reservations = reservationDAO.getAll();
                resp.getWriter().print(mapper.writeValueAsString(reservations));
            
            } else {
                // --- Rota 2: Buscar Um por ID (/api/reservations/1) ---
                
                // Extrai o "1" da URL "/1"
                int code = Integer.parseInt(pathInfo.substring(1));
                Reservation reservation = reservationDAO.getById(code);
                
                if (reservation == null) {
                    // Não encontrou
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                    resp.getWriter().print("{\"erro\": \"Pagamento não encontrado\"}");
                } else {
                    // Encontrou, retorna o produto
                    resp.setStatus(HttpServletResponse.SC_OK); // 200
                    resp.getWriter().print(mapper.writeValueAsString(reservation));
                }
            }
        } catch (NumberFormatException e) {
            // Se a URL for /api/reservations/abc (inválido)
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"ID de pagamento inválido.\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"erro\": \"Erro de Banco de Dados: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    // --- UPDATE (Atualizar) ---
    // PUT /api/reservations/1
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        // 1. PUT precisa de um ID na URL
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"É preciso informar o ID do pagamento na URL para atualizar.\"}");
            return;
        }

        try {
            int code = Integer.parseInt(pathInfo.substring(1));
            String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);
            
            Reservation reservation = mapper.readValue(jsonBody, Reservation.class);
            
            // 2. Garante que o ID do objeto é o mesmo da URL
            reservation.setCode(code); 
            
            // 3. (Validação, igual ao POST - opcional mas recomendado)
            
            // 4. Chama o DAO
            
            // reservationDAO.update(reservation);
            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK); // 200
            resp.getWriter().print(mapper.writeValueAsString(reservation)); // Retorna o objeto atualizado
            
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"ID de produto inválido.\"}");
        } /* catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"erro\": \"Erro de Banco de Dados: " + e.getMessage() + "\"}");
            e.printStackTrace();*/
        catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"JSON inválido. " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    // --- DELETE (Apagar) ---
    // DELETE /api/reservations/1
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"É preciso informar o ID do pagamento na URL para deletar.\"}");
            return;
        }

        try {
            int code = Integer.parseInt(pathInfo.substring(1));
            
            // Chama o DAO
            reservationDAO.delete(code);
            
            // Resposta de Delete não tem corpo (corpo vazio)
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
            
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"ID de produto inválido.\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"erro\": \"Erro de Banco de Dados: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}