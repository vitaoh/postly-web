<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Change password - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="phone-shell auth-gradient">
  <div class="statusbar">
    <span>2:40</span>
    <span class="status-icons"><span class="signal"></span><span class="wifi"></span><span class="battery"></span></span>
  </div>
  <main class="modal-overlay">
    <section class="dialog-panel">
      <h1>Change password</h1>
      <div class="dialog-icon">▣</div>
      <p class="muted">Enter your current password and choose a new one with at least 6 characters.</p>
      <form class="form-stack" action="${pageContext.request.contextPath}/configuracoes" method="get">
        <label class="password-field">
          <span class="sr-only">Current password</span>
          <input class="field" name="currentPassword" placeholder="Current password" type="password">
          <button class="password-toggle" type="button" data-toggle-password aria-label="Show password">◉</button>
        </label>
        <label class="muted">NEW PASSWORD</label>
        <label class="password-field">
          <span class="sr-only">New password</span>
          <input class="field" name="newPassword" placeholder="New password" type="password">
          <button class="password-toggle" type="button" data-toggle-password aria-label="Show password">◉</button>
        </label>
        <label class="password-field">
          <span class="sr-only">Confirm new password</span>
          <input class="field" name="confirmPassword" placeholder="Confirm new password" type="password">
          <button class="password-toggle" type="button" data-toggle-password aria-label="Show password">◉</button>
        </label>
        <div class="dialog-actions">
          <a class="text-link" href="${pageContext.request.contextPath}/configuracoes">Cancel</a>
          <button class="text-link" type="submit">Confirm</button>
        </div>
      </form>
    </section>
  </main>
</div>
<script src="${pageContext.request.contextPath}/assets/js/postly.js"></script>
</body>
</html>
