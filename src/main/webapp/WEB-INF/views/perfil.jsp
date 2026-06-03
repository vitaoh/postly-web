<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Profile - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="phone-shell">
  <div class="statusbar">
    <span>2:40</span>
    <span class="status-icons"><span class="signal"></span><span class="wifi"></span><span class="battery"></span></span>
  </div>
  <header class="appbar compact">
    <a class="icon-link" href="${pageContext.request.contextPath}/home" aria-label="Back">‹</a>
    <img class="logo-mark" src="${pageContext.request.contextPath}/assets/img/logo.png" alt="Postly">
    <a class="icon-link" href="${pageContext.request.contextPath}/configuracoes" aria-label="Settings">⚙</a>
  </header>
  <section class="profile-header">
    <img class="avatar large" src="${pageContext.request.contextPath}/${usuario.photo}" alt="${usuario.name}">
    <h1>${usuario.name}</h1>
    <span class="muted">@${usuario.username}</span>
    <div class="stats">
      <div class="stat"><strong>4</strong><span>Posts</span></div>
      <div class="stat"><strong>1</strong><span>Followers</span></div>
      <div class="stat"><strong>1</strong><span>Following</span></div>
    </div>
  </section>
  <main class="content">
    <h2 class="section-title">Posts</h2>
    <c:forEach var="post" items="${posts}" varStatus="status">
      <article class="post-card">
        <div class="post-head">
          <img class="avatar" src="${pageContext.request.contextPath}/${usuario.photo}" alt="${usuario.name}">
          <div class="post-author">
            <strong>${usuario.name}</strong>
            <span>@${usuario.username} · ${status.index + 4}d</span>
          </div>
        </div>
        <p class="post-text">${post.description}</p>
        <c:if test="${not empty post.image}">
          <img class="post-image" src="${pageContext.request.contextPath}/${post.image}" alt="Post media">
        </c:if>
        <div class="post-metrics">
          <span class="liked">♡ ${post.likeCount}</span>
          <a href="${pageContext.request.contextPath}/post">♡ ${post.commentCount}</a>
        </div>
      </article>
    </c:forEach>
  </main>
</div>
</body>
</html>
