package animal;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import cliente.Cliente;
import cliente.ClienteDAO;

/**
 * Servlet responsável pela gestão de animais no sistema VetCare.
 * Processa pedidos HTTP para listagem, criação, edição e visualização da
 * genealogia de animais.
 */
@WebServlet("/animais")
public class AnimalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Processa os pedidos GET.
     * Controla a navegação para as páginas de lista, edição e genealogia,
     * e gere a pesquisa de animais.
     * 
     * @param request  o objeto HttpServletRequest com os dados do pedido
     * @param response o objeto HttpServletResponse para enviar a resposta
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("p");
        if (action == null)
            action = "list";

        if ("edit".equals(action)) {
            // Mostra o formulário de edição/criação
            showEditForm(request, response);
        } else if ("genealogia".equals(action)) {
            // Exibe a página de genealogia para um animal específico
            String id = request.getParameter("id");
            if (id != null) {
                request.setAttribute("animal", AnimalDAO.getById(Integer.parseInt(id)));
            }
            request.getRequestDispatcher("animal/genealogia.jsp").forward(request, response);
        } else {
            // Listagem de animais (com suporte a pesquisa)
            String search = request.getParameter("search");
            List<Animal> list;
            if (search != null && !search.isEmpty()) {
                list = AnimalDAO.searchByTutor(search);
            } else {
                list = AnimalDAO.getAll();
            }
            request.setAttribute("listaAnimais", list);
            request.getRequestDispatcher("animal/lista.jsp").forward(request, response);
        }
    }

    /**
     * Prepara e encaminha para a página de edição de animal.
     * Carrega os dados do animal (se edição) e as listas de clientes e espécies
     * para os dropdowns.
     * 
     * @param request  o pedido HTTP
     * @param response a resposta HTTP
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id != null) {
            request.setAttribute("animal", AnimalDAO.getById(Integer.parseInt(id)));
        }
        // Carregar listas auxiliares para o formulário
        request.setAttribute("listaClientes", ClienteDAO.getAll());
        request.setAttribute("listaEspecies", AnimalDAO.getEspecies());
        request.getRequestDispatcher("animal/edita.jsp").forward(request, response);
    }

    /**
     * Processa os pedidos POST.
     * Responsável por guardar (inserir ou atualizar) os dados de um animal.
     * 
     * @param request  o pedido HTTP contendo os dados do formulário
     * @param response a resposta HTTP
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Animal a = new Animal(request);
        AnimalDAO.save(a);
        response.sendRedirect("animais");
    }
}
