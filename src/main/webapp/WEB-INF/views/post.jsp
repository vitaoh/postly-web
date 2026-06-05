<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Publicacao - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Publicacao</p>
        <h1 class="page-title">Detalhes da publicacao</h1>
        <p class="page-subtitle">Visualize a publicacao e acompanhe os comentarios em uma tela ampla.</p>
      </div>
      <div class="page-actions">
        <a class="btn outline" href="${pageContext.request.contextPath}/home">&larr; Inicio</a>
        <c:if test="${postPrincipal.userId == usuario.uid}">
          <a class="btn" href="${pageContext.request.contextPath}/editar-post?id=${postPrincipal.id}">Editar</a>
        </c:if>
      </div>
    </header>
    <c:if test="${not empty erro}">
      <p class="alert danger">${erro}</p>
    </c:if>
    <c:if test="${not empty mensagem}">
      <p class="alert success">${mensagem}</p>
    </c:if>

    <section class="detail-shell">
      <c:set var="autorPost" value="${usuariosPorUid[postPrincipal.userId]}" />
      <article class="post-card">
        <div class="post-head">
          <a href="${pageContext.request.contextPath}/perfil?uid=${postPrincipal.userId}">
            <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, empty autorPost.photo ? usuario.photo : autorPost.photo)}" alt="${empty autorPost.name ? usuario.name : autorPost.name}">
          </a>
          <div class="post-author">
            <a href="${pageContext.request.contextPath}/perfil?uid=${postPrincipal.userId}">
              <strong>${empty autorPost.name ? usuario.name : autorPost.name}</strong>
              <span>@${empty autorPost.username ? usuario.username : autorPost.username}</span>
            </a>
          </div>
          <div class="post-actions">
            <c:if test="${postPrincipal.userId == usuario.uid}">
              <a class="icon-link outline" href="${pageContext.request.contextPath}/editar-post?id=${postPrincipal.id}">Editar</a>
              <form class="inline-form" action="${pageContext.request.contextPath}/post" method="post">
                <input type="hidden" name="action" value="delete-post">
                <input type="hidden" name="postId" value="${postPrincipal.id}">
                <button class="icon-button danger" type="submit">Excluir</button>
              </form>
            </c:if>
          </div>
        </div>
        <p class="post-text">${postPrincipal.description}</p>
        <c:if test="${not empty postPrincipal.image}">
          <img class="post-image" src="${imagemService.src(pageContext.request.contextPath, postPrincipal.image)}" alt="Midia da publicacao">
        </c:if>
        <div class="post-metrics">
          <form class="inline-form" action="${pageContext.request.contextPath}/post" method="post">
            <input type="hidden" name="action" value="like">
            <input type="hidden" name="postId" value="${postPrincipal.id}">
            <button class="text-link liked" type="submit">Curtidas ${postPrincipal.likeCount}</button>
          </form>
          <span>Comentarios ${postPrincipal.commentCount}</span>
        </div>
      </article>

      <aside class="content-card">
        <h2 class="section-title">${postPrincipal.commentCount} comentarios</h2>
        <section class="comments-list">
          <c:forEach var="comentario" items="${comentarios}">
            <c:set var="autorComentario" value="${usuariosPorUid[comentario.userId]}" />
            <article class="comment">
              <a href="${pageContext.request.contextPath}/perfil?uid=${comentario.userId}">
                <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, empty autorComentario.photo ? usuario.photo : autorComentario.photo)}" alt="${empty autorComentario.name ? usuario.name : autorComentario.name}">
              </a>
              <span>
                <a href="${pageContext.request.contextPath}/perfil?uid=${comentario.userId}">
                  <strong>${empty autorComentario.name ? usuario.name : autorComentario.name}</strong>
                  <small class="muted">@${empty autorComentario.username ? usuario.username : autorComentario.username}</small>
                </a>
                <p>${comentario.text}</p>
              </span>
              <c:if test="${comentario.userId == usuario.uid || postPrincipal.userId == usuario.uid}">
                <form class="inline-form" action="${pageContext.request.contextPath}/post" method="post">
                  <input type="hidden" name="action" value="delete-comment">
                  <input type="hidden" name="postId" value="${postPrincipal.id}">
                  <input type="hidden" name="commentId" value="${comentario.id}">
                  <button class="icon-button danger" type="submit">Excluir</button>
                </form>
              </c:if>
            </article>
          </c:forEach>
        </section>
        <form class="composer-bar comment-composer" action="${pageContext.request.contextPath}/post" method="post">
          <input type="hidden" name="action" value="comment">
          <input type="hidden" name="postId" value="${postPrincipal.id}">
          <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, usuario.photo)}" alt="${usuario.name}">
          <input class="field" name="comment" placeholder="Escreva um comentario" required>
          <button class="btn" type="submit">Enviar</button>
        </form>
      </aside>
    </section>
  </main>
</div>
</body>
</html>
