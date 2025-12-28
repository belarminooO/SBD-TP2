<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%> <%@ page import="animal.Animal" %>
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8" />
    <title>VetCare - Genealogia</title>
    <link
      rel="stylesheet"
      href="${pageContext.request.contextPath}/css/style.css"
    />
    <style>
      .tree {
        text-align: center;
        margin-top: 50px;
      }
      .node {
        border: 2px solid var(--primary);
        padding: 15px;
        border-radius: 10px;
        display: inline-block;
        background: white;
        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
      }
      .line {
        width: 2px;
        height: 30px;
        background: var(--primary);
        margin: 0 auto;
      }
    </style>
  </head>
  <body>
    <div class="header">
      <div class="container">
        <h1>VetCare Manager</h1>
        <nav>
          <a href="${pageContext.request.contextPath}/animais" class="nav-link"
            >Voltar</a
          >
        </nav>
      </div>
    </div>

    <div class="container">
      <div class="card" style="text-align: center">
        <% Animal a = (Animal) request.getAttribute("animal"); %>
        <h2>🧬 Árvore Genealógica</h2>

        <div
          class="tree-container"
          style="
            display: flex;
            flex-direction: column;
            align-items: center;
            gap: 20px;
            padding: 40px;
          "
        >
          <!-- Node Ancestors -->
          <div
            class="node ancestors"
            style="
              border: 2px dashed #6c757d;
              padding: 15px;
              border-radius: 8px;
              background: #f8f9fa;
              min-width: 200px;
            "
          >
            <div
              style="
                font-size: 0.8rem;
                color: #6c757d;
                text-transform: uppercase;
                margin-bottom: 5px;
              "
            >
              Ascendência (Pais)
            </div>
            <strong
              ><%= (a.getFiliacao() != null && !a.getFiliacao().isEmpty() ?
              a.getFiliacao() : "Não Registada") %></strong
            >
          </div>

          <!-- Connection Line -->
          <div
            style="width: 2px; height: 40px; background: var(--primary-color)"
          ></div>

          <!-- Main Animal Node -->
          <div
            class="node active-animal"
            style="
              border: 3px solid var(--primary-color);
              padding: 25px;
              border-radius: 12px;
              background: white;
              box-shadow: 0 4px 15px rgba(13, 110, 253, 0.2);
              min-width: 250px;
            "
          >
            <div
              style="
                font-size: 0.9rem;
                color: var(--primary-color);
                font-weight: bold;
                margin-bottom: 5px;
              "
            >
              Paciente
            </div>
            <h3 style="margin: 0; color: #333"><%= a.getNome() %></h3>
            <div style="margin-top: 5px; color: #666">
              <%= a.getRaca() %> - <%= a.getSexo() %>
            </div>
          </div>
        </div>

        <div style="margin-top: 30px">
          <a href="animais" class="btn btn-secondary">Voltar à Lista</a>
        </div>
      </div>
    </div>
  </body>
</html>
