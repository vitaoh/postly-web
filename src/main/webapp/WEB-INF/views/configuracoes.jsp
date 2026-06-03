<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>My Profile - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="phone-shell auth-gradient">
  <div class="statusbar">
    <span>2:40</span>
    <span class="status-icons"><span class="signal"></span><span class="wifi"></span><span class="battery"></span></span>
  </div>
  <main class="settings-page">
    <header class="appbar compact">
      <a class="icon-link" href="${pageContext.request.contextPath}/perfil" aria-label="Back">‹</a>
      <h1 class="app-title">My Profile</h1>
      <span></span>
    </header>
    <div class="profile-photo-zone">
      <img class="avatar large" src="${imagemService.src(pageContext.request.contextPath, usuario.photo)}" alt="${usuario.name}">
      <a class="text-link" href="#">Change photo</a>
    </div>
    <section class="settings-card">
      <form class="form-stack" action="${pageContext.request.contextPath}/perfil" method="get">
        <label>Name<input class="field" name="name" value="${usuario.name}"></label>
        <label>Username<input class="field" name="username" value="@${usuario.username}"></label>
        <label>E-mail<input class="field" name="email" value="${usuario.email}" type="email"></label>
        <hr>
        <button class="btn full" type="submit">Save</button>
        <a class="btn outline full" href="${pageContext.request.contextPath}/mudar-senha">Change password</a>
        <a class="btn danger full" href="${pageContext.request.contextPath}/welcome">Sign out</a>
      </form>
    </section>
  </main>
</div>
</body>
</html>
