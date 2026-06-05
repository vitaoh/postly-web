<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Novo post - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Criacao</p>
        <h1 class="page-title">Novo post</h1>
        <p class="page-subtitle">Publique texto, imagem e localizacao no mesmo formato usado pelo app mobile.</p>
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
        <h2 class="section-title">Firestore</h2>
        <p class="page-subtitle">${firebaseStatus}</p>
      </aside>
    </section>
  </main>
</div>
</body>
</html>
