<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Perfil - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Perfil</p>
        <h1 class="page-title">${usuario.name}</h1>
        <p class="page-subtitle">@${usuario.username} &middot; ${usuario.email}</p>
      </div>
      <div class="page-actions">
        <a class="btn outline" href="${pageContext.request.contextPath}/home">&larr; Voltar ao feed</a>
        <a class="btn" href="${pageContext.request.contextPath}/configuracoes">Editar perfil</a>
      </div>
    </header>
    <c:if test="${not empty erro}">
      <p class="alert danger">${erro}</p>
    </c:if>
    <c:if test="${not empty mensagem}">
      <p class="alert success">${mensagem}</p>
    </c:if>

    <section class="dashboard-grid">
      <div class="content-column">
        <section class="profile-hero">
          <img class="avatar large" src="${imagemService.src(pageContext.request.contextPath, usuario.photo)}" alt="${usuario.name}">
          <div>
            <h1>${usuario.name}</h1>
            <p class="muted">@${usuario.username}</p>
            <div class="stats">
              <div class="stat"><strong>${postsCount}</strong><span>Posts</span></div>
              <div class="stat"><strong>1</strong><span>Seguidores</span></div>
              <div class="stat"><strong>1</strong><span>Seguindo</span></div>
            </div>
          </div>
        </section>

        <section>
          <h2 class="section-title">Publicacoes</h2>
          <c:forEach var="post" items="${posts}" varStatus="status">
            <c:set var="autor" value="${usuariosPorUid[post.userId]}" />
            <article class="post-card">
              <div class="post-head">
                <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, empty autor.photo ? usuario.photo : autor.photo)}" alt="${empty autor.name ? usuario.name : autor.name}">
                <div class="post-author">
                  <strong>${empty autor.name ? usuario.name : autor.name}</strong>
                  <span>@${empty autor.username ? usuario.username : autor.username} &middot; ${status.index + 1} post</span>
                </div>
              </div>
              <p class="post-text">${post.description}</p>
              <c:if test="${not empty post.image}">
                <a href="${pageContext.request.contextPath}/post?id=${post.id}">
                  <img class="post-image" src="${imagemService.src(pageContext.request.contextPath, post.image)}" alt="Midia do post">
                </a>
              </c:if>
              <div class="post-metrics">
                <span class="liked">Curtidas ${post.likeCount}</span>
                <a href="${pageContext.request.contextPath}/post?id=${post.id}">Comentarios ${post.commentCount}</a>
              </div>
            </article>
          </c:forEach>
        </section>
      </div>

      <aside class="right-rail">
        <section class="content-card">
          <h2 class="section-title">Conta</h2>
          <p class="page-subtitle">Gerencie nome, usuario, email e foto do perfil.</p>
          <a class="btn full" href="${pageContext.request.contextPath}/configuracoes">Abrir configuracoes</a>
        </section>
        <section class="content-card">
          <h2 class="section-title">Banco</h2>
          <p class="page-subtitle">${firebaseStatus}</p>
        </section>
      </aside>
    </section>
  </main>
</div>
</body>
</html>
