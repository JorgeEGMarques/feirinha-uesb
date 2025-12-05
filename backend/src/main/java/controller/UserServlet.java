package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entities.User;

import dao.UserDAO; 

import java.io.IOException;
import java.sql.SQLException; 

/**
 * Servlet responsável por gerenciar as requisições HTTP relacionadas a Usuários.
 * Mapeado para /api/usuarios.
 */
@WebServlet(urlPatterns = {"/api/usuarios", "/api/usuarios/*"}) 
public class UserServlet extends HttpServlet {

    private ObjectMapper mapper;
    private UserDAO userDAO;

    /**
     * Inicializa o servlet, configurando o ObjectMapper e o DAO.
     * 
     * @throws ServletException Se ocorrer um erro na inicialização.
     */
    @Override
    public void init() throws ServletException {
        this.mapper = new ObjectMapper();
        this.mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.userDAO = new UserDAO(); 
    }

    /**
     * Processa requisições HTTP POST para criar um novo usuário ou realizar login.
     * Se o pathInfo for /login, realiza a autenticação.
     * Caso contrário, cria um novo usuário.
     * 
     * @param req A requisição HTTP contendo o JSON do usuário ou credenciais.
     * @param resp A resposta HTTP.
     * @throws ServletException Se ocorrer um erro no servlet.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String pathInfo = req.getPathInfo();
        String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);

        if ("/login".equals(pathInfo)) {
            try {
                com.fasterxml.jackson.databind.JsonNode node = mapper.readTree(jsonBody);
                String email = node.has("email") ? node.get("email").asText() : null;
                String senha = node.has("senha") ? node.get("senha").asText() : null;

                if (email == null || senha == null) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().print("{\"erro\": \"Informe email e senha.\"}");
                    return;
                }

                User user = userDAO.validateLogin(email, senha);
                
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");

                if (user != null) {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().print(mapper.writeValueAsString(user));
                } else {
                    resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    resp.getWriter().print("{\"erro\": \"Usuário ou senha incorretos.\"}");
                }
            } catch (SQLException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                resp.getWriter().print("{\"erro\": \"Erro de banco: " + e.getMessage() + "\"}");
            } catch (Exception e) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().print("{\"erro\": \"JSON inválido.\"}");
            }
            return;
        }

        try {
            User newUser = mapper.readValue(jsonBody, User.class);
            userDAO.create(newUser); 

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            
            String jsonResposta = mapper.writeValueAsString(newUser);
            resp.getWriter().print(jsonResposta);

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); 
            resp.getWriter().print("{\"erro\": \"Erro ao salvar no banco de dados: " + e.getMessage() + "\"}");
            e.printStackTrace(); 
        
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"JSON inválido: " + e.getMessage() + "\"}");
        }
    }
    
    /**
     * Processa requisições HTTP GET para listar usuários ou buscar por CPF.
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
                java.util.List<model.entities.User> users = userDAO.getAll();
                resp.getWriter().print(mapper.writeValueAsString(users));
            } else {
                String cpf = pathInfo.substring(1);
                model.entities.User user = userDAO.getByCpf(cpf);
                if (user == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().print("{\"erro\": \"Usuário não encontrado\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().print(mapper.writeValueAsString(user));
                }
            }
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"erro\": \"Erro de Banco de Dados: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    /**
     * Processa requisições HTTP PUT para atualizar um usuário existente.
     * 
     * @param req A requisição HTTP contendo o CPF na URL e o JSON do usuário.
     * @param resp A resposta HTTP.
     * @throws ServletException Se ocorrer um erro no servlet.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"É preciso informar o CPF do usuário na URL para atualizar.\"}");
            return;
        }

        try {
            String cpf = pathInfo.substring(1);
            String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);
            model.entities.User user = mapper.readValue(jsonBody, model.entities.User.class);
            user.setCpf(cpf);
            userDAO.update(user);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(mapper.writeValueAsString(user));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"erro\": \"Erro de Banco de Dados: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"JSON inválido: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    /**
     * Processa requisições HTTP DELETE para remover um usuário.
     * 
     * @param req A requisição HTTP contendo o CPF na URL.
     * @param resp A resposta HTTP.
     * @throws ServletException Se ocorrer um erro no servlet.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"É preciso informar o CPF do usuário na URL para deletar.\"}");
            return;
        }

        try {
            String cpf = pathInfo.substring(1);
            userDAO.delete(cpf);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"erro\": \"Erro de Banco de Dados: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}