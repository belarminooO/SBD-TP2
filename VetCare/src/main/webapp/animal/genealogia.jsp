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
      <div class="card tree">
        <% Animal a = (Animal) request.getAttribute("animal"); %>
        <h2>Árvore Genealógica: <%= a.getNome() %></h2>

        <div class="node">
          <strong>Pai/Mãe (Filiação):</strong><br />
          <%= (a.getFiliacao() != null && !a.getFiliacao().isEmpty() ?
          a.getFiliacao() : "Desconhecido") %>
        </div>

        <div class="line"></div>

        <div class="node" style="background: var(--bg-soft)">
          <strong>Animal:</strong><br />
          <%= a.getNome() %>
        </div>

        <p style="margin-top: 20px; color: #666; font-style: italic">
          Nota: A filiação é registada no campo de texto livre conforme o
          enunciado.
        </p>
      </div>
    </div>
  </body>
</html>
