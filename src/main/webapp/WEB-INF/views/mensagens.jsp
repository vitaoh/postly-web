<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Mensagens - Postly</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css?v=5">
</head>
<body>
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Mensagens</p>
        <h1 class="page-title">Conversas</h1>
        <p class="page-subtitle">Retome contatos recentes e continue suas conversas no Postly.</p>
      </div>
      <div class="page-actions">
        <a class="btn outline" href="${pageContext.request.contextPath}/home">&larr; Inicio</a>
      </div>
    </header>

    <section class="messages-shell messages-shell-single">
      <section class="content-card">
        <h2 class="section-title">Caixa de entrada</h2>
        <div class="messages-list">
          <c:forEach var="conversa" items="${conversas}">
            <c:set var="conversaUsuario" value="${usuariosPorConversa[conversa.id]}" />
            <c:set var="conversaNome" value="${empty conversaUsuario.name ? 'Usuario' : conversaUsuario.name}" />
            <c:set var="conversaUsername" value="${empty conversaUsuario.username ? 'usuario' : conversaUsuario.username}" />
            <c:set var="conversaHorario" value="${tempoService.dataHoraConversa(conversa)}" />
            <a class="conversation" href="${pageContext.request.contextPath}/chat?chatId=${conversa.id}">
              <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, conversaUsuario.photo)}" alt="${conversaNome}">
              <span>
                <strong>${conversaNome}</strong>
                <small class="muted">@${conversaUsername}</small>
                <p class="conversation-preview">
                  <c:choose>
                    <c:when test="${empty conversa.lastMessage}">Sem mensagens ainda</c:when>
                    <c:when test="${conversa.lastSenderId == usuario.uid}">Voce: ${conversa.lastMessage}</c:when>
                    <c:otherwise>${conversaNome}: ${conversa.lastMessage}</c:otherwise>
                  </c:choose>
                </p>
              </span>
              <time class="conversation-time">${conversaHorario}</time>
            </a>
          </c:forEach>
          <c:if test="${empty conversas}">
            <p class="empty-state">Nenhuma conversa encontrada.</p>
          </c:if>
        </div>
      </section>
    </section>
  </main>
</div>
</body>
</html>
