// Curtida sem recarregar a pagina (estilo Twitter).
// Usa delegacao no document, entao funciona tambem nos posts adicionados pela paginacao.
// NUNCA navega/recarrega: assim nunca "pula para o topo", mesmo em posts carregados depois.
(function () {

  // atualiza o botao com base no estado novo (usado quando o servidor nao devolve JSON)
  function alternarVisual(botao) {
    if (!botao) {
      return;
    }
    var curtido = !botao.classList.contains('active');
    aplicarVisual(botao, curtido, null);
  }

  function aplicarVisual(botao, curtido, contagem) {
    botao.classList.toggle('active', curtido);

    var contador = botao.querySelector('.like-count');
    if (contador) {
      if (contagem !== null && contagem !== undefined) {
        contador.textContent = contagem;
      } else {
        var n = parseInt(contador.textContent, 10) || 0;
        contador.textContent = curtido ? n + 1 : Math.max(0, n - 1);
      }
    }

    var rotulo = botao.querySelector('.like-label');
    if (rotulo) {
      rotulo.textContent = curtido ? 'Curtido' : 'Curtir';
    }
  }

  document.addEventListener('submit', function (evento) {
    var form = evento.target;
    if (!form.classList || !form.classList.contains('like-form')) {
      return;
    }
    evento.preventDefault();

    var botao = form.querySelector('.like-chip');
    var dados = new URLSearchParams(new FormData(form));

    fetch(form.action, {
      method: 'POST',
      headers: { 'X-Requested-With': 'fetch' },
      body: dados
    })
      .then(function (resposta) {
        var tipo = resposta.headers.get('content-type') || '';
        // servidor atualizado responde JSON; build antigo redireciona (HTML) -> trata no else
        return tipo.indexOf('application/json') >= 0 ? resposta.json() : null;
      })
      .then(function (json) {
        if (json && json.ok) {
          var curtido = json.liked === 'true' || json.liked === true;
          aplicarVisual(botao, curtido, json.likeCount);
        } else {
          // o servidor processou a curtida mesmo sem devolver JSON: so atualiza o visual
          alternarVisual(botao);
        }
      })
      .catch(function () {
        alternarVisual(botao);
      });
  });
})();
