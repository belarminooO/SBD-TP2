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

@WebServlet("/animais")
public class AnimalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("p");
        if (action == null)
            action = "list";

        if ("edit".equals(action)) {
            showEditForm(request, response);
        } else if ("genealogia".equals(action)) {
            String id = request.getParameter("id");
            if (id != null) {
                request.setAttribute("animal", AnimalDAO.getById(Integer.parseInt(id)));
            }
            request.getRequestDispatcher("animal/genealogia.jsp").forward(request, response);
        } else {
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

    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String id = request.getParameter("id");
        if (id != null) {
            request.setAttribute("animal", AnimalDAO.getById(Integer.parseInt(id)));
        }
        request.setAttribute("listaClientes", ClienteDAO.getAll());
        request.setAttribute("listaEspecies", AnimalDAO.getEspecies());
        request.getRequestDispatcher("animal/edita.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Animal a = new Animal(request);
        AnimalDAO.save(a);
        response.sendRedirect("animais");
    }
}
