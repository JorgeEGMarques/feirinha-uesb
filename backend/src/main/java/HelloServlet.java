
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Servlet simples para teste de conectividade da API.
 * Retorna uma mensagem JSON fixa.
 * Mapeado para /api/hello.
 */
@WebServlet("/api/hello")
public class HelloServlet extends HttpServlet {

    /**
     * Processa requisições HTTP GET.
     * Retorna um JSON com uma mensagem de boas-vindas.
     * 
     * @param req A requisição HTTP.
     * @param resp A resposta HTTP.
     * @throws ServletException Se ocorrer um erro no servlet.
     * @throws IOException Se ocorrer um erro de I/O.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // Configura a resposta para ser um JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        // Escreve o JSON na resposta
        resp.getWriter().print("{\"mensagem\": \"Ola mundo do Servlet!\"}");
    }
}