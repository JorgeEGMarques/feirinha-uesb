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

        String origin = httpRequest.getHeader("Origin");
        if (origin != null && !origin.isEmpty()) {
            httpResponse.setHeader("Access-Control-Allow-Origin", origin);
            httpResponse.setHeader("Vary", "Origin");
        } else {
            httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        }

        httpResponse.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        String reqHeaders = httpRequest.getHeader("Access-Control-Request-Headers");
        String defaultAllowed = "Content-Type, Authorization, X-Requested-With";
        String allowedHeaders = (reqHeaders != null && !reqHeaders.isEmpty()) ? reqHeaders + ", " + defaultAllowed : defaultAllowed;
        httpResponse.setHeader("Access-Control-Allow-Headers", allowedHeaders);

        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");

        httpResponse.setHeader("Access-Control-Expose-Headers", "Location,Content-Type");

        if ("OPTIONS".equalsIgnoreCase(httpRequest.getMethod())) {
            httpResponse.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        chain.doFilter(request, response);
    }
}