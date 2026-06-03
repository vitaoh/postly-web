<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Home - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="phone-shell">
  <div class="statusbar">
    <span>2:38</span>
    <span class="status-icons"><span class="signal"></span><span class="wifi"></span><span class="battery"></span></span>
  </div>
  <header class="appbar">
    <a href="${pageContext.request.contextPath}/perfil" aria-label="Profile">
      <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, usuario.photo)}" alt="${usuario.name}">
    </a>
    <img class="logo-mark" src="${pageContext.request.contextPath}/assets/img/logo.png" alt="Postly">
    <a class="icon-link" href="${pageContext.request.contextPath}/mensagens" aria-label="Messages">▱</a>
  </header>
  <section class="home-toolbar">
    <label class="search-box">
      <span aria-hidden="true">⌕</span>
      <input type="search" placeholder="Search by description, city or user...">
    </label>
    <nav class="tabs" aria-label="Feed filter">
      <a class="tab" href="#">For You</a>
      <a class="tab active" href="#">Following</a>
    </nav>
  </section>
  <main class="content">
    <c:forEach var="post" items="${posts}" varStatus="status">
      <article class="post-card">
        <div class="post-head">
          <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, usuario.photo)}" alt="${usuario.name}">
          <div class="post-author">
            <strong>${usuario.name}</strong>
            <span>@${usuario.username} · ${status.index + 4}d</span>
          </div>
          <div class="post-actions">
            <a class="icon-link outline" href="${pageContext.request.contextPath}/editar-post" aria-label="Edit post">✎</a>
            <button class="icon-button danger" type="button" aria-label="Delete post">⌫</button>
          </div>
        </div>
        <p class="post-text">${post.description}</p>
        <c:if test="${not empty post.image}">
          <a href="${pageContext.request.contextPath}/post">
            <img class="post-image" src="${imagemService.src(pageContext.request.contextPath, post.image)}" alt="Post media">
          </a>
        </c:if>
        <div class="post-metrics">
          <span class="liked">♡ ${post.likeCount}</span>
          <a href="${pageContext.request.contextPath}/post">♡ ${post.commentCount}</a>
        </div>
      </article>
    </c:forEach>
  </main>
  <a class="fab" href="${pageContext.request.contextPath}/postar">＋ New post</a>
</div>
</body>
</html>
