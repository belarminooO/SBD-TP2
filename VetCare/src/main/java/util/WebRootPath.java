package util;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet que devolve o caminho real (físico) da aplicação no servidor.
 * É utilizado pela classe util.Configura para localizar o ficheiro de propriedades.
 */
@WebServlet("/WebRootPath")
public class WebRootPath extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String realPath = getServletContext().getRealPath("/");
        response.setContentType("text/plain");
        response.getWriter().write(realPath);
    }
}
