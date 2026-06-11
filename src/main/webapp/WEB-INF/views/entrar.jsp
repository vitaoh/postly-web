<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Entrar - Postly</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css?v=4">
</head>
<body data-context-path="${pageContext.request.contextPath}">
<main class="auth-shell">
  <section class="auth-copy">
    <a class="auth-brand" href="${pageContext.request.contextPath}/welcome">
      <img src="${pageContext.request.contextPath}/assets/img/logo.png" alt="">
      <span>Postly</span>
    </a>
    <div>
      <h1>Entre e continue de onde parou.</h1>
      <p>Acesse suas publicacoes, mensagens e perfil em uma experiencia organizada para desktop.</p>
    </div>
  </section>

  <section class="auth-panel">
    <section class="auth-card">
      <h1>Entrar</h1>
      <form class="form-stack" action="${pageContext.request.contextPath}/auth/session" method="post" data-auth-login>
        <c:if test="${not empty erro}">
          <p class="alert danger">${erro}</p>
        </c:if>
        <c:if test="${not empty mensagem}">
          <p class="alert success">${mensagem}</p>
        </c:if>
        <p class="alert danger" data-auth-message hidden></p>
        <input class="field" name="login" placeholder="E-mail ou usuario" autocomplete="username">
        <label class="password-field">
          <span class="sr-only">Senha</span>
          <input class="field" name="password" placeholder="Senha" type="password" autocomplete="current-password">
          <button class="password-toggle" type="button" data-toggle-password aria-label="Mostrar senha">Ver</button>
        </label>
        <button class="btn full" type="submit">Entrar</button>
        <button class="btn outline full" type="button" data-google-login>Entrar com Google</button>
        <a class="btn outline full" href="${pageContext.request.contextPath}/criar-conta">Criar nova conta</a>
        <button class="text-link" type="button" data-reset-password>Esqueci minha senha</button>

        <section class="profile-complete" data-google-profile hidden>
          <h2>Completar perfil</h2>
          <div class="google-profile-preview">
            <img class="avatar large" data-google-photo src="${pageContext.request.contextPath}/assets/img/avatar-demo.svg" alt="">
            <span>
              <strong data-google-name>Conta Google</strong>
              <small class="muted" data-google-email></small>
            </span>
          </div>
          <input class="field" name="googleName" placeholder="Nome">
          <input class="field" name="googleUsername" placeholder="Usuario">
          <button class="btn full" type="button" data-complete-google>Salvar e entrar</button>
        </section>
      </form>
    </section>
  </section>
</main>
<script src="${pageContext.request.contextPath}/assets/js/postly.js"></script>
<script src="https://www.gstatic.com/firebasejs/10.12.5/firebase-app-compat.js"></script>
<script src="https://www.gstatic.com/firebasejs/10.12.5/firebase-auth-compat.js"></script>
<script src="${pageContext.request.contextPath}/assets/js/postly-auth.js"></script>
</body>
</html>
