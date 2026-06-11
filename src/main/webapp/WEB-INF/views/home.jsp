<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Inicio - Postly</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css?v=5">
</head>
<body>
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Inicio</p>
        <h1 class="page-title">Compartilhe e acompanhe publicacoes</h1>
        <p class="page-subtitle">Veja novidades, encontre pessoas e participe das conversas do Postly.</p>
      </div>
      <div class="page-actions">
        <a class="btn outline" href="${pageContext.request.contextPath}/mensagens">Abrir mensagens</a>
        <a class="btn" href="${pageContext.request.contextPath}/postar">Nova publicacao</a>
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
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" aria-hidden="true"><circle cx="11" cy="11" r="7"/><path d="m20 20-3.8-3.8"/></svg>
            <input type="search" name="busca" value="${busca}" placeholder="Buscar descricao, cidade ou usuario">
            <button class="text-link" type="submit">Buscar</button>
          </form>
          <nav class="tabs" aria-label="Filtro do feed">
            <a class="tab ${feedAtivo == 'for-you' ? 'active' : ''}" href="${forYouUrl}">Para voce</a>
            <a class="tab ${feedAtivo == 'following' ? 'active' : ''}" href="${followingUrl}">Seguindo</a>
          </nav>
        </section>

        <c:if test="${empty posts}">
          <section class="empty-state">
            Nenhuma publicacao encontrada para esse filtro.
          </section>
        </c:if>

        <div class="feed-posts" id="feedPosts">
        <c:forEach var="post" items="${posts}" varStatus="status">
          <c:set var="autor" value="${usuariosPorUid[post.userId]}" />
          <article class="post-card">
            <div class="post-head">
              <a href="${pageContext.request.contextPath}/perfil?uid=${post.userId}">
                <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, empty autor.photo ? usuario.photo : autor.photo)}" alt="${empty autor.name ? usuario.name : autor.name}">
              </a>
              <div class="post-author">
                <a href="${pageContext.request.contextPath}/perfil?uid=${post.userId}">
                  <strong>${empty autor.name ? usuario.name : autor.name}</strong>
                  <span>@${empty autor.username ? usuario.username : autor.username}<c:if test="${post.timestamp > 0}"> &middot; ${tempoService.relativo(post.timestamp)}</c:if></span>
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
            <div class="post-metrics">
              <c:set var="jaCurtiu" value="${not empty post.likedBy and post.likedBy.contains(usuario.uid)}" />
              <form class="inline-form" action="${pageContext.request.contextPath}/post" method="post">
                <input type="hidden" name="action" value="like">
                <input type="hidden" name="postId" value="${post.id}">
                <input type="hidden" name="redirect" value="home">
                <input type="hidden" name="feed" value="${feedAtivo}">
                <input type="hidden" name="busca" value="${busca}">
                <button class="chip like-chip ${jaCurtiu ? 'active' : ''}" type="submit" title="${jaCurtiu ? 'Remover curtida' : 'Curtir'}">
                  <svg width="16" height="16" viewBox="0 0 24 24" fill="${jaCurtiu ? 'currentColor' : 'none'}" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.6l-1-1a5.5 5.5 0 0 0-7.8 7.8l1 1L12 21.2l7.8-7.8 1-1a5.5 5.5 0 0 0 0-7.8z"/></svg>
                  ${post.likeCount}
                  <span>${jaCurtiu ? 'Curtido' : 'Curtir'}</span>
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
        </div>

        <c:if test="${temMais}">
          <c:url var="maisUrl" value="/home">
            <c:param name="feed" value="${feedAtivo}" />
            <c:param name="busca" value="${busca}" />
            <c:param name="cursor" value="${proximoCursor}" />
          </c:url>
          <a class="btn outline full" id="loadMore" href="${maisUrl}">Carregar mais publicacoes</a>
        </c:if>
      </div>

      <aside class="right-rail">
        <section class="profile-card profile-summary">
          <img class="avatar large" src="${imagemService.src(pageContext.request.contextPath, usuario.photo)}" alt="${usuario.name}">
          <div>
            <h2 class="section-title">${usuario.name}</h2>
            <p class="muted">@${usuario.username}</p>
          </div>
          <div class="stats">
            <div class="stat"><strong>${usuarioPostsCount}</strong><span>Publicacoes</span></div>
            <div class="stat"><strong>${usuarioComentariosCount}</strong><span>Comentarios</span></div>
            <div class="stat"><strong>${conversasCount}</strong><span>Conversas</span></div>
          </div>
        </section>

        <section class="content-card">
          <h2 class="section-title">Atalhos rapidos</h2>
          <div class="quick-actions">
            <a class="btn full" href="${pageContext.request.contextPath}/postar">Criar publicacao</a>
            <a class="btn outline full" href="${pageContext.request.contextPath}/mensagens">Abrir mensagens</a>
            <a class="btn outline full" href="${pageContext.request.contextPath}/configuracoes">Editar perfil</a>
          </div>
        </section>
      </aside>
    </section>
  </main>
</div>
<script>
  (function () {
    var botao = document.getElementById('loadMore');
    var feed = document.getElementById('feedPosts');
    if (!botao || !feed) {
      return;
    }

    botao.addEventListener('click', function (evento) {
      evento.preventDefault();
      botao.textContent = 'Carregando...';

      fetch(botao.href)
        .then(function (resposta) { return resposta.text(); })
        .then(function (html) {
          var doc = new DOMParser().parseFromString(html, 'text/html');
          var novos = doc.querySelectorAll('#feedPosts > article');
          novos.forEach(function (post) {
            feed.appendChild(document.adoptNode(post));
          });

          var proximo = doc.getElementById('loadMore');
          if (proximo) {
            botao.href = proximo.getAttribute('href');
            botao.textContent = 'Carregar mais publicacoes';
          } else {
            botao.remove();
          }
        })
        .catch(function () {
          // fallback: navega normalmente para a proxima pagina
          window.location.href = botao.href;
        });
    });
  })();
</script>
</body>
</html>
