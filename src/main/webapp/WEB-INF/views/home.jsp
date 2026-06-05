<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Inicio - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Feed</p>
        <h1 class="page-title">Compartilhe e acompanhe posts</h1>
        <p class="page-subtitle">Interface web conectada ao mesmo Firestore usado pelo aplicativo mobile.</p>
      </div>
      <div class="page-actions">
        <a class="btn outline" href="${pageContext.request.contextPath}/mensagens">Abrir mensagens</a>
        <a class="btn" href="${pageContext.request.contextPath}/postar">Novo post</a>
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
        <section class="content-card toolbar">
          <c:url var="forYouUrl" value="/home">
            <c:param name="feed" value="for-you" />
            <c:param name="busca" value="${busca}" />
          </c:url>
          <c:url var="followingUrl" value="/home">
            <c:param name="feed" value="following" />
            <c:param name="busca" value="${busca}" />
          </c:url>
          <form class="search-box" action="${pageContext.request.contextPath}/home" method="get">
            <input type="hidden" name="feed" value="${feedAtivo}">
            <span aria-hidden="true">Buscar</span>
            <input type="search" name="busca" value="${busca}" placeholder="Descricao, cidade ou usuario">
            <button class="text-link" type="submit">Buscar</button>
          </form>
          <nav class="tabs" aria-label="Filtro do feed">
            <a class="tab ${feedAtivo == 'for-you' ? 'active' : ''}" href="${forYouUrl}">Para voce</a>
            <a class="tab ${feedAtivo == 'following' ? 'active' : ''}" href="${followingUrl}">Seguindo</a>
          </nav>
        </section>

        <c:if test="${empty posts}">
          <section class="empty-state">
            Nenhum post encontrado para esse filtro.
          </section>
        </c:if>

        <c:forEach var="post" items="${posts}" varStatus="status">
          <c:set var="autor" value="${usuariosPorUid[post.userId]}" />
          <article class="post-card">
            <div class="post-head">
              <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, empty autor.photo ? usuario.photo : autor.photo)}" alt="${empty autor.name ? usuario.name : autor.name}">
              <div class="post-author">
                <strong>${empty autor.name ? usuario.name : autor.name}</strong>
                <span>@${empty autor.username ? usuario.username : autor.username} &middot; ${status.index + 1} post</span>
              </div>
              <div class="post-actions">
                <c:if test="${post.userId == usuario.uid}">
                  <a class="icon-link outline" href="${pageContext.request.contextPath}/editar-post?id=${post.id}" aria-label="Editar post">Editar</a>
                  <form class="inline-form" action="${pageContext.request.contextPath}/post" method="post">
                    <input type="hidden" name="action" value="delete-post">
                    <input type="hidden" name="postId" value="${post.id}">
                    <button class="icon-button danger" type="submit" aria-label="Excluir post">Excluir</button>
                  </form>
                </c:if>
              </div>
            </div>
            <p class="post-text">${post.description}</p>
            <c:if test="${not empty post.image}">
              <a href="${pageContext.request.contextPath}/post?id=${post.id}">
                <img class="post-image" src="${imagemService.src(pageContext.request.contextPath, post.image)}" alt="Midia do post">
              </a>
            </c:if>
            <div class="post-metrics">
              <form class="inline-form" action="${pageContext.request.contextPath}/post" method="post">
                <input type="hidden" name="action" value="like">
                <input type="hidden" name="postId" value="${post.id}">
                <button class="text-link liked" type="submit">Curtidas ${post.likeCount}</button>
              </form>
              <a href="${pageContext.request.contextPath}/post?id=${post.id}">Comentarios ${post.commentCount}</a>
            </div>
          </article>
        </c:forEach>
      </div>

      <aside class="right-rail">
        <section class="profile-card profile-summary">
          <img class="avatar large" src="${imagemService.src(pageContext.request.contextPath, usuario.photo)}" alt="${usuario.name}">
          <div>
            <h2 class="section-title">${usuario.name}</h2>
            <p class="muted">@${usuario.username}</p>
          </div>
          <div class="stats">
            <div class="stat"><strong>${postsCount}</strong><span>Posts</span></div>
            <div class="stat"><strong>${comentariosCount}</strong><span>Comentarios</span></div>
            <div class="stat"><strong>${conversasCount}</strong><span>Conversas</span></div>
          </div>
        </section>

        <section class="content-card">
          <h2 class="section-title">Status do Firestore</h2>
          <p class="page-subtitle">${firebaseStatus}</p>
          <div class="stats">
            <div class="stat"><strong>${usuariosCount}</strong><span>Usuarios</span></div>
            <div class="stat"><strong>${postsCount}</strong><span>Posts</span></div>
            <div class="stat"><strong>${conversasCount}</strong><span>Chats</span></div>
          </div>
        </section>
      </aside>
    </section>
  </main>
</div>
</body>
</html>
