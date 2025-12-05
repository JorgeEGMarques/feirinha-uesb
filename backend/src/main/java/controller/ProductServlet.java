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

/**
 * Servlet responsável por gerenciar as requisições HTTP relacionadas a Produtos.
 * Mapeado para /api/products.
 */
@WebServlet(urlPatterns = {"/api/products", "/api/products/*"})
public class ProductServlet extends HttpServlet {

    private ObjectMapper mapper;
    private ProductDAO productDAO;

    /**
     * Inicializa o servlet, configurando o ObjectMapper e o DAO.
     * 
     * @throws ServletException Se ocorrer um erro na inicialização.
     */
    @Override
    public void init() throws ServletException {
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule()); 
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.productDAO = new ProductDAO(); 
    }
    
    /**
     * Método auxiliar para enviar erros na resposta HTTP.
     * 
     * @param resp A resposta HTTP.
     * @param statusCode O código de status HTTP.
     * @param message A mensagem de erro.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    private void sendError(HttpServletResponse resp, int statusCode, String message) throws IOException {
        resp.setStatus(statusCode);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().print("{\"erro\": \"" + message + "\"}");
    }

    /**
     * Processa requisições HTTP POST para criar um novo produto.
     * 
     * @param req A requisição HTTP contendo o JSON do produto.
     * @param resp A resposta HTTP.
     * @throws ServletException Se ocorrer um erro no servlet.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);

        try {
            Product newProduct = mapper.readValue(jsonBody, Product.class);

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

            productDAO.create(newProduct); 

            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED); 

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

    /**
     * Processa requisições HTTP GET para listar produtos ou buscar por ID.
     * 
     * @param req A requisição HTTP.
     * @param resp A resposta HTTP.
     * @throws ServletException Se ocorrer um erro no servlet.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {

                List<Product> products = productDAO.getAll();
                resp.getWriter().print(mapper.writeValueAsString(products));
            
            } else {

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

    /**
     * Processa requisições HTTP PUT para atualizar um produto existente.
     * 
     * @param req A requisição HTTP contendo o ID na URL e o JSON do produto.
     * @param resp A resposta HTTP.
     * @throws ServletException Se ocorrer um erro no servlet.
     * @throws IOException Se ocorrer um erro de I/O.
     */
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

            productDAO.update(product);
            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK); 
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

    /**
     * Processa requisições HTTP DELETE para remover um produto.
     * 
     * @param req A requisição HTTP contendo o ID na URL.
     * @param resp A resposta HTTP.
     * @throws ServletException Se ocorrer um erro no servlet.
     * @throws IOException Se ocorrer um erro de I/O.
     */
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
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
            
        } catch (NumberFormatException e) {
            sendError(resp, HttpServletResponse.SC_BAD_REQUEST, "ID de produto inválido.");
        } catch (SQLException e) {
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Erro de Banco de Dados: " + e.getMessage());
            e.printStackTrace();
        }
    }
}