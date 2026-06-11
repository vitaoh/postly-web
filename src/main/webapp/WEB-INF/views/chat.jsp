<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!doctype html>
<html lang="pt-BR">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Conversa - Postly</title>
  <link rel="preconnect" href="https://fonts.googleapis.com">
  <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
  <link rel="stylesheet" href="https://fonts.googleapis.com/css2?family=Plus+Jakarta+Sans:wght@400;500;600;700;800&display=swap">
  <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/postly.css?v=5">
</head>
<body>
<div class="app-layout">
  <%@ include file="fragments/sidebar.jspf" %>

  <main class="main-panel">
    <header class="page-header">
      <div>
        <p class="page-kicker">Conversa</p>
        <h1 class="page-title"><a href="${pageContext.request.contextPath}/perfil?uid=${outroUsuario.uid}">${outroUsuario.name}</a></h1>
        <p class="page-subtitle"><a href="${pageContext.request.contextPath}/perfil?uid=${outroUsuario.uid}">@${outroUsuario.username}</a></p>
      </div>
      <div class="page-actions">
        <a class="btn outline" href="${pageContext.request.contextPath}/mensagens">&larr; Conversas</a>
      </div>
    </header>
    <c:if test="${not empty erro}">
      <p class="alert danger">${erro}</p>
    </c:if>
    <c:if test="${not empty mensagem}">
      <p class="alert success">${mensagem}</p>
    </c:if>

    <section class="messages-shell">
      <section class="content-card">
        <h2 class="section-title">Conversas</h2>
        <div class="messages-list">
          <c:forEach var="conversa" items="${conversas}">
            <c:set var="conversaUsuario" value="${usuariosPorConversa[conversa.id]}" />
            <c:set var="conversaNome" value="${empty conversaUsuario.name ? 'Usuario' : conversaUsuario.name}" />
            <c:set var="conversaUsername" value="${empty conversaUsuario.username ? 'usuario' : conversaUsuario.username}" />
            <c:set var="conversaHorario" value="${tempoService.dataHoraConversa(conversa)}" />
            <a class="conversation ${conversa.id == chatAtual.id ? 'active' : ''}" href="${pageContext.request.contextPath}/chat?chatId=${conversa.id}">
              <img class="avatar" src="${imagemService.src(pageContext.request.contextPath, conversaUsuario.photo)}" alt="${conversaNome}">
              <span>
                <strong>${conversaNome}</strong>
                <small class="muted">@${conversaUsername}</small>
                <p class="conversation-preview">
                  <c:choose>
                    <c:when test="${empty conversa.lastMessage}">Sem mensagens ainda</c:when>
                    <c:when test="${conversa.lastSenderId == usuario.uid}">Voce: ${conversa.lastMessage}</c:when>
                    <c:otherwise>${conversaNome}: ${conversa.lastMessage}</c:otherwise>
                  </c:choose>
                </p>
              </span>
              <time class="conversation-time">${conversaHorario}</time>
            </a>
          </c:forEach>
          <c:if test="${empty conversas}">
            <p class="empty-state">Nenhuma conversa encontrada.</p>
          </c:if>
        </div>
      </section>

      <section>
        <div class="message-thread">
          <c:if test="${empty mensagens}">
            <p class="empty-state">Nenhuma mensagem ainda. Diga um oi para @${outroUsuario.username}!</p>
          </c:if>
          <c:forEach var="mensagem" items="${mensagens}">
            <article class="message-bubble ${mensagem.senderId == usuario.uid ? 'mine' : ''} ${mensagem.imagem ? 'media' : ''}">
              <c:if test="${mensagem.imagem}">
                <img class="message-image" src="${mensagem.mediaDataUri}" alt="Foto enviada na conversa" loading="lazy">
              </c:if>
              <c:if test="${mensagem.audio}">
                <audio class="message-audio" controls preload="none" src="${mensagem.mediaDataUri}"></audio>
              </c:if>
              <c:if test="${not empty mensagem.text and not mensagem.imagem and not mensagem.audio}">
                <p>${mensagem.text}</p>
              </c:if>
              <time>${tempoService.dataHora(mensagem.timestamp)}</time>
            </article>
          </c:forEach>
        </div>
        <form class="composer-bar chat-composer" id="chatComposer" action="${pageContext.request.contextPath}/chat" method="post" enctype="multipart/form-data">
          <input type="hidden" name="chatId" value="${chatAtual.id}">
          <label class="composer-icon" title="Enviar foto">
            <input type="file" name="photoFile" id="chatPhoto" accept="image/*" hidden>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M4 7h3l2-2.5h6L17 7h3a1.5 1.5 0 0 1 1.5 1.5V18A1.5 1.5 0 0 1 20 19.5H4A1.5 1.5 0 0 1 2.5 18V8.5A1.5 1.5 0 0 1 4 7z"/><circle cx="12" cy="13" r="3.5"/></svg>
          </label>
          <button class="composer-icon" type="button" id="chatMic" title="Gravar audio">
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="9" y="3" width="6" height="11" rx="3"/><path d="M5 11a7 7 0 0 0 14 0M12 18v3"/></svg>
          </button>
          <input type="file" name="audioFile" id="chatAudio" hidden>
          <input class="field" name="message" id="chatMessage" placeholder="Escreva uma mensagem" required>
          <button class="btn" type="submit">Enviar</button>
        </form>
      </section>
    </section>
  </main>
