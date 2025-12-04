package controller;

// Imports do Jackson (para JSON)
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dao.PaymentDAO;
import model.entities.Payment;

// Imports do Servlet (JAKARTA)
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.sql.SQLException;
import java.util.List;
import java.io.IOException;

@WebServlet(urlPatterns = {"/api/payments", "/api/payments/*"}) // URL em português
public class PaymentServlet extends HttpServlet {

    private ObjectMapper mapper; // 1. Mova a inicialização para o init()
    private PaymentDAO paymentDAO;

    @Override
    public void init() throws ServletException {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule()); 
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.paymentDAO = new PaymentDAO(); // DAO é inicializado
    }

    // --- CREATE (Criar) ---
    // POST /api/payments
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);

        try {
            Payment newPayment = mapper.readValue(jsonBody, Payment.class);
            
              // Validação (Barreira 1) - não exigir 'id' (gera o DB). Exigir campos obrigatórios mínimos.
              if (newPayment.getBuyerCpf() == null || newPayment.getPaymentForm() == null || newPayment.getPaymentDate() == null || newPayment.getTentCode() == null) {
                  resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                  resp.getWriter().print("{\"erro\": \"Falta de campos obrigatórios: cpf_comprador, forma_pagamento, data_pagamento, cod_barraca.\"}");
                  return;
              }

            // --- LÓGICA DO BANCO (ATIVA) ---
            paymentDAO.create(newPayment); 

            // --- FIM DA LÓGICA DO BANCO ---
            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED); // 201
            
            String jsonResposta = mapper.writeValueAsString(newPayment);
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
    // GET /api/payments  (Listar todos)
    // GET /api/payments/1 (Buscar por ID)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String pathInfo = req.getPathInfo(); // Pega o que vem depois de "/api/payments"
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // --- Rota 1: Listar Todos (/api/payments) ---
                List<Payment> payments = paymentDAO.getAll();
                resp.getWriter().print(mapper.writeValueAsString(payments));
            
            } else {
                // --- Rota 2: Buscar Um por ID (/api/payments/1) ---
                
                // Extrai o "1" da URL "/1"
                int id = Integer.parseInt(pathInfo.substring(1));
                Payment payment = paymentDAO.getById(id);
                
                if (payment == null) {
                    // Não encontrou
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                    resp.getWriter().print("{\"erro\": \"Pagamento não encontrado\"}");
                } else {
                    // Encontrou, retorna o produto
                    resp.setStatus(HttpServletResponse.SC_OK); // 200
                    resp.getWriter().print(mapper.writeValueAsString(payment));
                }
            }
        } catch (NumberFormatException e) {
            // Se a URL for /api/payments/abc (inválido)
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"ID de pagamento inválido.\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"erro\": \"Erro de Banco de Dados: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    // --- UPDATE (Atualizar) ---
    // PUT /api/payments/1
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
            int id = Integer.parseInt(pathInfo.substring(1));
            String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);
            
            Payment payment = mapper.readValue(jsonBody, Payment.class);
            
            // 2. Garante que o ID do objeto é o mesmo da URL
            payment.setId(id); 
            
            // 3. (Validação, igual ao POST - opcional mas recomendado)
            
            // 4. Chama o DAO
            paymentDAO.update(payment);
            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK); // 200
            resp.getWriter().print(mapper.writeValueAsString(payment)); // Retorna o objeto atualizado
            
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"ID de produto inválido.\"}");
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

    // --- DELETE (Apagar) ---
    // DELETE /api/payments/1
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"É preciso informar o ID do pagamento na URL para deletar.\"}");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            
            // Chama o DAO
            paymentDAO.delete(id);
            
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