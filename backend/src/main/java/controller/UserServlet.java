package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entities.User;

import dao.UserDAO; // 1. IMPORTE O SEU NOVO DAO

import java.io.IOException;
import java.sql.SQLException; // 2. IMPORTE A EXCEÇÃO DO SQL

@WebServlet(urlPatterns = {"/api/usuarios", "/api/usuarios/*"}) 
public class UserServlet extends HttpServlet {

    private ObjectMapper mapper;
    private UserDAO userDAO; // 3. CRIE UMA INSTÂNCIA DO DAO

    @Override
    public void init() throws ServletException {
        this.mapper = new ObjectMapper();
        this.mapper.configure(com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // Inicialize o DAO uma vez quando o servlet for criado
        this.userDAO = new UserDAO(); 
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);

        try {
            User newUser = mapper.readValue(jsonBody, User.class);

            // --- AQUI É A MUDANÇA ---
            
            // LÓGICA ANTIGA (FICTÍCIA):
            // System.out.println("Recebido no backend: " + newUser.getNome());
            
            // LÓGICA NOVA (REAL):
            userDAO.create(newUser); // 4. CHAME O DAO PARA SALVAR!
            
            // --- FIM DA MUDANÇA ---

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            
            String jsonResposta = mapper.writeValueAsString(newUser);
            resp.getWriter().print(jsonResposta);

        } catch (SQLException e) {
            // 5. CAPTURE O ERRO DO BANCO DE DADOS
            // (Ex: se o usuário tentar inserir um CPF duplicado)
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Erro 500
            resp.getWriter().print("{\"erro\": \"Erro ao salvar no banco de dados: " + e.getMessage() + "\"}");
            e.printStackTrace(); // Mostra o erro no log do Tomcat
        
        } catch (Exception e) {
            // (Isso pega erros de JSON mal formatado)
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); // Erro 400
            resp.getWriter().print("{\"erro\": \"JSON inválido: " + e.getMessage() + "\"}");
        }
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // List all users
                java.util.List<model.entities.User> users = userDAO.getAll();
                resp.getWriter().print(mapper.writeValueAsString(users));
            } else {
                // GET /api/usuarios/{cpf}
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
            user.setCpf(cpf); // ensure path wins
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