<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Chat - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Chat</p>
        <h1 class="page-title">${outroUsuario.name}</h1>
        <p class="page-subtitle">@${outroUsuario.username}</p>
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
            <a class="conversation" href="${pageContext.request.contextPath}/chat?chatId=${conversa.id}">
              <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, outroUsuario.photo)}" alt="${outroUsuario.name}">
              <span>
                <strong>${outroUsuario.name}</strong>
                <p>${conversa.lastMessage}</p>
              </span>
              <span class="muted">chat</span>
            </a>
          </c:forEach>
        </div>
      </section>

      <section>
        <div class="message-thread">
          <c:forEach var="mensagem" items="${mensagens}">
            <article class="message-bubble ${mensagem.senderId == usuario.uid ? 'mine' : ''}">
              <p>${mensagem.text}</p>
              <time>enviado</time>
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
