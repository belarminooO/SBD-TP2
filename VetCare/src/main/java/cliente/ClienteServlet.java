package cliente;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("p");
        if (action == null) action = "list";
        
        switch (action) {
            case "list":
                listClientes(request, response);
                break;
            case "edit":
                showEditForm(request, response);
                break;
            default:
                listClientes(request, response);
                break;
        }
    }

    private void listClientes(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        List<Cliente> list = ClienteDAO.getAll();
        request.setAttribute("listaClientes", list);
        request.getRequestDispatcher("cliente/lista.jsp").forward(request, response);
    }
    
    private void showEditForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String nif = request.getParameter("nif");
        if (nif != null) {
            Cliente c = ClienteDAO.getByNif(nif);
            request.setAttribute("cliente", c);
        }
        request.getRequestDispatcher("cliente/edita.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tipo = request.getParameter("TipoCliente");
        Cliente c = null;
        if ("Pessoa".equals(tipo)) {
            c = new ClientePessoa(request);
        } else {
            c = new ClienteEmpresa(request);
        }
        
        ClienteDAO.save(c); // Should handle update too if modified DAO to support update/merge
        response.sendRedirect("clientes");
    }
}
