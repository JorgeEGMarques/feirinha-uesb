package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.StockDAO;
import model.entities.Stock;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet responsável por gerenciar as requisições HTTP relacionadas ao Estoque.
 * Mapeado para /api/stock.
 */
@WebServlet(urlPatterns = {"/api/stock", "/api/stock/*"})
public class StockServlet extends HttpServlet {

    private ObjectMapper mapper;
    private StockDAO stockDAO;

    /**
     * Inicializa o servlet, configurando o ObjectMapper e o DAO.
     * 
     * @throws ServletException Se ocorrer um erro na inicialização.
     */
    @Override
    public void init() throws ServletException {
        this.stockDAO = new StockDAO();
        this.mapper = new ObjectMapper();
    }

    /**
     * Processa requisições HTTP POST para adicionar ou atualizar estoque (Upsert).
     * 
     * @param req A requisição HTTP contendo o JSON do estoque.
     * @param resp A resposta HTTP.
     * @throws ServletException Se ocorrer um erro no servlet.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Stock stock = mapper.readValue(req.getReader(), Stock.class);
            
            if (stock.getStockQuantity() <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print("{\"erro\": \"Quantidade deve ser maior que zero.\"}");
                return;
            }
            
            stockDAO.save(stock);
            
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().print(mapper.writeValueAsString(stock));
            
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("Erro SQL: " + e.getMessage());
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"JSON inválido\"}");
        }
    }

    /**
     * Processa requisições HTTP GET para listar o estoque de uma barraca.
     * Requer o parâmetro ?tentId=X na URL.
     * 
     * @param req A requisição HTTP.
     * @param resp A resposta HTTP.
     * @throws ServletException Se ocorrer um erro no servlet.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String tentIdStr = req.getParameter("tentId");
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (tentIdStr != null) {
                int tentId = Integer.parseInt(tentIdStr);
                List<Stock> stocks = stockDAO.getByTentId(tentId);
                resp.getWriter().print(mapper.writeValueAsString(stocks));
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print("{\"erro\": \"Informe o parâmetro ?tentId=X na URL.\"}");
            }
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"ID da barraca inválido.\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("Erro SQL: " + e.getMessage());
        }
    }
}
