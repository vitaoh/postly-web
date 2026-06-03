<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="phone-shell auth-gradient">
  <div class="statusbar">
    <span>2:36</span>
    <span class="status-icons"><span class="signal"></span><span class="wifi"></span><span class="battery"></span></span>
  </div>
  <main class="welcome-content">
    <img class="logo-large" src="${pageContext.request.contextPath}/assets/img/logo.png" alt="Postly">
    <p class="tagline">Share your moments.</p>
    <section class="auth-card">
      <h1>Welcome to Postly</h1>
      <div class="form-stack">
        <a class="btn full" href="${pageContext.request.contextPath}/entrar">Sign in</a>
        <a class="btn outline full" href="${pageContext.request.contextPath}/criar-conta">Create account</a>
      </div>
    </section>
  </main>
</div>
</body>
</html>
