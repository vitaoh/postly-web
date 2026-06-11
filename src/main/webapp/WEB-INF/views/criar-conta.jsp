<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Criar conta - Postly</title>
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
      <h1>Crie sua conta no Postly.</h1>
      <p>Escolha seu usuario, personalize seu perfil e comece a publicar em poucos passos.</p>
    </div>
  </section>

  <section class="auth-panel">
    <section class="auth-card">
      <h1>Criar conta</h1>
      <form class="form-stack" action="${pageContext.request.contextPath}/auth/register" method="post" data-auth-register>
        <c:if test="${not empty erro}">
          <p class="alert danger">${erro}</p>
        </c:if>
        <c:if test="${not empty mensagem}">
          <p class="alert success">${mensagem}</p>
        </c:if>
        <p class="alert danger" data-auth-message hidden></p>
        <input class="field" name="username" placeholder="Usuario" autocomplete="username">
        <input class="field" name="name" placeholder="Nome" autocomplete="name">
        <input class="field" name="email" placeholder="E-mail" type="email" autocomplete="email">
        <label class="password-field">
          <span class="sr-only">Senha</span>
          <input class="field" name="password" placeholder="Senha" type="password" autocomplete="new-password">
          <button class="password-toggle" type="button" data-toggle-password aria-label="Mostrar senha">Ver</button>
        </label>
        <label class="password-field">
          <span class="sr-only">Confirmar senha</span>
          <input class="field" name="confirmPassword" placeholder="Confirmar senha" type="password" autocomplete="new-password">
          <button class="password-toggle" type="button" data-toggle-password aria-label="Mostrar senha">Ver</button>
        </label>
        <button class="btn full" type="submit">Criar conta</button>
        <a class="text-link auth-back-link" href="${pageContext.request.contextPath}/entrar">Voltar para login</a>
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
