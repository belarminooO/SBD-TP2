package veterinario;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Controlador responsável pela gestão do corpo clínico veterinário.
 * Gere os pedidos HTTP para listagem, edição e registo de profissionais
 * médicos na plataforma.
 */
@WebServlet("/vets")
public class VeterinarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Processa pedidos GET para visualização da lista ou formulário de edição de
     * veterinários.
     * 
     * @param request  Pedido HTTP.
     * @param response Resposta HTTP.
     * @throws ServletException Em caso de erro no processamento do servlet.
     * @throws IOException      Em caso de erro de entrada/saída.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("p");
        if (action == null) {
            action = "list";
        }

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

    /**
     * Processa pedidos POST para gravação ou atualização de perfis de veterinários.
     * Realiza a distinção entre inserção de novos registos e atualização de
     * existentes
     * com base no número da licença profissional.
     * 
     * @param request  Pedido HTTP contendo os dados do veterinário.
     * @param response Resposta HTTP.
     * @throws ServletException Em caso de erro no processamento.
     * @throws IOException      Em caso de erro de entrada/saída.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Veterinario v = new Veterinario(request);
        if (VeterinarioDAO.getByLicenca(v.getNLicenca()) != null) {
            VeterinarioDAO.update(v);
        } else {
            VeterinarioDAO.save(v);
        }
        response.sendRedirect("vets");
    }
}
