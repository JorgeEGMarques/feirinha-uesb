package controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
// Imports do Jackson (para JSON)
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dao.SaleDAO;
// Imports do Servlet (JAKARTA)
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entities.Sale;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/api/sales/*")
public class SaleServlet extends HttpServlet {

    private ObjectMapper mapper;
    private SaleDAO saleDAO;

    @Override // 2. Assinatura correta do init()
    public void init() throws ServletException {
        
        // 3. Crie e configure o mapper AQUI
        this.mapper = new ObjectMapper();
        
        // Ensina o mapper a ler/escrever java.time.LocalDate
        this.mapper.registerModule(new JavaTimeModule()); 
        
        // Diz ao mapper para não falhar se o JSON tiver campos a mais
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 4. REMOVEMOS A LINHA DO ERRO DAQUI
        
        // Inicialize seu DAO
        this.saleDAO = new SaleDAO();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);

        try {
            // 5. Agora o mapper.readValue usará o mapper configurado
            Sale newSale = mapper.readValue(jsonBody, Sale.class);
            
            // --- LÓGICA DO BANCO DE DADOS (VAI SER O PRÓXIMO PASSO) ---
            
            // (Descomente esta linha quando estiver pronto)
            // 1. DESCOMENTE A LINHA ABAIXO PARA SALVAR NO BANCO
            saleDAO.create(newSale); 

            // --- FIM DA LÓGICA DO BANCO ---
            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            
            // 6. Envie a resposta UMA VEZ
            String jsonResposta = mapper.writeValueAsString(newSale);
            resp.getWriter().print(jsonResposta);

        // 2. DESCOMENTE ESTE BLOCO CATCH para lidar com erros do banco
        } catch (SQLException e) { // (Descomente quando adicionar a chamada do DAO)
             resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
             resp.getWriter().print("{\"erro\": \"Erro de Banco de Dados: " + e.getMessage() + "\"}");
             e.printStackTrace();
        } catch (Exception e) {
            // (Pega erros de JSON mal formatado, como o formato da data)
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
            resp.getWriter().print("{\"erro\": \"JSON inválido. " + e.getMessage() + "\"}");
            e.printStackTrace(); // Isso mostrará o erro de formato de data
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                java.util.List<model.entities.Sale> sales = saleDAO.getAll();
                resp.getWriter().print(mapper.writeValueAsString(sales));
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                model.entities.Sale sale = saleDAO.getById(id);
                if (sale == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().print("{\"erro\": \"Venda não encontrada\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().print(mapper.writeValueAsString(sale));
                }
            }
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
            model.entities.Sale sale = mapper.readValue(jsonBody, model.entities.Sale.class);
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