package auth;

import java.io.IOException;
import java.util.*;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet. http.*;

/**
 * Filtro de segurança que intercepta todas as requisições. 
 */
@WebFilter("/*")
public class AuthFilter implements Filter {
    
    // Recursos públicos que não requerem autenticação
    private static final Set<String> PUBLIC_RESOURCES = new HashSet<>(Arrays.asList(
        "/login",
        "/auth/login. jsp",
        "/css",
        "/js",
        "/images"
    ));
    
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        
        String path = request.getServletPath();
        String contextPath = request.getContextPath();
        
        // Debug - remover depois
        System.out.println("AuthFilter - Path: " + path);
        
        // Permite acesso a recursos públicos
        if (isPublicResource(path)) {
            chain.doFilter(request, response);
            return;
        }
        
        // Verifica se o utilizador está autenticado
        HttpSession session = request.getSession(false);
        Utilizador user = (session != null) ? 
            (Utilizador) session.getAttribute("utilizador") : null;
        
        if (user == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }
        
        // Debug - remover depois
        System. out.println("AuthFilter - User: " + user.getUsername() + ", Role: " + user.getRole());
        
        // Verifica permissões de acesso baseadas no role
        if (! hasPermission(user, path, request)) {
            System.out.println("AuthFilter - BLOQUEADO:  " + path);
            response.sendError(HttpServletResponse.SC_FORBIDDEN, 
                "Não tem permissão para aceder a esta página.");
            return;
        }
        
        // Adiciona utilizador como atributo do request para uso nas JSPs
        request.setAttribute("currentUser", user);
        
        chain.doFilter(request, response);
    }
    
    /**
     * Verifica se o recurso é público.
     */
    private boolean isPublicResource(String path) {
        if (path == null) {
            return false;
        }
        for (String publicPath : PUBLIC_RESOURCES) {
            if (path.equals(publicPath) || path.startsWith(publicPath + "/")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Verifica se o utilizador tem permissão para aceder ao recurso.
     */
    private boolean hasPermission(Utilizador user, String path, HttpServletRequest request) {
        
        // HOME - todos os utilizadores autenticados podem aceder
        if (path == null || path.isEmpty() || path.equals("/") || path.equals("/index.jsp")) {
            return true;
        }
        
        // LOGOUT - todos podem fazer logout
        if (path.equals("/logout")) {
            return true;
        }
        
        // Gerente tem acesso a tudo
        if (user.isGerente()) {
            return true;
        }
        
        // Veterinário - pode ver mas não editar clientes
        if (user.isVeterinario()) {
            // Não pode aceder à gestão de veterinários nem relatórios
            if (path. startsWith("/vets") || path.startsWith("/manager")) {
                return false;
            }
            return true;
        }
        
        // Cliente - acesso muito restrito
        if (user.isCliente()) {
            String nifCliente = user.getNifCliente();
            
            // Se não tem NIF associado, bloqueia tudo exceto home
            if (nifCliente == null || nifCliente.isEmpty()) {
                return false;
            }
            
            // Pode ver APENAS os seus animais
            if (path.startsWith("/animais")) {
                String paramNif = request.getParameter("clienteNif");
                String p = request.getParameter("p");
                
                // Bloqueia criar/editar animais
                if ("edit".equals(p)) {
                    return false;
                }
                
                // Permite ver genealogia e histórico
                if ("genealogia".equals(p)) {
                    return true;
                }
                
                // Só pode ver lista filtrada pelo seu NIF
                if (paramNif != null && paramNif. equals(nifCliente)) {
                    return true;
                }
                
                return false;
            }
            
            // Pode ver APENAS os seus agendamentos
            if (path. startsWith("/agendamentos")) {
                String paramNif = request.getParameter("clienteNif");
                if (paramNif != null && paramNif.equals(nifCliente)) {
                    return true;
                }
                return false;
            }
            
            // Pode ver APENAS os seus próprios dados de cliente
            if (path.startsWith("/clientes")) {
                String nif = request.getParameter("nif");
                String p = request.getParameter("p");
                
                // Só pode ver (não editar) os seus próprios dados
                if ("edit".equals(p) && nif != null && nif.equals(nifCliente)) {
                    return true;
                }
                return false;
            }
            
            // Pode ver histórico (será filtrado depois no servlet)
            if (path.startsWith("/historico")) {
                return true;
            }
            
            // Bloqueia tudo o resto
            return false;
        }
        
        return false;
    }
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {}
    
    @Override
    public void destroy() {}
}