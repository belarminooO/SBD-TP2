package animal;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.Part;
import java.util.Scanner;
import cliente.ClienteDAO;
import util.Configura;

/**
 * Servlet responsável pela gestão de animais no sistema VetCare.
 * Processa pedidos HTTP para listagem, criação, edição e visualização da
 * genealogia de animais.
 */
@MultipartConfig(fileSizeThreshold = 1024 * 1024 * 2, maxFileSize = 1024 * 1024 * 10, maxRequestSize = 1024 * 1024 * 50)
@WebServlet("/animais")
public class AnimalServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Processa os pedidos HTTP GET.
     * Suporta operações de listagem, pesquisa, visualização de formulário de edição
     * e visualização de genealogia.
     * 
     * @param request  O objeto HttpServletRequest que contém o pedido do cliente.
     * @param response O objeto HttpServletResponse que contém a resposta do
     *                 servlet.
     * @throws ServletException Se ocorrer um erro específico do servlet.
     * @throws IOException      Se ocorrer um erro de I/O.
     */
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

    /**
     * Apresenta o formulário de edição preenchido com os dados de um animal
     * existente
     * ou um formulário vazio para criação de um novo registo.
     * 
     * @param request  O objeto HttpServletRequest.
     * @param response O objeto HttpServletResponse.
     * @throws ServletException Se ocorrer um erro específico do servlet.
     * @throws IOException      Se ocorrer um erro de I/O.
     */
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

    /**
     * Processa os pedidos HTTP POST para criação ou atualização de animais.
     * Trata o processamento de campos multipart (incluindo upload de fotos),
     * validação de dados obrigatórios e persistência na base de dados.
     * 
     * @param request  O objeto HttpServletRequest contendo os dados do formulário.
     * @param response O objeto HttpServletResponse.
     * @throws ServletException Se ocorrer um erro específico do servlet.
     * @throws IOException      Se ocorrer um erro de I/O.
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        System.out.println(">>> AnimalServlet: Processando POST");

        String nome = trimToNull(getFieldValue(request, "Nome"));
        String raca = trimToNull(getFieldValue(request, "Raca"));
        String sexo = trimToNull(getFieldValue(request, "Sexo"));
        String nif = trimToNull(getFieldValue(request, "Cliente_NIF"));
        String especie = trimToNull(getFieldValue(request, "Catalogo_NomeComum"));
        String dataNasc = trimToNull(getFieldValue(request, "DataNascimento"));
        String pesoStr = trimToNull(getFieldValue(request, "PesoAtual"));
        String idStr = trimToNull(getFieldValue(request, "IDAnimal"));

        String filiacao = trimToNull(getFieldValue(request, "Filiacao"));
        String estadoRep = trimToNull(getFieldValue(request, "EstadoReprodutivo"));
        String alergias = trimToNull(getFieldValue(request, "Alergias"));
        String cores = trimToNull(getFieldValue(request, "Cores"));
        String caract = trimToNull(getFieldValue(request, "CaracteristicasDistintivas"));
        String microchip = trimToNull(getFieldValue(request, "NumeroTransponder"));

        System.out.println("    [DEBUG] Dados Recebidos:");
        System.out.println("    - Nome: " + nome);
        System.out.println("    - EstadoReprodutivo: [" + (estadoRep == null ? "NULL" : estadoRep) + "] Len="
                + (estadoRep != null ? estadoRep.length() : 0));
        System.out.println("    - Cores: " + cores);

        Animal a = new Animal();
        if (idStr != null)
            a.setIdAnimal(Integer.parseInt(idStr));
        a.setNome(nome);
        a.setRaca(raca);
        a.setSexo(sexo);
        a.setClienteNif(nif);
        a.setCatalogoNomeComum(especie);
        a.setDataNascimento(util.DataFormatter.StringToSqlDate(dataNasc));

        a.setFiliacao(filiacao);
        a.setEstadoReprodutivo(estadoRep);
        a.setAlergias(alergias);
        a.setCores(cores);
        a.setCaracteristicasDistintivas(caract);
        a.setNumeroTransponder(microchip);

        if (pesoStr != null) {
            try {
                a.setPesoAtual(new java.math.BigDecimal(pesoStr.replace(",", ".")));
            } catch (Exception e) {
                System.err.println("    Erro peso: " + pesoStr);
            }
        }

        try {
            Part filePart = request.getPart("fotoFicheiro");
            if (filePart != null && filePart.getSize() > 0) {
                try (java.io.InputStream input = filePart.getInputStream()) {
                    byte[] bytes = input.readAllBytes();
                    a.setFotografia(bytes);
                    System.out.println("    [DEBUG] Foto lida para binário: " + bytes.length + " bytes");
                }
            } else {
                if (idStr != null) {
                    Animal ant = AnimalDAO.getById(Integer.parseInt(idStr));
                    if (ant != null)
                        a.setFotografia(ant.getFotografia());
                }
            }
        } catch (Exception e) {
            System.err.println("    Erro upload: " + e.getMessage());
        }

        StringBuilder erros = new StringBuilder();
        if (a.getNome() == null)
            erros.append("<li>O campo <b>Nome</b> é obrigatório.</li>");
        if (a.getClienteNif() == null)
            erros.append("<li>O campo <b>Tutor (Cliente)</b> é obrigatório.</li>");
        if (a.getCatalogoNomeComum() == null)
            erros.append("<li>O campo <b>Espécie</b> é obrigatório.</li>");
        if (a.getPesoAtual() == null)
            erros.append("<li>O campo <b>Peso Atual</b> é obrigatório.</li>");
        if (a.getDataNascimento() == null)
            erros.append("<li>O campo <b>Data de Nascimento</b> é obrigatório ou inválido.</li>");
        if (a.getEstadoReprodutivo() == null)
            erros.append("<li>O campo <b>Estado Reprodutivo</b> é obrigatório.</li>");

        if (erros.length() > 0) {
            request.getSession().setAttribute("msgErr", "Corrija os seguintes erros:<ul>" + erros.toString() + "</ul>");
            request.setAttribute("animal", a);
            showEditForm(request, response);
            return;
        }

        int result = AnimalDAO.save(a);
        if (result > 0) {
            request.getSession().setAttribute("msg", "Animal '" + a.getNome() + "' gravado com sucesso!");
            response.sendRedirect("animais");
        } else {
            String dbErr = AnimalDAO.getLastError();
            request.getSession().setAttribute("msgErr", "<b>Erro na Base de Dados:</b><br>" + dbErr);
            request.setAttribute("animal", a);
            showEditForm(request, response);
        }
    }

    /**
     * Utilitário para normalizar strings de input.
     * Remove espaços em branco e converte strings vazias para null.
     * 
     * @param s A string a processar.
     * @return A string normalizada ou null se estiver vazia ou for nula.
     */
    private String trimToNull(String s) {
        if (s == null)
            return null;
        s = s.trim();
        return s.isEmpty() ? null : s;
    }

    /**
     * Recupera o valor de um campo do pedido HTTP, suportando tanto parâmetros
     * normais como partes de pedidos multipart (upload).
     * 
     * @param request   O objeto HttpServletRequest.
     * @param fieldName O nome do campo a recuperar.
     * @return O valor do campo como string, ou null se não for encontrado.
     */
    private String getFieldValue(HttpServletRequest request, String fieldName) {
        try {
            String val = request.getParameter(fieldName);
            if (val != null)
                return val;
            Part p = request.getPart(fieldName);
            if (p != null && p.getSize() > 0 && p.getContentType() == null) {
                try (Scanner s = new Scanner(p.getInputStream(), "UTF-8")) {
                    return s.useDelimiter("\\A").hasNext() ? s.next() : "";
                }
            }
        } catch (Exception e) {
        }
        return null;
    }
}
