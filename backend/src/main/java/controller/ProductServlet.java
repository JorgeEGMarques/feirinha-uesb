package controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dao.ProductDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entities.Product;

import java.io.IOException;
import java.math.BigDecimal; 
import java.sql.SQLException;
import java.util.List;

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
    
    // Método ajudante para enviar erros
    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print("{\"erro\": \"" + message + "\"}");
    }


    // --- CREATE (Criar) ---
    // POST /api/products
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);

        try {
            Product newProduct = mapper.readValue(jsonBody, Product.class);
            
            // --- VALIDAÇÃO (BARREIRA 1) ---
            
            // CORREÇÃO: Remova a validação do ID.
            // O banco de dados agora gera o ID, então o 'code' do JSON será 0 (padrão)
            // e isso é esperado.
            /* if (newProduct.getCode() == 0) {
                 sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "O 'code' (cod_produto) é obrigatório.");
                 return;
            }
            */

            if (newProduct.getName() == null || newProduct.getName().trim().isEmpty()) {
                 sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "O 'name' (nome_produto) é obrigatório.");
                 return;
            }
            if (newProduct.getPrice() == null) {
                 sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "O 'price' (preco_produto) é obrigatório.");
                 return;
            }
            if (newProduct.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
                sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "O 'price' (preco_produto) deve ser maior que zero.");
                return;
            }
            // --- FIM DA VALIDAÇÃO ---


            // --- LÓGICA DO BANCO (ATIVA) ---
            // O DAO agora vai preencher o ID do newProduct
            productDAO.create(newProduct); 

            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED); // 201
            
            // Envia o produto de volta, agora com o ID que o banco gerou
            String jsonResposta = mapper.writeValueAsString(newProduct);
            resp.getWriter().print(jsonResposta);

        } catch (SQLException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro de Banco de Dados: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "JSON inválido: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- READ (Ler) ---
    // GET /api/products  (Listar todos)
    // GET /api/products/1 (Buscar por ID)
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // --- Rota 1: Listar Todos (/api/products) ---
                List<Product> products = productDAO.getAll();
                resp.getWriter().print(mapper.writeValueAsString(products));
            
            } else {
                // --- Rota 2: Buscar Um por ID (/api/products/1) ---
                int id = Integer.parseInt(pathInfo.substring(1));
                Product product = productDAO.getById(id);
                
                if (product == null) {
                    sendError(resp, HttpServletResponse.SC_NOT_FOUND, "Produto não encontrado.");
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK); // 200
                    resp.getWriter().print(mapper.writeValueAsString(product));
                }
            }
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "ID de produto inválido.");
        } catch (SQLException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro de Banco de Dados: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- UPDATE (Atualizar) ---
    // PUT /api/products/1
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "É preciso informar o ID do produto na URL para atualizar.");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);
            
            Product product = mapper.readValue(jsonBody, Product.class);
            product.setCode(id); 
            
            // (Validações do POST também deveriam estar aqui)
            
            productDAO.update(product);
            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK); // 200
            resp.getWriter().print(mapper.writeValueAsString(product));
            
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "ID de produto inválido.");
        } catch (SQLException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro de Banco de Dados: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "JSON inválido: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // --- DELETE (Apagar) ---
    // DELETE /api/products/1
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        
        if (pathInfo == null || pathInfo.equals("/")) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "É preciso informar o ID do produto na URL para deletar.");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            productDAO.delete(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
            
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "ID de produto inválido.");
        } catch (SQLException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro de Banco de Dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}