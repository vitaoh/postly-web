<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Edit Post - Postly</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css">
</head>
<body>
<div class="phone-shell">
  <div class="statusbar">
    <span>2:38</span>
    <span class="status-icons"><span class="signal"></span><span class="wifi"></span><span class="battery"></span></span>
  </div>
  <main class="sheet-page">
    <section class="sheet">
      <div class="sheet-handle"></div>
      <h1>Edit Post</h1>
      <form action="${pageContext.request.contextPath}/home" method="get">
        <label class="muted" for="description">What's on your mind?</label>
        <textarea class="textarea" id="description" name="description">${postPrincipal.description}</textarea>
        <img class="post-image" src="${pageContext.request.contextPath}/${postPrincipal.image}" alt="Current post media">
        <div class="media-actions">
          <button class="btn outline" type="button">▣ Image</button>
          <button class="btn outline" type="button">⌾ Location</button>
        </div>
        <button class="btn full" type="submit">Save</button>
      </form>
    </section>
  </main>
</div>
</body>
</html>
