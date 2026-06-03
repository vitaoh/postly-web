<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Create account - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="phone-shell auth-gradient">
  <div class="statusbar">
    <span>2:37</span>
    <span class="status-icons"><span class="signal"></span><span class="wifi"></span><span class="battery"></span></span>
  </div>
  <main class="auth-page">
    <img class="logo-mark" src="${pageContext.request.contextPath}/assets/img/logo.png" alt="Postly">
    <section class="auth-card">
      <h1>Create account</h1>
      <form class="form-stack" action="${pageContext.request.contextPath}/home" method="get">
        <input class="field" name="username" placeholder="Username" autocomplete="username">
        <input class="field" name="name" placeholder="Name" autocomplete="name">
        <input class="field" name="email" placeholder="E-mail" type="email" autocomplete="email">
        <label class="password-field">
          <span class="sr-only">Password</span>
          <input class="field" name="password" placeholder="Password" type="password" autocomplete="new-password">
          <button class="password-toggle" type="button" data-toggle-password aria-label="Show password">◉</button>
        </label>
        <label class="password-field">
          <span class="sr-only">Confirm password</span>
          <input class="field" name="confirmPassword" placeholder="Confirm password" type="password" autocomplete="new-password">
          <button class="password-toggle" type="button" data-toggle-password aria-label="Show password">◉</button>
        </label>
        <button class="btn full" type="submit">Create account</button>
        <a class="text-link" href="${pageContext.request.contextPath}/entrar">Back to login</a>
      </form>
    </section>
  </main>
</div>
<script src="${pageContext.request.contextPath}/assets/js/postly.js"></script>
</body>
</html>
