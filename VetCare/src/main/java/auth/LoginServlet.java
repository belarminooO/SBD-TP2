package auth;

import java.io. IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet. annotation.WebServlet;
import jakarta.servlet.http.*;

/**
 * Servlet responsável pela autenticação de utilizadores.  
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    /**
     * Exibe a página de login.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Se já estiver autenticado, redireciona para home
        HttpSession session = request.getSession(false);
        if (session != null && session.getAttribute("utilizador") != null) {
            response.sendRedirect(request.getContextPath() + "/");
            return;
        }
        
        request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
    }
    
    /**
     * Processa a tentativa de login.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // Validação básica
        if (username == null || username.trim().isEmpty() ||
            password == null || password. trim().isEmpty()) {
            request.setAttribute("error", "Por favor, preencha todos os campos.");
            request.getRequestDispatcher("/auth/login.jsp").forward(request, response);
            return;
        }
        
        // Tenta autenticar
        Utilizador user = AuthDAO.authenticate(username. trim(), password);
        
        if (user != null) {
            // Login bem sucedido
            HttpSession session = request.getSession(true);
            session.setAttribute("utilizador", user);
            session.setMaxInactiveInterval(30 * 60); // 30 minutos
            
            // TODOS os utilizadores vão para a home
            response.sendRedirect(request.getContextPath() + "/");
        } else {
            // Login falhou
            request.setAttribute("error", "Credenciais inválidas. Verifique o username e password.");
            request.getRequestDispatcher("/auth/login. jsp").forward(request, response);
        }
    }
}