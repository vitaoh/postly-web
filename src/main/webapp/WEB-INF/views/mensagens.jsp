<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Messages - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="phone-shell">
  <div class="statusbar">
    <span>2:39</span>
    <span class="status-icons"><span class="signal"></span><span class="wifi"></span><span class="battery"></span></span>
  </div>
  <header class="appbar compact">
    <a class="icon-link" href="${pageContext.request.contextPath}/home" aria-label="Back">‹</a>
    <img class="logo-mark" src="${pageContext.request.contextPath}/assets/img/logo.png" alt="Postly">
    <span></span>
  </header>
  <main class="content">
    <section class="messages-list">
      <c:forEach var="conversa" items="${conversas}">
        <a class="conversation" href="${pageContext.request.contextPath}/chat">
          <img class="avatar" src="${pageContext.request.contextPath}/${outroUsuario.photo}" alt="${outroUsuario.name}">
          <span>
            <strong>${outroUsuario.name}</strong>
            <p>You: ${conversa.lastMessage}</p>
          </span>
          <span class="muted">1d</span>
        </a>
      </c:forEach>
    </section>
  </main>
</div>
</body>
</html>
