package controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import dao.ComentarioDAO;
import model.entities.Comentario;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet responsável por gerenciar as requisições HTTP relacionadas a Comentários.
 * Mapeado para /api/comentarios.
 */
@WebServlet(urlPatterns = {"/api/comentarios", "/api/comentarios/*"})
public class ComentarioServlet extends HttpServlet {

    private ObjectMapper mapper;
    private ComentarioDAO comentarioDAO;

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
        this.comentarioDAO = new ComentarioDAO();
    }

    /**
     * Processa requisições HTTP POST para criar um novo comentário.
     * 
     * @param req A requisição HTTP contendo o JSON do comentário.
     * @param resp A resposta HTTP.
     * @throws ServletException Se ocorrer um erro no servlet.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String body = req.getReader().lines().reduce("", (a,b)->a+b);
        try {
            Comentario c = mapper.readValue(body, Comentario.class);
            comentarioDAO.create(c);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().print(mapper.writeValueAsString(c));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"erro\": \"Erro de banco: " + e.getMessage() + "\"}");
            e.printStackTrace();
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"JSON inválido: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    /**
     * Processa requisições HTTP GET para listar comentários ou buscar por ID.
     * Suporta:
     * - /api/comentarios: Lista todos os comentários.
     * - /api/comentarios/produto/{id}: Lista comentários de um produto específico.
     * - /api/comentarios/{id}: Busca um comentário pelo ID.
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
                List<Comentario> lista = comentarioDAO.getAll();
                resp.getWriter().print(mapper.writeValueAsString(lista));
                return;
            }

            if (pathInfo.startsWith("/produto/")) {
                String idStr = pathInfo.substring("/produto/".length());
                int prodId = Integer.parseInt(idStr);
                List<Comentario> lista = comentarioDAO.getByProduct(prodId);
                resp.getWriter().print(mapper.writeValueAsString(lista));
                return;
            }

            String idStr = pathInfo.substring(1);
            int id = Integer.parseInt(idStr);
            Comentario c = comentarioDAO.getById(id);
            if (c == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().print("{\"erro\": \"Comentário não encontrado\"}");
            } else {
                resp.getWriter().print(mapper.writeValueAsString(c));
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"ID inválido\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"erro\": \"Erro de banco: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }

    /**
     * Processa requisições HTTP DELETE para remover um comentário.
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
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"É preciso informar o ID do comentário na URL para deletar.\"}");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            comentarioDAO.delete(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"ID inválido\"}");
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().print("{\"erro\": \"Erro de banco: " + e.getMessage() + "\"}");
            e.printStackTrace();
        }
    }
}