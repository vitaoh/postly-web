<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Post - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="phone-shell">
  <div class="statusbar">
    <span>2:41</span>
    <span class="status-icons"><span class="signal"></span><span class="wifi"></span><span class="battery"></span></span>
  </div>
  <header class="appbar compact">
    <a class="icon-link" href="${pageContext.request.contextPath}/home" aria-label="Back">‹</a>
    <img class="logo-mark" src="${pageContext.request.contextPath}/assets/img/logo.png" alt="Postly">
    <span></span>
  </header>
  <main class="content">
    <article class="post-card">
      <div class="post-head">
        <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, usuario.photo)}" alt="${usuario.name}">
        <div class="post-author">
          <strong>${usuario.name}</strong>
          <span>@${usuario.username} · 4d</span>
        </div>
        <div class="post-actions">
          <a class="icon-link outline" href="${pageContext.request.contextPath}/editar-post" aria-label="Edit post">✎</a>
          <button class="icon-button danger" type="button" aria-label="Delete post">⌫</button>
        </div>
      </div>
      <p class="post-text">${postPrincipal.description}</p>
      <img class="post-image" src="${imagemService.src(pageContext.request.contextPath, postPrincipal.image)}" alt="Post media">
      <div class="post-metrics">
        <span class="liked">♡ ${postPrincipal.likeCount}</span>
        <span>♡ ${postPrincipal.commentCount}</span>
      </div>
    </article>

    <h2 class="section-title">${postPrincipal.commentCount} comments</h2>
    <section class="comments-list">
      <c:forEach var="comentario" items="${comentarios}">
        <article class="comment">
          <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, usuario.photo)}" alt="${usuario.name}">
          <span>
            <strong>${usuario.name}</strong> <span class="muted">@${usuario.username} · 1d</span>
            <p>${comentario.text}</p>
          </span>
          <button class="icon-button danger" type="button" aria-label="Delete comment">⌫</button>
        </article>
      </c:forEach>
    </section>
    <form class="composer-bar" action="${pageContext.request.contextPath}/post" method="get">
      <img class="avatar comment-avatar" src="${imagemService.src(pageContext.request.contextPath, usuario.photo)}" alt="${usuario.name}">
      <input class="field" name="comment" placeholder="Write a comment...">
      <button class="btn" type="submit">➜</button>
    </form>
  </main>
</div>
</body>
</html>
