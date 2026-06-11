<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Nova publicacao - Postly</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css?v=4">
</head>
<body>
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Criacao</p>
        <h1 class="page-title">Nova publicacao</h1>
        <p class="page-subtitle">Conte o que aconteceu, adicione uma imagem e ajude outras pessoas a encontrarem sua publicacao.</p>
      </div>
      <div class="page-actions">
        <a class="btn outline" href="${pageContext.request.contextPath}/home">&larr; Cancelar</a>
      </div>
    </header>

    <section class="settings-grid">
      <section class="settings-card">
        <form class="form-stack" action="${pageContext.request.contextPath}/postar" method="post" enctype="multipart/form-data">
          <c:if test="${not empty erro}">
            <p class="alert danger">${erro}</p>
          </c:if>
          <label for="description">Descricao</label>
          <textarea class="textarea" id="description" name="description" placeholder="O que voce esta pensando?" required></textarea>
          <label>Imagem
            <input class="field" name="imageFile" type="file" accept="image/*">
          </label>
          <label>Localizacao
            <input class="field" name="locationName" placeholder="Cidade, academia, escola...">
          </label>
          <div class="media-actions">
            <input class="field" name="latitude" placeholder="Latitude" inputmode="decimal">
            <input class="field" name="longitude" placeholder="Longitude" inputmode="decimal">
          </div>
          <button class="btn" type="submit">Publicar</button>
        </form>
      </section>

      <aside class="content-card">
        <h2 class="section-title">Dicas para publicar</h2>
        <div class="tips-list">
          <p>Use uma descricao clara e direta.</p>
          <p>Adicione uma imagem quando ela ajudar a contar melhor o momento.</p>
          <p>Informe a localizacao apenas quando fizer sentido para a publicacao.</p>
        </div>
      </aside>
    </section>
  </main>
</div>
</body>
</html>
