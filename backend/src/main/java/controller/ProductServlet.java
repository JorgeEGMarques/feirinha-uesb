package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dao.ProductDAO;
import model.entities.Product;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/api/products/*")
public class ProductServlet extends HttpServlet {

    private final ObjectMapper mapper = new ObjectMapper();
    private final ProductDAO productDAO = new ProductDAO();

    // ----------------- CREATE -----------------
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            Product newProduct = mapper.readValue(req.getReader(), Product.class);
            productDAO.create(newProduct);

            resp.setContentType("application/json");
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(mapper.writeValueAsString(newProduct));
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // ----------------- READ -----------------
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String pathInfo = req.getPathInfo(); // pode ser null ou "/id"
        resp.setContentType("application/json");

        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // lista todos
                List<Product> products = productDAO.getAll();
                resp.getWriter().write(mapper.writeValueAsString(products));
            } else {
                // busca por ID
                long id = Long.parseLong(pathInfo.substring(1));
                Product product = productDAO.getById(id);
                if (product != null) {
                    resp.getWriter().write(mapper.writeValueAsString(product));
                } else {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Produto não encontrado");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // ----------------- UPDATE -----------------
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do produto é obrigatório");
                return;
            }

            long id = Long.parseLong(pathInfo.substring(1));
            Product product = mapper.readValue(req.getReader(), Product.class);
            product.setCode(id);

            productDAO.update(product);

            resp.setContentType("application/json");
            resp.getWriter().write(mapper.writeValueAsString(product));
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // ----------------- DELETE -----------------
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID do produto é obrigatório");
                return;
            }

            long id = Long.parseLong(pathInfo.substring(1));
            productDAO.delete(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT); // 204
        } catch (SQLException e) {
            e.printStackTrace();
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
