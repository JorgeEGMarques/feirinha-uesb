package controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dao.ProductDAO;
import model.entities.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal; 
import java.sql.SQLException;
import java.util.List;

// ATENÇÃO: A anotação DEVE ser a do seu outro Servlet (ProductServlet)
// Eu mudei para bater com o padrão de rota dos outros.
@WebServlet(urlPatterns = {"/api/products", "/api/products/*"})
public class ProductServlet extends HttpServlet {

    private ObjectMapper mapper;
    private ProductDAO productDAO;

    @Override
    public void init() throws ServletException {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule()); 
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.productDAO = new ProductDAO(); // DAO é inicializado
    }

    // --- CREATE (Criar) ---
    // POST /api/products
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);

        try {
            Product newProduct = mapper.readValue(jsonBody, Product.class);
            
            // Validação (Barreira 1) - O Servlet protege o DAO
            if (newProduct.getName() == null || newProduct.getPrice() == null) {
                 resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                 resp.getWriter().print("{\"erro\": \"Nome e preço são obrigatórios.\"}");
                 return;
            }
            if (newProduct.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400
                resp.getWriter().print("{\"erro\": \"O preço deve ser maior que zero.\"}");
                return;
            }

            // --- LÓGICA DO BANCO (ATIVA) ---
            productDAO.create(newProduct); 

            // --- FIM DA LÓGICA DO BANCO ---
            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED); // 201
            
            String jsonResposta = mapper.writeValueAsString(newProduct);
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
    // GET /api/products  (Listar todos)
    // GET /api/products/1 (Buscar por ID)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String pathInfo = req.getPathInfo(); // Pega o que vem depois de "/api/products"
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // --- Rota 1: Listar Todos (/api/products) ---
                List<Product> products = productDAO.getAll();
                resp.getWriter().print(mapper.writeValueAsString(products));
            
            } else {
                // --- Rota 2: Buscar Um por ID (/api/products/1) ---
                
                // Extrai o "1" da URL "/1"
                int id = Integer.parseInt(pathInfo.substring(1));
                Product product = productDAO.getById(id);
                
                if (product == null) {
                    // Não encontrou
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND); // 404
                    resp.getWriter().print("{\"erro\": \"Produto não encontrado\"}");
                } else {
                    // Encontrou, retorna o produto
                    resp.setStatus(HttpServletResponse.SC_OK); // 200
                    resp.getWriter().print(mapper.writeValueAsString(product));
                }
            }
        } catch (NumberFormatException e) {
            // Se a URL for /api/products/abc (inválido)
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"ID de produto inválido.\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"erro\": \"Erro de Banco de Dados: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    // --- UPDATE (Atualizar) ---
    // PUT /api/products/1
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        // 1. PUT precisa de um ID na URL
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"É preciso informar o ID do produto na URL para atualizar.\"}");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);
            
            Product product = mapper.readValue(jsonBody, Product.class);
            
            // 2. Garante que o ID do objeto é o mesmo da URL
            product.setCode(id); 
            
            // 3. (Validação, igual ao POST - opcional mas recomendado)
            
            // 4. Chama o DAO
            productDAO.update(product);
            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK); // 200
            resp.getWriter().print(mapper.writeValueAsString(product)); // Retorna o objeto atualizado
            
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
    // DELETE /api/products/1
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"É preciso informar o ID do produto na URL para deletar.\"}");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            
            // Chama o DAO
            productDAO.delete(id);
            
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