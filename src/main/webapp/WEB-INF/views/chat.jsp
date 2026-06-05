<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Conversa - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Conversa</p>
        <h1 class="page-title"><a href="${pageContext.request.contextPath}/perfil?uid=${outroUsuario.uid}">${outroUsuario.name}</a></h1>
        <p class="page-subtitle"><a href="${pageContext.request.contextPath}/perfil?uid=${outroUsuario.uid}">@${outroUsuario.username}</a></p>
      </div>
      <div class="page-actions">
        <a class="btn outline" href="${pageContext.request.contextPath}/mensagens">&larr; Conversas</a>
      </div>
    </header>
    <c:if test="${not empty erro}">
      <p class="alert danger">${erro}</p>
    </c:if>
    <c:if test="${not empty mensagem}">
      <p class="alert success">${mensagem}</p>
    </c:if>

    <section class="messages-shell">
      <section class="content-card">
        <h2 class="section-title">Conversas</h2>
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

      <section>
        <div class="message-thread">
          <c:forEach var="mensagem" items="${mensagens}">
            <article class="message-bubble ${mensagem.senderId == usuario.uid ? 'mine' : ''}">
              <p>${mensagem.text}</p>
              <time>${tempoService.dataHora(mensagem.timestamp)}</time>
            </article>
          </c:forEach>
        </div>
        <form class="composer-bar" action="${pageContext.request.contextPath}/chat" method="post">
          <input type="hidden" name="chatId" value="${chatAtual.id}">
          <input class="field" name="message" placeholder="Escreva uma mensagem" required>
          <button class="btn" type="submit">Enviar</button>
        </form>
      </section>
    </section>
  </main>
</div>
</body>
</html>
