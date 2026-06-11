<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Postly</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css?v=4">
</head>
<body>
<main class="auth-shell">
  <section class="auth-copy">
    <a class="auth-brand" href="${pageContext.request.contextPath}/welcome">
      <img src="${pageContext.request.contextPath}/assets/img/logo.png" alt="">
      <span>Postly</span>
    </a>
    <div>
      <h1>Compartilhe seus momentos.</h1>
      <p>Publique ideias, acompanhe perfis e converse com pessoas em uma experiencia simples e direta.</p>
    </div>
  </section>

  <section class="auth-panel">
    <div class="auth-card">
      <h1>Bem-vindo ao Postly</h1>
      <p class="page-subtitle">Entre na sua conta ou crie um perfil para publicar e conversar.</p>
      <br>
      <div class="form-stack">
        <a class="btn full" href="${pageContext.request.contextPath}/entrar">Entrar</a>
        <a class="btn outline full" href="${pageContext.request.contextPath}/criar-conta">Criar conta</a>
      </div>
    </div>
  </section>
</main>
</body>
</html>
