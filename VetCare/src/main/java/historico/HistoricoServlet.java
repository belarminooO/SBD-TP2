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

@WebServlet("/historico")
public class HistoricoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String action = request.getParameter("p");
        if (action == null) action = "list";

        if ("list".equals(action)) {
            listHistorico(request, response);
        } else if ("new".equals(action)) {
            showNewForm(request, response);
        }
    }

    private void listHistorico(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        request.getRequestDispatcher("historico/lista.jsp").forward(request, response);
    }
    
    private void showNewForm(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setAttribute("listaAnimais", AnimalDAO.getAll());
        // Types would be Consulta or Vacinacao
        request.getRequestDispatcher("historico/novo.jsp").forward(request, response);
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String tipo = request.getParameter("TipoDiscriminador");
        
        PrestacaoServico ps;
        if ("Consulta".equals(tipo)) {
            Consulta c = new Consulta();
            c.setMotivo(request.getParameter("Motivo"));
            c.setSintomas(request.getParameter("Sintomas"));
            c.setDiagnostico(request.getParameter("Diagnostico"));
            c.setMedicacaoPrescrita(request.getParameter("MedicacaoPrescrita"));
            ps = c;
        } else if ("ExameFisico".equals(tipo)) {
            ExameFisico ef = new ExameFisico();
            ef.setTemperatura(new java.math.BigDecimal(request.getParameter("Temperatura")));
            ef.setPeso(new java.math.BigDecimal(request.getParameter("Peso")));
            ef.setFrequenciaCardiaca(Integer.parseInt(request.getParameter("FrequenciaCardiaca")));
            ef.setFrequenciaRespiratoria(Integer.parseInt(request.getParameter("FrequenciaRespiratoria")));
            ps = ef;
        } else if ("ResultadoExame".equals(tipo)) {
            ResultadoExame re = new ResultadoExame();
            re.setTipoExame(request.getParameter("TipoExame"));
            re.setResultadoDetalhado(request.getParameter("ResultadoDetalhado"));
            ps = re;
        } else if ("Desparasitacao".equals(tipo)) {
            Desparasitacao d = new Desparasitacao();
            d.setTipo(request.getParameter("Tipo"));
            d.setProdutosUtilizados(request.getParameter("ProdutosUtilizados"));
            ps = d;
        } else if ("Cirurgia".equals(tipo)) {
            Cirurgia c = new Cirurgia();
            c.setTipoCirurgia(request.getParameter("TipoCirurgia"));
            c.setNotasPosOperatorias(request.getParameter("NotasPosOperatorias"));
            ps = c;
        } else if ("TratamentoTerapeutico".equals(tipo)) {
            TratamentoTerapeutico tt = new TratamentoTerapeutico();
            tt.setDescricao(request.getParameter("Descricao"));
            ps = tt;
        } else {
            Vacinacao v = new Vacinacao();
            v.setTipoVacina(request.getParameter("TipoVacina"));
            v.setFabricante(request.getParameter("Fabricante"));
            ps = v;
        }
        
        ps.setDetalhesGerais(request.getParameter("DetalhesGerais"));
        ps.setAnimalId(Integer.parseInt(request.getParameter("Animal_IDAnimal")));
        ps.setTipoServicoId(1); // Placeholder or from form
        ps.setDataHora(new Timestamp(System.currentTimeMillis()));
        
        HistoricoDAO.save(ps);
        response.sendRedirect("animais"); // Return to animal list
    }
}
