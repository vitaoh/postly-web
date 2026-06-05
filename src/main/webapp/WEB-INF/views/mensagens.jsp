<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Mensagens - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Mensagens</p>
        <h1 class="page-title">Conversas</h1>
        <p class="page-subtitle">Acompanhe conversas sincronizadas com a colecao chats do Firestore.</p>
      </div>
      <div class="page-actions">
        <a class="btn outline" href="${pageContext.request.contextPath}/home">&larr; Feed</a>
      </div>
    </header>

    <section class="messages-shell">
      <section class="content-card">
        <h2 class="section-title">Caixa de entrada</h2>
        <div class="messages-list">
          <c:forEach var="conversa" items="${conversas}">
            <a class="conversation" href="${pageContext.request.contextPath}/chat?chatId=${conversa.id}">
              <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, outroUsuario.photo)}" alt="${outroUsuario.name}">
              <span>
                <strong>${outroUsuario.name}</strong>
                <p>Voce: ${conversa.lastMessage}</p>
              </span>
              <span class="muted">recente</span>
            </a>
          </c:forEach>
        </div>
      </section>

      <section class="content-card">
        <h2 class="section-title">Previa</h2>
        <p class="page-subtitle">Selecione uma conversa para abrir o chat completo.</p>
        <a class="btn" href="${pageContext.request.contextPath}/chat?chatId=${chatAtual.id}">Abrir conversa principal</a>
      </section>
    </section>
  </main>
</div>
</body>
</html>
