package util;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet para obtenção do caminho absoluto da raiz da aplicação web.
 *
 * Este servlet tem uma única função crítica: responder com o caminho absoluto
 * do servidor onde a aplicação está instalada. É utilizado pela classe
 * Configura
 * para determinar a localização dos ficheiros de configuração e recursos.
 * 
 * O servlet responde a pedidos GET retornando o caminho real do contexto da
 * aplicação.
 */
@WebServlet("/WebRootPath")
public class WebRootPath extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Processa pedidos HTTP GET retornando o caminho absoluto da raiz da aplicação.
     * 
     * @param request  Objeto de pedido HTTP.
     * @param response Objeto de resposta HTTP.
     * @throws ServletException Se ocorrer um erro específico do servlet.
     * @throws IOException      Se ocorrer um erro de entrada/saída.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String realPath = getServletContext().getRealPath("/");
        response.setContentType("text/plain");
        response.getWriter().write(realPath);
    }
}