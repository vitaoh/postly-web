<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Nova publicacao - Postly</title>
  <link rel="icon" type="image/png" href="${pageContext.request.contextPath}/assets/img/favicon.png">
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css?v=7">
</head>
<body>
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Criacao</p>
        <h1 class="page-title">Nova publicacao</h1>
        <p class="page-subtitle">Conte o que aconteceu, adicione uma imagem e ajude outras pessoas a encontrarem sua publicacao.</p>
      </div>
      <div class="page-actions">
        <a class="btn outline" href="${pageContext.request.contextPath}/home">&larr; Cancelar</a>
      </div>
    </header>

    <section class="settings-grid">
      <section class="settings-card">
        <form class="form-stack" action="${pageContext.request.contextPath}/postar" method="post" enctype="multipart/form-data">
          <c:if test="${not empty erro}">
            <p class="alert danger">${erro}</p>
          </c:if>
          <label for="description">Descricao</label>
          <textarea class="textarea" id="description" name="description" placeholder="O que voce esta pensando?" required></textarea>
          <label>Imagem
            <input class="field" name="imageFile" type="file" accept="image/*">
          </label>
          <label>Localizacao
            <input class="field" id="locationName" name="locationName" placeholder="Cidade, academia, escola...">
          </label>
          <button class="btn outline" type="button" id="usarLocalizacao">
            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M12 21s-7-6.3-7-11a7 7 0 0 1 14 0c0 4.7-7 11-7 11z"/><circle cx="12" cy="10" r="2.5"/></svg>
            Usar minha localizacao
          </button>
          <p class="muted" id="locationStatus" hidden></p>
          <input type="hidden" name="latitude" id="latitude">
          <input type="hidden" name="longitude" id="longitude">
          <button class="btn" type="submit">Publicar</button>
        </form>
      </section>

      <aside class="content-card">
        <h2 class="section-title">Dicas para publicar</h2>
        <div class="tips-list">
          <p>Use uma descricao clara e direta.</p>
          <p>Adicione uma imagem quando ela ajudar a contar melhor o momento.</p>
          <p>Informe a localizacao apenas quando fizer sentido para a publicacao.</p>
        </div>
      </aside>
    </section>
  </main>
</div>
<script>
  (function () {
    var botao = document.getElementById('usarLocalizacao');
    var status = document.getElementById('locationStatus');
    var campoLat = document.getElementById('latitude');
    var campoLng = document.getElementById('longitude');
    var campoNome = document.getElementById('locationName');
    if (!botao) {
      return;
    }

    function mostrar(texto) {
      status.hidden = false;
      status.textContent = texto;
    }

    botao.addEventListener('click', function () {
      if (!navigator.geolocation) {
        mostrar('Seu navegador nao suporta localizacao.');
        return;
      }
      mostrar('Obtendo sua localizacao...');
      botao.disabled = true;

      navigator.geolocation.getCurrentPosition(function (pos) {
        var lat = pos.coords.latitude;
        var lng = pos.coords.longitude;
        campoLat.value = lat;
        campoLng.value = lng;
        mostrar('Localizacao definida. Buscando o nome do lugar...');

        // descobre o nome da cidade pelas coordenadas (OpenStreetMap, sem chave)
        fetch('https://nominatim.openstreetmap.org/reverse?format=json&lat=' + lat + '&lon=' + lng + '&zoom=14&accept-language=pt-BR')
          .then(function (r) { return r.json(); })
          .then(function (dados) {
            var a = (dados && dados.address) || {};
            var cidade = a.city || a.town || a.village || a.municipality || a.suburb || a.county || '';
            if (cidade && !campoNome.value) {
              campoNome.value = cidade;
            }
            mostrar('Localizacao adicionada' + (cidade ? ': ' + cidade : '') + '.');
          })
          .catch(function () {
            mostrar('Coordenadas adicionadas. Escreva o nome do lugar se quiser.');
          })
          .finally(function () { botao.disabled = false; });
      }, function () {
        mostrar('Nao foi possivel obter a localizacao. Permita o acesso e tente de novo.');
        botao.disabled = false;
      }, { enableHighAccuracy: true, timeout: 10000 });
    });
  })();
</script>
</body>
</html>
