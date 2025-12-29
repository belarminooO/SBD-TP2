package cliente;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Servlet responsável pela gestão do ciclo de vida dos clientes (tutores) no
 * sistema VetCare.
 * 
 * Implementa suporte polimórfico para distinguir entre clientes do tipo Pessoa
 * e Empresa,
 * permitindo a criação, edição e listagem de registos de clientes através de
 * uma interface web.
 * 
 * O servlet processa pedidos GET para visualização e listagem, e pedidos POST
 * para
 * criação e atualização de registos.
 */
@WebServlet("/clientes")
public class ClienteServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Processa pedidos HTTP GET para listagem e edição de clientes.
     * 
     * @param request  Objeto de pedido HTTP contendo parâmetros e atributos.
     * @param response Objeto de resposta HTTP para envio de dados ao cliente.
     * @throws ServletException Se ocorrer um erro específico do servlet.
     * @throws IOException      Se ocorrer um erro de entrada/saída.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("p");
        if (action == null)
            action = "list";

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

    /**
     * Lista todos os clientes registados no sistema.
     * 
     * @param request  Objeto de pedido HTTP.
     * @param response Objeto de resposta HTTP.
     * @throws ServletException Se ocorrer um erro específico do servlet.
     * @throws IOException      Se ocorrer um erro de entrada/saída.
     */
    private void listClientes(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        List<Cliente> list = ClienteDAO.getAll();
        request.setAttribute("listaClientes", list);
        request.getRequestDispatcher("cliente/lista.jsp").forward(request, response);
    }

    /**
     * Prepara o formulário de edição ou criação de cliente.
     * 
     * Se um NIF for fornecido, carrega os dados do cliente existente para edição.
     * Caso contrário, apresenta um formulário vazio para criação de novo cliente.
     * O DAO encarrega-se de devolver a instância correta (Pessoa ou Empresa).
     * 
     * @param request  Objeto de pedido HTTP.
     * @param response Objeto de resposta HTTP.
     * @throws ServletException Se ocorrer um erro específico do servlet.
     * @throws IOException      Se ocorrer um erro de entrada/saída.
     */
    private void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nif = request.getParameter("nif");
        if (nif != null) {
            Cliente c = ClienteDAO.getByNif(nif);
            request.setAttribute("cliente", c);
        }
        request.getRequestDispatcher("cliente/edita.jsp").forward(request, response);
    }

    /**
     * Processa pedidos HTTP POST para criação ou atualização de clientes.
     * 
     * Implementa decisão polimórfica baseada no tipo de cliente selecionado pelo
     * utilizador,
     * criando a instância apropriada (ClientePessoa ou ClienteEmpresa) e delegando
     * a persistência ao DAO.
     * 
     * @param request  Objeto de pedido HTTP contendo os dados do formulário.
     * @param response Objeto de resposta HTTP para redirecionamento.
     * @throws ServletException Se ocorrer um erro específico do servlet.
     * @throws IOException      Se ocorrer um erro de entrada/saída.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tipo = request.getParameter("TipoCliente");
        Cliente c = null;

        if ("Pessoa".equals(tipo)) {
            c = new ClientePessoa(request);
        } else {
            c = new ClienteEmpresa(request);
        }

        ClienteDAO.save(c);

        response.sendRedirect("clientes");
    }
}
