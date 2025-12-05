import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Filtro responsável por configurar o CORS (Cross-Origin Resource Sharing).
 * Permite que o frontend (React) acesse a API backend de uma origem diferente.
 * Aplicado a todas as requisições iniciadas por /api/*.
 */
@WebFilter("/api/*")
public class CorsFilter implements Filter {

    /**
     * Intercepta a requisição para adicionar os cabeçalhos de CORS.
     * 
     * @param request A requisição do servlet.
     * @param response A resposta do servlet.
     * @param chain A cadeia de filtros.
     * @throws IOException Se ocorrer um erro de I/O.
     * @throws ServletException Se ocorrer um erro no servlet.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpServletRequest httpRequest = (HttpServletRequest) request;

        // Permite que qualquer origem (ou seu localhost:3000) acesse
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        
        // Métodos que o React pode usar
        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        
        // Headers que o React pode enviar (incluindo o 'Authorization' para JWTs)
        httpResponse.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With");

        // Permite que o navegador pré-verifique a requisição (OPTIONS)
        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        // Continua o fluxo normal da requisição (ex: vai para o HelloServlet)
        chain.doFilter(request, response);
    }
}