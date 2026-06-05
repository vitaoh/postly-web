<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Alterar senha - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body data-context-path="${pageContext.request.contextPath}">
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Seguranca</p>
        <h1 class="page-title">Alterar senha</h1>
        <p class="page-subtitle">Use pelo menos 6 caracteres para manter compatibilidade com Firebase Auth.</p>
      </div>
      <div class="page-actions">
        <a class="btn outline" href="${pageContext.request.contextPath}/configuracoes">&larr; Configuracoes</a>
      </div>
    </header>

    <section class="settings-grid">
      <section class="settings-card">
        <form class="form-stack" action="${pageContext.request.contextPath}/mudar-senha" method="post" data-change-password>
          <c:if test="${not empty erro}">
            <p class="alert danger">${erro}</p>
          </c:if>
          <c:if test="${not empty mensagem}">
            <p class="alert success">${mensagem}</p>
          </c:if>
          <p class="alert danger" data-auth-message hidden></p>
          <label class="password-field">
            <span class="sr-only">Senha atual</span>
            <input class="field" name="currentPassword" placeholder="Senha atual" type="password" required>
            <button class="password-toggle" type="button" data-toggle-password aria-label="Mostrar senha">Ver</button>
          </label>
          <label class="password-field">
            <span class="sr-only">Nova senha</span>
            <input class="field" name="newPassword" placeholder="Nova senha" type="password" required minlength="6">
            <button class="password-toggle" type="button" data-toggle-password aria-label="Mostrar senha">Ver</button>
          </label>
          <label class="password-field">
            <span class="sr-only">Confirmar nova senha</span>
            <input class="field" name="confirmPassword" placeholder="Confirmar nova senha" type="password" required minlength="6">
            <button class="password-toggle" type="button" data-toggle-password aria-label="Mostrar senha">Ver</button>
          </label>
          <div class="media-actions">
            <a class="btn outline" href="${pageContext.request.contextPath}/configuracoes">Cancelar</a>
            <button class="btn" type="submit">Confirmar</button>
          </div>
        </form>
      </section>

      <aside class="content-card">
        <h2 class="section-title">Firebase Auth</h2>
        <p class="page-subtitle">A camada Java esta preparada para atualizar senha via Admin SDK quando o usuario autenticado estiver definido.</p>
      </aside>
    </section>
  </main>
</div>
<script src="${pageContext.request.contextPath}/assets/js/postly.js"></script>
<script type="module" src="${pageContext.request.contextPath}/assets/js/postly-auth.js"></script>
</body>
</html>
