<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Configuracoes - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Conta</p>
        <h1 class="page-title">Configuracoes do perfil</h1>
        <p class="page-subtitle">Atualize os dados publicos que tambem aparecem no aplicativo para celular.</p>
      </div>
      <div class="page-actions">
        <a class="btn outline" href="${pageContext.request.contextPath}/perfil">&larr; Perfil</a>
      </div>
    </header>

    <section class="settings-grid">
      <section class="settings-card">
        <form class="form-stack" action="${pageContext.request.contextPath}/configuracoes" method="post" enctype="multipart/form-data">
          <c:if test="${not empty erro}">
            <p class="alert danger">${erro}</p>
          </c:if>
          <c:if test="${not empty mensagem}">
            <p class="alert success">${mensagem}</p>
          </c:if>
          <label>Nome
            <input class="field" name="name" value="${usuario.name}" required>
          </label>
          <label>Usuario
            <input class="field" name="username" value="${usuario.username}" required>
          </label>
          <label>E-mail
            <input class="field" name="email" value="${usuario.email}" type="email" readonly>
          </label>
          <label>Foto
            <input class="field" name="photoFile" type="file" accept="image/*">
          </label>
          <button class="btn" type="submit">Salvar perfil</button>
          <a class="btn outline" href="${pageContext.request.contextPath}/mudar-senha">Alterar senha</a>
        </form>
        <form class="form-stack logout-form" action="${pageContext.request.contextPath}/auth/logout" method="post">
          <button class="btn danger full" type="submit">Sair da conta</button>
        </form>
      </section>

      <aside class="profile-card profile-summary">
        <div class="profile-photo-zone">
          <img class="avatar large" src="${imagemService.src(pageContext.request.contextPath, usuario.photo)}" alt="${usuario.name}">
          <a class="text-link" href="#">Alterar foto</a>
        </div>
        <div class="stats">
          <div class="stat"><strong>${usuarioPostsCount}</strong><span>Publicacoes</span></div>
          <div class="stat"><strong>${usuarioComentariosCount}</strong><span>Comentarios</span></div>
          <div class="stat"><strong>${conversasCount}</strong><span>Conversas</span></div>
        </div>
      </aside>
    </section>
  </main>
</div>
</body>
</html>
