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
            <c:set var="conversaUid" value="${empty conversaUsuario.uid ? outroUsuario.uid : conversaUsuario.uid}" />
            <c:set var="conversaNome" value="${empty conversaUsuario.name ? outroUsuario.name : conversaUsuario.name}" />
            <c:set var="conversaUsername" value="${empty conversaUsuario.username ? outroUsuario.username : conversaUsuario.username}" />
            <c:set var="conversaFoto" value="${empty conversaUsuario.photo ? outroUsuario.photo : conversaUsuario.photo}" />
            <c:set var="conversaHorario" value="${tempoService.dataHoraConversa(conversa)}" />
            <article class="conversation">
              <a href="${pageContext.request.contextPath}/perfil?uid=${conversaUid}">
                <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, conversaFoto)}" alt="${conversaNome}">
              </a>
              <span>
                <a href="${pageContext.request.contextPath}/perfil?uid=${conversaUid}">
                  <strong>${conversaNome}</strong>
                  <small class="muted">@${conversaUsername}</small>
                </a>
                <p>${empty conversaHorario ? 'Sem mensagens ainda' : conversaHorario}</p>
              </span>
              <a class="muted" href="${pageContext.request.contextPath}/chat?chatId=${conversa.id}">abrir</a>
            </article>
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
