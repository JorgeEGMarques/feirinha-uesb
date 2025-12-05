package controller;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dao.TentDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entities.Tent;

import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet responsável por gerenciar as requisições HTTP relacionadas a Barracas.
 * Mapeado para /api/tents.
 */
@WebServlet("/api/tents/*")
public class TentServlet extends HttpServlet {

    private ObjectMapper mapper;
    private TentDAO tentDAO;

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
        
        this.tentDAO = new TentDAO();
    }

    /**
     * Processa requisições HTTP POST para criar uma nova barraca.
     * 
     * @param req A requisição HTTP contendo o JSON da barraca.
     * @param resp A resposta HTTP.
     * @throws ServletException Se ocorrer um erro no servlet.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);

        try {
            Tent newTent = mapper.readValue(jsonBody, Tent.class);

            tentDAO.create(newTent); 
            
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            
            String jsonResposta = mapper.writeValueAsString(newTent);
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

    /**
     * Processa requisições HTTP GET para listar barracas ou buscar por ID.
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
                java.util.List<model.entities.Tent> tents = tentDAO.getAll();
                resp.getWriter().print(mapper.writeValueAsString(tents));
            } else {
                int id = Integer.parseInt(pathInfo.substring(1));
                model.entities.Tent tent = tentDAO.getById(id);
                if (tent == null) {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().print("{\"erro\": \"Barraca não encontrada\"}");
                } else {
                    resp.setStatus(HttpServletResponse.SC_OK);
                    resp.getWriter().print(mapper.writeValueAsString(tent));
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

    /**
     * Processa requisições HTTP PUT para atualizar uma barraca existente.
     * 
     * @param req A requisição HTTP contendo o ID na URL e o JSON da barraca.
     * @param resp A resposta HTTP.
     * @throws ServletException Se ocorrer um erro no servlet.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().print("{\"erro\": \"É preciso informar o ID da barraca na URL para atualizar.\"}");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            String jsonBody = req.getReader().lines().reduce("", (a, b) -> a + b);
            model.entities.Tent tent = mapper.readValue(jsonBody, model.entities.Tent.class);
            tent.setCode(id);
            tentDAO.update(tent);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().print(mapper.writeValueAsString(tent));
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

    /**
     * Processa requisições HTTP DELETE para remover uma barraca.
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
            resp.getWriter().print("{\"erro\": \"É preciso informar o ID da barraca na URL para deletar.\"}");
            return;
        }

        try {
            int id = Integer.parseInt(pathInfo.substring(1));
            tentDAO.delete(id);
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