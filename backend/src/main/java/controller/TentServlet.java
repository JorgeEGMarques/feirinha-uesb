package controller;

// Imports do Jackson (para JSON)
import com.fasterxml.jackson.databind.ObjectMapper;

// Imports do Servlet (JAKARTA)
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.entities.Tent;

import java.io.IOException;

@WebServlet("/api/tents/*")
public class TentServlet extends HttpServlet {

    private ObjectMapper mapper = new ObjectMapper(); // Objeto que converte JSON

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        // 1. LER O JSON QUE O CLIENTE ENVIOU (O "BODY" DA REQUISIÇÃO)
        // Usamos um StringBuilder para ler o corpo da requisição
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) {
            sb.append(line);
        }
        String jsonBody = sb.toString();

        // 2. CONVERTER O JSON PARA UM OBJETO JAVA
        Tent novoTent = mapper.readValue(jsonBody, Tent.class);

        // --- AQUI ENTRA A LÓGICA DO JDBC ---
        // Por enquanto, vamos só simular que salvamos no banco e demos um ID a ele
        System.out.println("Recebido no backend: " + novoTent.getCode());
        //novoTent.setId(1); // Simula que o banco deu o ID 1
        // --- FIM DA LÓGICA DO BANCO ---

        // 3. RESPONDER AO CLIENTE COM UM JSON
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_CREATED); // Seta o Status HTTP 201 (Created)

        // Converte o objeto Java (com ID) de volta para uma String JSON
        String jsonResposta = mapper.writeValueAsString(novoTent);
        
        resp.getWriter().print(jsonResposta);
    }
}