</div>
<script>
  (function () {
    var thread = document.querySelector('.message-thread');
    if (thread) {
      thread.scrollTop = thread.scrollHeight;
    }

    var composer = document.getElementById('chatComposer');
    var photoInput = document.getElementById('chatPhoto');
    var audioInput = document.getElementById('chatAudio');
    var micBtn = document.getElementById('chatMic');
    if (!composer || !photoInput || !audioInput || !micBtn) {
      return;
    }

    photoInput.addEventListener('change', function () {
      if (photoInput.files.length > 0) {
        composer.submit();
      }
    });

    var mediaRecorder = null;
    var chunks = [];
    var autoStop = null;

    function pararStream(stream) {
      stream.getTracks().forEach(function (track) { track.stop(); });
    }

    micBtn.addEventListener('click', function () {
      if (mediaRecorder && mediaRecorder.state === 'recording') {
        mediaRecorder.stop();
        return;
      }
      if (!navigator.mediaDevices || !window.MediaRecorder) {
        alert('Este navegador nao suporta gravacao de audio.');
        return;
      }

      navigator.mediaDevices.getUserMedia({ audio: true }).then(function (stream) {
        var mime = ['audio/webm;codecs=opus', 'audio/webm', 'audio/mp4'].find(function (tipo) {
          return MediaRecorder.isTypeSupported(tipo);
        }) || '';

        mediaRecorder = mime ? new MediaRecorder(stream, { mimeType: mime }) : new MediaRecorder(stream);
        chunks = [];

        mediaRecorder.ondataavailable = function (evento) {
          if (evento.data && evento.data.size > 0) {
            chunks.push(evento.data);
          }
        };

        mediaRecorder.onstop = function () {
          pararStream(stream);
          clearTimeout(autoStop);
          micBtn.classList.remove('recording');
          micBtn.title = 'Gravar audio';

          var tipo = mediaRecorder.mimeType || 'audio/webm';
          var blob = new Blob(chunks, { type: tipo });
          if (blob.size === 0) {
            return;
          }
          var extensao = tipo.indexOf('mp4') >= 0 ? 'm4a' : 'webm';
          var arquivo = new File([blob], 'audio.' + extensao, { type: tipo });
          var transfer = new DataTransfer();
          transfer.items.add(arquivo);
          audioInput.files = transfer.files;
          composer.submit();
        };

        mediaRecorder.start();
        micBtn.classList.add('recording');
        micBtn.title = 'Parar e enviar';
        // limite de 60s para caber no documento do Firestore
        autoStop = setTimeout(function () {
          if (mediaRecorder.state === 'recording') {
            mediaRecorder.stop();
          }
        }, 60000);
      }).catch(function () {
        alert('Permita o acesso ao microfone para gravar audio.');
      });
    });
  })();
</script>
</body>
</html>
