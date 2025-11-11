
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

// A anotação @WebServlet dispensa a configuração no web.xml
@WebServlet("/api/hello")
public class HelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // Configura a resposta para ser um JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        // Escreve o JSON na resposta
        resp.getWriter().print("{\"mensagem\": \"Ola mundo do Servlet!\"}");
    }
}