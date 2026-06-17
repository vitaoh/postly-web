// Curtida sem recarregar a pagina (estilo Twitter).
// Funciona tambem para posts adicionados pela paginacao, pois escuta no document.
(function () {
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
      .then(function (resposta) { return resposta.json(); })
      .then(function (json) {
        if (!json || !json.ok) {
          return;
        }
        var curtido = json.liked === 'true' || json.liked === true;
        if (botao) {
          botao.classList.toggle('active', curtido);
          var contador = botao.querySelector('.like-count');
          if (contador) {
            contador.textContent = json.likeCount;
          }
          var rotulo = botao.querySelector('.like-label');
          if (rotulo) {
            rotulo.textContent = curtido ? 'Curtido' : 'Curtir';
          }
        }
      })
      .catch(function () {
        // se algo falhar, envia o formulario normalmente
        form.submit();
      });
  });
})();
