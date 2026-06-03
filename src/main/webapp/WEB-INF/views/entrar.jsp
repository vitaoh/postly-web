<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Sign in - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="phone-shell auth-gradient">
  <div class="statusbar">
    <span>2:37</span>
    <span class="status-icons"><span class="signal"></span><span class="wifi"></span><span class="battery"></span></span>
  </div>
  <main class="auth-page">
    <img class="logo-large" src="${pageContext.request.contextPath}/assets/img/logo.png" alt="Postly">
    <p class="auth-subtitle">Sign in to Postly</p>
    <section class="auth-card">
      <form class="form-stack" action="${pageContext.request.contextPath}/home" method="get">
        <input class="field" name="login" placeholder="E-mail or username" autocomplete="username">
        <label class="password-field">
          <span class="sr-only">Password</span>
          <input class="field" name="password" placeholder="Password" type="password" autocomplete="current-password">
          <button class="password-toggle" type="button" data-toggle-password aria-label="Show password">◉</button>
        </label>
        <button class="btn full" type="submit">Sign in</button>
        <a class="btn outline full" href="${pageContext.request.contextPath}/criar-conta">Create new account</a>
        <a class="btn outline full" href="${pageContext.request.contextPath}/home">Sign in with Google</a>
        <a class="text-link" href="${pageContext.request.contextPath}/mudar-senha">Forgot my password</a>
      </form>
    </section>
  </main>
</div>
<script src="${pageContext.request.contextPath}/assets/js/postly.js"></script>
</body>
</html>
