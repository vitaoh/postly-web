<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Editar post - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Edicao</p>
        <h1 class="page-title">Editar post</h1>
        <p class="page-subtitle">Ajuste descricao, imagem ou localizacao da publicacao.</p>
      </div>
      <div class="page-actions">
        <a class="btn outline" href="${pageContext.request.contextPath}/post">&larr; Voltar</a>
      </div>
    </header>

    <section class="settings-grid">
      <section class="settings-card">
        <form class="form-stack" action="${pageContext.request.contextPath}/editar-post" method="post" enctype="multipart/form-data">
          <c:if test="${not empty erro}">
            <p class="alert danger">${erro}</p>
          </c:if>
          <input type="hidden" name="postId" value="${postPrincipal.id}">
          <label for="description">Descricao</label>
          <textarea class="textarea" id="description" name="description" required>${postPrincipal.description}</textarea>
          <c:if test="${not empty postPrincipal.image}">
            <img class="post-image" src="${imagemService.src(pageContext.request.contextPath, postPrincipal.image)}" alt="Midia atual do post">
          </c:if>
          <label>Trocar imagem
            <input class="field" name="imageFile" type="file" accept="image/*">
          </label>
          <label>Localizacao
            <input class="field" name="locationName" value="${postPrincipal.locationName}">
          </label>
          <div class="media-actions">
            <input class="field" name="latitude" value="${postPrincipal.latitude}" placeholder="Latitude" inputmode="decimal">
            <input class="field" name="longitude" value="${postPrincipal.longitude}" placeholder="Longitude" inputmode="decimal">
          </div>
          <button class="btn" type="submit">Salvar alteracoes</button>
        </form>
      </section>

      <aside class="content-card">
        <h2 class="section-title">Resumo</h2>
        <p class="page-subtitle">Curtidas: ${postPrincipal.likeCount}</p>
        <p class="page-subtitle">Comentarios: ${postPrincipal.commentCount}</p>
      </aside>
    </section>
  </main>
</div>
</body>
</html>
