package controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dao.SaleDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entities.Sale;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/sales/*")
public class SaleServlet extends HttpServlet {

    private ObjectMapper mapper;
    private SaleDAO saleDAO;

    @Override
    public void init() throws ServletException {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule()); 
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.saleDAO = new SaleDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);

        try {
            Sale newSale = mapper.readValue(jsonBody, Sale.class);
            
            saleDAO.create(newSale); 
            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            
            String jsonResposta = mapper.writeValueAsString(newSale);
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

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        // Captura parâmetros de filtro
        String userIdFilter = req.getParameter("userId");
        String tentIdFilter = req.getParameter("tentId");

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            // Caso 1: Busca por ID específico na URL (/api/sales/1)
            if (pathInfo != null && !pathInfo.equals("/")) {
                int id = Integer.parseInt(pathInfo.substring(1));
                Sale sale = saleDAO.getById(id);
                if (sale == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().print("{\"erro\": \"Venda não encontrada\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().print(mapper.writeValueAsString(sale));
                }
                return;
            }

            // Caso 2: Filtro por Usuário (Histórico de Compras)
            if (userIdFilter != null) {
                List<Sale> sales = saleDAO.getByUserId(userIdFilter);
                resp.getWriter().print(mapper.writeValueAsString(sales));
                return;
            }

            // Caso 3: Filtro por Barraca (Histórico de Vendas)
            if (tentIdFilter != null) {
                int tentId = Integer.parseInt(tentIdFilter);
                List<Sale> sales = saleDAO.getByTentId(tentId);
                resp.getWriter().print(mapper.writeValueAsString(sales));
                return;
            }

            // Caso 4: Retorna TUDO (padrão)
            List<Sale> sales = saleDAO.getAll();
            resp.getWriter().print(mapper.writeValueAsString(sales));

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"ID inválido\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"erro\": \"Erro de Banco de Dados: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"É preciso informar o ID da venda na URL para atualizar.\"}");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);
            Sale sale = mapper.readValue(jsonBody, Sale.class);
            sale.setId(id);
            saleDAO.update(sale);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(mapper.writeValueAsString(sale));
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"ID inválido\"}");
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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"É preciso informar o ID da venda na URL para deletar.\"}");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            saleDAO.delete(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"ID inválido\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"erro\": \"Erro de Banco de Dados: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}