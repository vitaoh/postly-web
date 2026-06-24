<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Perfil - Postly</title>
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css?v=7">
</head>
<body>
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Perfil</p>
        <h1 class="page-title">${perfil.name}</h1>
        <p class="page-subtitle">@${perfil.username} &middot; ${perfil.email}</p>
      </div>
      <div class="page-actions">
        <a class="btn outline" href="${pageContext.request.contextPath}/home">&larr; Voltar ao inicio</a>
        <c:choose>
          <c:when test="${perfilEhAtual}">
            <a class="btn" href="${pageContext.request.contextPath}/configuracoes">Editar perfil</a>
          </c:when>
          <c:otherwise>
            <form class="inline-form" action="${pageContext.request.contextPath}/perfil" method="post">
              <input type="hidden" name="action" value="toggle-follow">
              <input type="hidden" name="uid" value="${perfil.uid}">
              <button class="btn outline" type="submit">${perfilSeguidoPeloAtual ? 'Deixar de seguir' : 'Seguir'}</button>
            </form>
            <a class="btn" href="${pageContext.request.contextPath}/chat?otherUid=${perfil.uid}">Mensagem</a>
          </c:otherwise>
        </c:choose>
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
          <img class="avatar large" src="${imagemService.src(pageContext.request.contextPath, perfil.photo)}" alt="${perfil.name}">
          <div>
            <h1>${perfil.name}</h1>
            <p class="muted">@${perfil.username}</p>
            <div class="stats">
              <div class="stat"><strong>${postsCount}</strong><span>Publicacoes</span></div>
              <div class="stat"><strong>${perfilSeguidoresCount}</strong><span>Seguidores</span></div>
              <div class="stat"><strong>${perfilSeguindoCount}</strong><span>Seguindo</span></div>
            </div>
          </div>
        </section>

        <section>
          <h2 class="section-title">Publicacoes</h2>
          <c:forEach var="post" items="${posts}" varStatus="status">
            <c:set var="autor" value="${usuariosPorUid[post.userId]}" />
            <article class="post-card" id="post-${post.id}">
              <div class="post-head">
                <a href="${pageContext.request.contextPath}/perfil?uid=${post.userId}">
                  <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, empty autor.photo ? perfil.photo : autor.photo)}" alt="${empty autor.name ? perfil.name : autor.name}">
                </a>
                <div class="post-author">
                  <a href="${pageContext.request.contextPath}/perfil?uid=${post.userId}">
                    <strong>${empty autor.name ? perfil.name : autor.name}</strong>
                    <span>@${empty autor.username ? perfil.username : autor.username}<c:if test="${post.timestamp > 0}"> &middot; ${tempoService.relativo(post.timestamp)}</c:if></span>
                  </a>
                </div>
                <div class="post-actions">
                  <c:if test="${post.userId == usuario.uid}">
                    <a class="icon-link outline" href="${pageContext.request.contextPath}/editar-post?id=${post.id}" aria-label="Editar publicacao">Editar</a>
                    <form class="inline-form" action="${pageContext.request.contextPath}/post" method="post">
                      <input type="hidden" name="action" value="delete-post">
                      <input type="hidden" name="postId" value="${post.id}">
                      <button class="icon-button danger" type="submit" aria-label="Excluir publicacao">Excluir</button>
                    </form>
                  </c:if>
                </div>
              </div>
              <a class="post-open" href="${pageContext.request.contextPath}/post?id=${post.id}" aria-label="Abrir publicacao">
                <p class="post-text">${post.description}</p>
              </a>
              <c:if test="${not empty post.image}">
                <a href="${pageContext.request.contextPath}/post?id=${post.id}">
                  <img class="post-image" src="${imagemService.src(pageContext.request.contextPath, post.image)}" alt="Midia da publicacao">
                </a>
              </c:if>
              <c:if test="${not empty post.locationName}">
                <p class="post-location">
                  <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 21s-7-6.3-7-11a7 7 0 0 1 14 0c0 4.7-7 11-7 11z"/><circle cx="12" cy="10" r="2.5"/></svg>
                  ${post.locationName}
                </p>
              </c:if>
              <div class="post-metrics">
                <c:set var="jaCurtiu" value="${not empty post.likedBy and post.likedBy.contains(usuario.uid)}" />
                <form class="inline-form like-form" action="${pageContext.request.contextPath}/post" method="post">
                  <input type="hidden" name="action" value="like">
                  <input type="hidden" name="postId" value="${post.id}">
                  <input type="hidden" name="redirect" value="perfil">
                  <input type="hidden" name="uid" value="${perfil.uid}">
                  <button class="chip like-chip ${jaCurtiu ? 'active' : ''}" type="submit" title="${jaCurtiu ? 'Remover curtida' : 'Curtir'}">
                    <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.6l-1-1a5.5 5.5 0 0 0-7.8 7.8l1 1L12 21.2l7.8-7.8 1-1a5.5 5.5 0 0 0 0-7.8z"/></svg>
                    <span class="like-count">${post.likeCount}</span>
                    <span class="like-label">${jaCurtiu ? 'Curtido' : 'Curtir'}</span>
                  </button>
                </form>
                <a class="chip" href="${pageContext.request.contextPath}/post?id=${post.id}">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M21 12c0 4.1-4 7.5-9 7.5-1.2 0-2.3-.2-3.3-.5L3 21l1.6-4.1C3.6 15.6 3 13.9 3 12c0-4.1 4-7.5 9-7.5s9 3.4 9 7.5z"/></svg>
                  ${post.commentCount}
                  <span>Comentarios</span>
                </a>
              </div>
            </article>
          </c:forEach>
        </section>
      </div>

      <aside class="right-rail">
        <section class="content-card">
          <h2 class="section-title">Conta</h2>
          <c:choose>
            <c:when test="${perfilEhAtual}">
              <p class="page-subtitle">Gerencie nome, usuario, email e foto do perfil.</p>
              <a class="btn full" href="${pageContext.request.contextPath}/configuracoes">Abrir configuracoes</a>
            </c:when>
            <c:otherwise>
              <p class="page-subtitle">Veja publicacoes publicas e converse com @${perfil.username}.</p>
              <a class="btn full" href="${pageContext.request.contextPath}/chat?otherUid=${perfil.uid}">Enviar mensagem</a>
            </c:otherwise>
          </c:choose>
        </section>
        <section class="content-card">
          <h2 class="section-title">Resumo</h2>
          <div class="stats">
            <div class="stat"><strong>${postsCount}</strong><span>Publicacoes</span></div>
            <div class="stat"><strong>${perfilSeguidoresCount}</strong><span>Seguidores</span></div>
            <div class="stat"><strong>${perfilSeguindoCount}</strong><span>Seguindo</span></div>
          </div>
        </section>
      </aside>
    </section>
  </main>
</div>
<script src="${pageContext.request.contextPath}/assets/js/postly-like.js?v=2"></script>
</body>
</html>
