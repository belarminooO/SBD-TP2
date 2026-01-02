package historico;

import java.io.IOException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import animal.Animal;
import animal.AnimalDAO;
import agendamento.Agendamento;
import agendamento.AgendamentoDAO;

/**
 * Controlador responsável pela gestão do histórico clínico dos animais.
 * Gere os fluxos de visualização da linha do tempo e a inserção de novos
 * registos clínicos através de uma arquitetura centralizada.
 */
@WebServlet("/historico")
public class HistoricoServlet extends HttpServlet {
    /**
     * Identificador de versão para a serialização da classe.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Processa os pedidos GET encaminhando para a visualização ou formulários.
     * 
     * @param request  Objeto que contém os dados do pedido.
     * @param response Objeto para a resposta ao cliente.
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("p");
        if (action == null)
            action = "list";

        if ("list".equals(action)) {
            listHistorico(request, response);
        } else if ("new".equals(action)) {
            showNewForm(request, response);
        }
    }

    /**
     * Prepara e exibe a listagem do histórico clínico de um animal.
     * Obtém os dados do animal e os registos históricos correspondentes
     * para apresentação no interface.
     * 
     * @param request  Pedido HTTP.
     * @param response Resposta HTTP.
     */
    private void listHistorico(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String animalIdStr = request.getParameter("idAnimal");
        if (animalIdStr == null) {
            response.sendRedirect("animais");
            return;
        }
        int idAnimal = Integer.parseInt(animalIdStr);

        Animal animal = AnimalDAO.getById(idAnimal);
        List<java.util.Map<String, Object>> historia = HistoricoDAO.getHistoryByAnimal(idAnimal);

        request.setAttribute("animal", animal);
        request.setAttribute("historia", historia);
        request.setAttribute("agendamentosFuturos", AgendamentoDAO.getFutureAppointments(idAnimal));

        request.getRequestDispatcher("historico/lista.jsp").forward(request, response);
    }

    /**
     * Apresenta o formulário para registo de novos eventos clínicos.
     * 
     * @param request  Pedido HTTP.
     * @param response Resposta HTTP.
     */
    private void showNewForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("listaAnimais", AnimalDAO.getAll());
        String idAnimal = request.getParameter("idAnimal");
        if (idAnimal != null) {
            request.setAttribute("selectedAnimalId", Integer.parseInt(idAnimal));
        }
        request.getRequestDispatcher("historico/novo.jsp").forward(request, response);
    }

    /**
     * Processa a submissão de novos dados clínicos.
     * Identifica a especialização do serviço através do discriminador e
     * instancia o objeto correspondente para persistência.
     * 
     * @param request  Pedido HTTP com os dados do formulário.
     * @param response Resposta HTTP.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String tipo = request.getParameter("TipoDiscriminador");
        PrestacaoServico ps = null;
        int servicoId = 1;

        if ("Consulta".equals(tipo)) {
            Consulta c = new Consulta();
            c.setMotivo(request.getParameter("Motivo"));
            c.setSintomas(request.getParameter("Sintomas"));
            c.setDiagnostico(request.getParameter("Diagnostico"));
            c.setMedicacaoPrescrita(request.getParameter("MedicacaoPrescrita"));
            ps = c;
            servicoId = 1;
        } else if ("ExameFisico".equals(tipo)) {
            ExameFisico ef = new ExameFisico();
            ef.setTemperatura(new java.math.BigDecimal(request.getParameter("Temperatura")));
            ef.setPeso(new java.math.BigDecimal(request.getParameter("Peso")));
            ef.setFrequenciaCardiaca(Integer.parseInt(request.getParameter("FrequenciaCardiaca")));
            ef.setFrequenciaRespiratoria(Integer.parseInt(request.getParameter("FrequenciaRespiratoria")));
            ps = ef;
            servicoId = 4;
        } else if ("ResultadoExame".equals(tipo)) {
            ResultadoExame re = new ResultadoExame();
            re.setTipoExame(request.getParameter("TipoExame"));
            re.setResultadoDetalhado(request.getParameter("ResultadoDetalhado"));
            ps = re;
            servicoId = 5;
        } else if ("Desparasitacao".equals(tipo)) {
            Desparasitacao d = new Desparasitacao();
            d.setTipo(request.getParameter("Tipo"));
            d.setProdutosUtilizados(request.getParameter("ProdutosUtilizados"));
            ps = d;
            servicoId = 6;
        } else if ("Cirurgia".equals(tipo)) {
            Cirurgia c = new Cirurgia();
            c.setTipoCirurgia(request.getParameter("TipoCirurgia"));
            c.setNotasPosOperatorias(request.getParameter("NotasPosOperatorias"));
            ps = c;
            servicoId = 3;
        } else if ("TratamentoTerapeutico".equals(tipo)) {
            TratamentoTerapeutico tt = new TratamentoTerapeutico();
            tt.setDescricao(request.getParameter("Descricao"));
            ps = tt;
            servicoId = 7;
        } else {
            Vacinacao v = new Vacinacao();
            v.setTipoVacina(request.getParameter("TipoVacina"));
            v.setFabricante(request.getParameter("Fabricante"));
            ps = v;
            servicoId = 2;
        }

        ps.setDetalhesGerais(request.getParameter("DetalhesGerais"));
        int animalId = Integer.parseInt(request.getParameter("Animal_IDAnimal"));
        ps.setAnimalId(animalId);
        ps.setTipoServicoId(servicoId);
        ps.setDataHora(new Timestamp(System.currentTimeMillis()));

        HistoricoDAO.save(ps);

        response.sendRedirect("historico?idAnimal=" + animalId);
    }
}
