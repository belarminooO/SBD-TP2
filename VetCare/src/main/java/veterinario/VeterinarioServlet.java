package veterinario;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/vets")
public class VeterinarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("p");
        if (action == null) action = "list";

        if ("edit".equals(action)) {
            String licenca = request.getParameter("lic");
            if (licenca != null) {
                request.setAttribute("vet", VeterinarioDAO.getByLicenca(licenca));
            }
            request.getRequestDispatcher("vets/edita.jsp").forward(request, response);
        } else {
            request.setAttribute("listaVets", VeterinarioDAO.getAll());
            request.getRequestDispatcher("vets/lista.jsp").forward(request, response);
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Veterinario v = new Veterinario(request);
        if (VeterinarioDAO.getByLicenca(v.getNLicenca()) != null) {
            VeterinarioDAO.update(v);
        } else {
            VeterinarioDAO.save(v);
        }
        response.sendRedirect("vets");
    }
}
