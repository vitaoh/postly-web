<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Chat - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="phone-shell">
  <div class="statusbar">
    <span>2:39</span>
    <span class="status-icons"><span class="signal"></span><span class="wifi"></span><span class="battery"></span></span>
  </div>
  <header class="appbar compact">
    <a class="icon-link" href="${pageContext.request.contextPath}/mensagens" aria-label="Back">‹</a>
    <div class="chat-header">
      <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, outroUsuario.photo)}" alt="${outroUsuario.name}">
      <span class="chat-header-text">
        <strong>${outroUsuario.name}</strong>
        <span>@${outroUsuario.username}</span>
      </span>
    </div>
    <span></span>
  </header>

  <main class="message-thread">
    <c:forEach var="mensagem" items="${mensagens}">
      <article class="message-bubble mine">
        <p>${mensagem.text}</p>
        <time>1d</time>
      </article>
    </c:forEach>
    <article class="message-bubble">
      <p>Recebi aqui pelo mobile.</p>
      <time>1d</time>
    </article>
  </main>

  <form class="composer-bar" action="${pageContext.request.contextPath}/chat" method="get">
    <span></span>
    <input class="field" name="message" placeholder="Write a message...">
    <button class="btn" type="submit">➜</button>
  </form>
</div>
</body>
</html>
