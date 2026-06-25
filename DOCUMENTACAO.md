# 📘 Documentação Completa — Postly Web

Documento técnico de ponta a ponta: visão geral, arquitetura, banco de dados,
conexões, funcionalidades, camadas, segurança e deploy.

---

## 1. Visão geral

O **Postly Web** é uma rede social no estilo Twitter, versão web do aplicativo
**Postly Mobile** (Android/Kotlin). Os dois compartilham **o mesmo banco de dados
Firebase Firestore**, então publicações, perfis, curtidas, comentários e conversas
(incluindo fotos e áudios) sincronizam entre a web e o celular em tempo real.

É uma aplicação **Java Web (Jakarta EE)** baseada em **Servlet + JSP**, empacotada
como **WAR** e executada no **Apache Tomcat 10.1.55**.

- **Linguagem:** Java 17
- **Padrão de projeto:** MVC em camadas (View → Controller → Service → DAO → Modelo)
- **Banco:** Cloud Firestore (NoSQL, na nuvem)
- **Autenticação:** Firebase Authentication (e-mail/senha e Google)
- **Front-end:** JSP + JSTL + CSS puro + JavaScript (sem frameworks)

---

## 2. Stack e tecnologias

| Camada | Tecnologia |
|---|---|
| Linguagem | Java 17 |
| API web | Jakarta Servlet 6.0, JSP 3.1, JSTL 3.0 |
| Servidor | Apache Tomcat 10.1.55 |
| Build | Maven (empacotamento `war`) |
| Banco de dados | Google Cloud Firestore |
| Autenticação | Firebase Authentication |
| SDK servidor | Firebase Admin SDK 9.9.0 |
| Front-end | JSP, JSTL, CSS3 (variáveis, tema claro/escuro), JavaScript puro |
| Fonte | Plus Jakarta Sans (Google Fonts) |

**Dependências (pom.xml):**
- `jakarta.servlet-api` (provided — fornecido pelo Tomcat)
- `jakarta.servlet.jsp-api` (provided)
- `jakarta.servlet.jsp.jstl-api` + `jakarta.servlet.jsp.jstl` (JSTL)
- `firebase-admin` 9.9.0 (Firestore + Auth no servidor)
- `junit` (testes)

---

## 3. Arquitetura em camadas (MVC)

```
┌──────────────────────────────────────────────────────────┐
│  VIEW            JSP + JSTL  (webapp/WEB-INF/views)        │
│  - renderiza HTML, recebe dados via request attributes     │
├──────────────────────────────────────────────────────────┤
│  CONTROLLER      PostlyPageController (1 Servlet)          │
│  - recebe GET/POST, valida sessão, orquestra as camadas    │
├──────────────────────────────────────────────────────────┤
│  SERVICE         regras de negócio                         │
│  - PostlyPostService, PostlyChatService, etc.              │
├──────────────────────────────────────────────────────────┤
│  DAO             persistência (acesso ao Firestore)        │
│  - FirebasePostDAO, FirebaseUsuarioDAO, etc.              │
├──────────────────────────────────────────────────────────┤
│  MODELO          entidades (POJOs)                         │
│  - Post, Usuario, Comentario, ChatThread, ChatMessage      │
└──────────────────────────────────────────────────────────┘
                          │
                          ▼
                  ☁️ Cloud Firestore
```

**Princípio da Separação de Interesses (SoC):** cada camada tem uma única
responsabilidade. A View não acessa o banco; o Controller não tem regra de negócio
pesada; o Service concentra as regras; o DAO isola o Firestore; o Modelo só carrega
dados. Trocar o banco, por exemplo, afetaria apenas a camada DAO.

---

## 4. Estrutura de pastas

```
postly-web/
├── pom.xml                         # configuração Maven
├── README.md
├── DOCUMENTACAO.md                 # este arquivo
├── private/                        # chave do Firebase (alternativa; fora do git)
└── src/main/
    ├── java/com/victor/postlyweb/
    │   ├── config/
    │   │   └── FirebaseConfig.java          # conexão única com o Firestore
    │   ├── controllers/
    │   │   └── PostlyPageController.java     # o Servlet (todas as rotas)
    │   ├── modelo/
    │   │   ├── Usuario.java
    │   │   ├── Post.java
    │   │   ├── Comentario.java
    │   │   ├── ChatThread.java               # conversa
    │   │   └── ChatMessage.java              # mensagem
    │   ├── persistencia/firebase/
    │   │   ├── FirebaseUsuarioDAO.java
    │   │   ├── FirebasePostDAO.java
    │   │   ├── FirebaseComentarioDAO.java
    │   │   └── FirebaseChatDAO.java
    │   └── service/
    │       ├── PostlyAuthService.java        # verifica token Firebase
    │       ├── PostlyUsuarioService.java
    │       ├── PostlyPostService.java
    │       ├── PostlyComentarioService.java
    │       ├── PostlyChatService.java
    │       ├── ImagemBase64Service.java      # processa imagens
    │       └── TempoService.java             # formata datas/tempo
    └── webapp/
        ├── index.jsp                         # redireciona para /welcome
        ├── assets/
        │   ├── css/postly.css                # todo o estilo (tema claro/escuro)
        │   ├── js/postly-auth.js             # login/cadastro via Firebase JS
        │   ├── js/postly-like.js             # curtida via AJAX
        │   ├── js/postly.js                  # utilidades de auth
        │   └── img/  (logo.png, favicon.png, avatar-default.svg)
        └── WEB-INF/
            ├── web.xml                       # servlet, rotas, multipart
            └── views/                        # as telas JSP
                ├── welcome.jsp  entrar.jsp  criar-conta.jsp  mudar-senha.jsp
                ├── home.jsp  postar.jsp  editar-post.jsp  post.jsp
                ├── perfil.jsp  configuracoes.jsp
                ├── mensagens.jsp  chat.jsp
                └── fragments/sidebar.jspf    # menu lateral reutilizável
```

---

## 5. Banco de dados — Cloud Firestore (NoSQL)

O Firestore organiza dados em **coleções** que contêm **documentos** (cada documento
é um conjunto de campos chave-valor), e documentos podem ter **subcoleções**.

### Coleções e estrutura

```
users (coleção)
 └── {uid} (documento)
      ├── uid: string
      ├── name: string
      ├── username: string (único, minúsculo)
      ├── email: string (único, minúsculo)
      ├── photo: string (imagem em Base64 ou caminho de asset)
      ├── following (subcoleção) → {uidAlvo}: { userId, createdAt }
      └── followers (subcoleção) → {uidSeguidor}: { userId, createdAt }

posts (coleção)
 └── {postId} (documento)
      ├── id: string
      ├── userId: string (autor)
      ├── description: string
      ├── image: string (Base64 da foto, opcional)
      ├── timestamp: long (epoch millis)
      ├── latitude / longitude: double (opcional)
      ├── locationName: string (cidade, opcional)
      ├── likeCount: int
      ├── likedBy: array<string> (uids que curtiram)
      ├── commentCount: int
      └── comments (subcoleção)
           └── {commentId}: { id, postId, userId, text, timestamp }

chats (coleção)
 └── {chatId}  (id = "uidA_uidB" ordenado alfabeticamente)
      ├── id: string
      ├── participants: array<string> (os 2 uids)
      ├── lastMessage / lastSenderId / lastTimestamp
      ├── createdAt / updatedAt
      └── messages (subcoleção)
           └── {messageId}: { id, chatId, senderId, text, timestamp,
                              type, mediaBase64, mediaMimeType }
```

### Observações importantes do modelo

- **ID da conversa determinístico:** `chatId = ordena(uidA, uidB).join("_")`. Assim
  os dois usuários sempre caem na mesma conversa, sem duplicar.
- **Imagens em Base64:** as fotos (post, perfil, chat) são guardadas como **texto
  Base64 dentro do documento** — não em arquivos. É assim que o app mobile também faz,
  por isso a compatibilidade. O Firestore limita cada documento a **1 MiB**, por isso
  as imagens são reduzidas antes de salvar (ver seção 13).
- **Tipos de mensagem:** `type` = `"text"`, `"image"` ou `"audio"`. Para mídia, os
  bytes vão em `mediaBase64` e o formato em `mediaMimeType`.

---

## 6. Conexão com o Firebase

Centralizada em **`FirebaseConfig.java`** — um **singleton thread-safe**:

- Inicializa o `FirebaseApp` **uma única vez** (double-checked locking) e reutiliza a
  mesma conexão gRPC do Firestore em todas as requisições. É o equivalente, no mundo
  NoSQL, a um "pool de conexões" — evita reabrir conexão a cada acesso.
- A credencial (chave de serviço, `serviceAccountKey.json`) é procurada nesta ordem:
  1. Propriedade do sistema `firebase.serviceAccountKey` (caminho absoluto)
  2. Variável de ambiente `GOOGLE_APPLICATION_CREDENTIALS`
  3. **Classpath** (`src/main/resources/serviceAccountKey.json`) ← método principal,
     funciona em qualquer máquina sem configurar nada
  4. Pasta `private/` do projeto (fallback)
- Usa o **Firebase Admin SDK**, que tem **acesso administrativo total** ao Firestore e
  **ignora as regras de segurança** do banco — toda autorização é feita no servidor.

> A chave é secreta e **não vai para o GitHub** (está no `.gitignore`).

---

## 7. Autenticação e sessão

A autenticação é **híbrida** (parte no navegador, parte no servidor):

1. **No navegador** (`postly-auth.js`): o usuário faz login/cadastro com o **Firebase
   Authentication JS** (e-mail/senha ou Google). O Firebase devolve um **ID Token**.
2. **No servidor** (`/auth/session`, `/auth/register`, `/auth/google-complete`): o
   token é enviado e **verificado** por `PostlyAuthService.verificarToken()` usando o
   Admin SDK. Confirmado o token, o servidor cria a **sessão HTTP** (`HttpSession`)
   guardando o `uid` (`postly.uid`).
3. **Rotas protegidas:** antes de servir `/home`, `/perfil`, `/chat` etc., o Controller
   checa se há `uid` na sessão; se não houver, redireciona para `/entrar`.
4. **Logout** (`/auth/logout`): invalida a sessão e volta para `/welcome`.

Endpoints auxiliares de cadastro (respondem JSON):
- `/auth/check-username` — verifica se o usuário está disponível
- `/auth/check-account` — valida usuário + e-mail antes de criar
- `/auth/resolve-username` — descobre o e-mail a partir do @usuário (login por usuário)

---

## 8. Rotas (mapeadas no web.xml → PostlyPageController)

**Páginas (GET) / Ações (POST):**

| Rota | GET | POST |
|---|---|---|
| `/welcome` | tela inicial pública | — |
| `/entrar` | tela de login | — |
| `/criar-conta` | tela de cadastro | — |
| `/mudar-senha` | tela de troca de senha | aviso (troca é via Firebase) |
| `/home` | feed (paginado, com busca/filtro) | — |
| `/postar` | formulário de nova publicação | cria post (com foto/localização) |
| `/editar-post` | formulário de edição | atualiza o post |
| `/post` | detalhe do post + comentários | curtir, comentar, excluir post/comentário |
| `/perfil` | perfil + publicações | seguir/deixar de seguir |
| `/configuracoes` | editar perfil | salva nome/usuário/foto |
| `/mensagens` | lista de conversas | — |
| `/chat` | conversa aberta | envia texto/foto/áudio |
| `/auth/*` | (redireciona) | endpoints de autenticação (JSON) |

O `POST /post` usa um parâmetro `action` para diferenciar: `like`, `comment`,
`delete-comment`, `delete-post`.

---

## 9. Funcionalidades (detalhado)

### 9.1 Feed (home)
- Lista publicações ordenadas da mais recente para a mais antiga.
- **Dois filtros:** "Para você" (todos) e "Seguindo" (só de quem você segue).
- **Busca** por descrição, cidade, nome ou @usuário — **sem distinção de acento**
  ("sao" encontra "São Carlos") e em **todas** as publicações (não só na página atual).

### 9.2 Paginação estilo Twitter (por cursor)
- Carrega 5 posts por vez. O botão **"Carregar mais publicações"** busca a próxima
  página via `fetch` e **anexa** os posts sem recarregar (sensação de scroll infinito).
- Usa **cursor por timestamp** (`startAfter`): cada página continua exatamente de onde
  a anterior parou. Durante a busca, a paginação é desativada (procura em tudo).

### 9.3 Publicações (CRUD)
- **Criar:** descrição (obrigatória) + foto (opcional) + localização (opcional).
- **Editar:** só o autor; mantém curtidas/comentários/data originais.
- **Excluir:** só o autor.
- **Localização:** botão "Usar minha localização" pega o GPS do navegador e descobre
  a **cidade** automaticamente (via OpenStreetMap/Nominatim, sem chave). A cidade
  aparece como um selo nos posts publicados.
- **Tempo:** cada post mostra há quanto tempo foi feito ("agora", "5min", "3h", "2d").

### 9.4 Curtidas
- Botão coração com estado visual: contorno = não curtido, **vermelho preenchido = curtido**.
- Funciona via **AJAX**: curtir atualiza o coração e a contagem **no lugar, sem
  recarregar** a página nem perder a posição do scroll (mesmo em posts paginados).

### 9.5 Comentários
- Comentar, listar e excluir (autor do comentário ou dono do post podem excluir).
- Cada comentário mostra autor, @usuário e há quanto tempo foi feito.
- O contador de comentários do post é mantido atualizado.

### 9.6 Perfil e seguir
- Mostra foto, nome, @usuário, estatísticas (publicações, seguidores, seguindo) e os
  posts do usuário.
- **Seguir / deixar de seguir** outros usuários (atualiza as subcoleções
  `following`/`followers` de ambos atomicamente).
- No próprio perfil aparecem os botões **Editar** e **Excluir** em cada post.

### 9.7 Mensagens / Chat
- Lista de conversas com foto, nome, **prévia da última mensagem** ("Você: ..." ou
  "Fulano: ...") e horário. O card inteiro é clicável.
- Conversa em balões estilo app (os seus à direita, em roxo).
- Envio de **texto**, **foto** (botão de câmera) e **áudio** (botão de microfone, que
  grava pelo navegador com o `MediaRecorder` e envia ao soltar; limite de 60s).
- Fotos aparecem dentro do balão; áudios viram um player; o chat já abre rolado até a
  última mensagem.

### 9.8 Configurações
- Editar nome, @usuário e foto de perfil (com unicidade de usuário garantida).
- Trocar senha (fluxo do Firebase) e sair da conta.

---

## 10. Camada de Serviço (regras de negócio)

| Service | Responsabilidade |
|---|---|
| `PostlyAuthService` | Verifica o ID Token do Firebase (Admin SDK) |
| `PostlyUsuarioService` | Salvar usuário com **usuário/e-mail únicos**, buscar, seguir/deixar de seguir |
| `PostlyPostService` | Criar, editar (só autor), excluir (só autor), **alternar curtida**, buscar post |
| `PostlyComentarioService` | Adicionar e excluir comentário (com checagem de permissão) |
| `PostlyChatService` | Enviar mensagem/mídia validando se você participa da conversa |
| `ImagemBase64Service` | Converter/reduzir imagem para Base64 (ver seção 13) |
| `TempoService` | Formatar datas e tempo relativo para as telas |

As validações de regra (ex.: "só o autor edita", "usuário único", "mínimo 3
caracteres") ficam **aqui**, não na View nem no DAO.

---

## 11. Camada de Persistência (DAO)

Cada DAO isola o acesso ao Firestore de uma coleção:

| DAO | Coleção | Destaques |
|---|---|---|
| `FirebaseUsuarioDAO` | `users` | salvar, buscar por uid/username/email, seguir (WriteBatch atômico), contar seguidores/seguindo |
| `FirebasePostDAO` | `posts` | adicionar, atualizar, excluir, **feed paginado por cursor**, listar por usuário, **curtida em transação** |
| `FirebaseComentarioDAO` | `posts/{id}/comments` | adicionar/excluir comentário e manter o `commentCount` |
| `FirebaseChatDAO` | `chats` | criar/buscar conversa, listar conversas, enviar texto e mídia (WriteBatch) |

---

## 12. Gerenciamento de transações

O projeto usa as garantias de atomicidade do Firestore:

- **Curtida** — `runTransaction`: lê o post e atualiza `likeCount` + `likedBy` de forma
  atômica, evitando contagem errada em cliques simultâneos.
- **Mensagem do chat** — `WriteBatch`: grava a mensagem na subcoleção **e** atualiza os
  metadados da conversa (`lastMessage`, `lastTimestamp`...) numa única operação — ou
  tudo grava, ou nada.
- **Seguir/deixar de seguir** — `WriteBatch`: atualiza as duas pontas
  (`following` de um, `followers` do outro) juntas.

---

## 13. Processamento e upload de imagens

Fluxo único usado por post, foto de perfil e foto do chat (`ImagemBase64Service`):

1. **Upload multipart** — configurado no `web.xml`: até **20 MB por arquivo**
   (`max-file-size`) e **21 MB por requisição**, com *threshold* alto que mantém o
   upload **em memória** (não depende de pasta temporária do SO).
2. **Leitura** — os bytes são lidos do `part.getInputStream()` (sem caminho de arquivo).
3. **Redimensionamento** — a imagem é reduzida para no máximo **800px** e recomprimida
   como **JPEG (qualidade 0.70)** → tipicamente ~100 KB, bem abaixo do limite do
   Firestore. Vira **Base64** e é salva no documento.
4. **Modo robusto para Linux:** no início da classe é forçado
   `java.awt.headless=true` e `ImageIO.setUseCache(false)` — o servidor Linux não tem
   interface gráfica e poderia falhar ao processar imagem; isso evita o erro.
5. **Plano B:** se o redimensionamento falhar por qualquer motivo, a imagem original é
   salva mesmo assim (limitada a **500 KB** para o Base64 caber no limite de 1 MiB do
   documento). Garante que o upload funcione em qualquer ambiente.

Para **áudio** do chat, os bytes vão em Base64 bruto (limite ~700 KB), preservando o
formato gravado pelo navegador (webm/opus ou mp4), compatível com o player e o mobile.

---

## 14. Front-end

- **JSP + JSTL:** as telas usam EL (`${...}`) e tags `<c:if>`, `<c:forEach>` etc. para
  renderizar os dados que o Controller coloca como *request attributes*. Não há lógica
  de negócio nas telas.
- **Fragmento reutilizável:** `fragments/sidebar.jspf` (menu lateral) é incluído em
  todas as páginas internas.
- **CSS (`postly.css`):** estilo próprio com **variáveis CSS**, **tema claro e escuro
  automáticos** (segue o sistema), paleta roxa (`#9539CB`), layout responsivo,
  componentes (cards, chips, balões de chat, botões em pílula).
- **JavaScript (sem framework):**
  - `postly-auth.js` / `postly.js` — login e cadastro com o Firebase JS.
  - `postly-like.js` — curtida via AJAX (atualiza no lugar, nunca recarrega).
  - scripts inline — paginação "carregar mais", gravação de áudio, geolocalização.
- **Favicon e logo** do app, com cantos arredondados.

---

## 15. Conceitos acadêmicos atendidos

| Conceito | Como é aplicado |
|---|---|
| Separação de Interesses (SoC) | Camadas View/Controller/Service/DAO/Modelo bem divididas |
| Arquitetura em camadas | Fluxo unidirecional entre as 5 camadas |
| Persistência em banco | Cloud Firestore via Firebase Admin SDK |
| "Pool"/reuso de conexão | `FirebaseConfig` singleton reaproveita a conexão gRPC |
| Gerenciamento de transações | `runTransaction` (curtida) e `WriteBatch` (chat, seguir) |
| Paginação | Cursor por `timestamp` com `startAfter` + "carregar mais" |
| Upload de arquivos | Multipart + redimensionamento/compressão + Base64 |

---

## 16. Integração com o app mobile

Web e mobile usam **o mesmo projeto Firebase e as mesmas coleções**. Os modelos batem
campo a campo (inclusive `type`/`mediaBase64`/`mediaMimeType` das mensagens e o
`image` em Base64 dos posts), por isso o que é criado em um aparece no outro: posts,
curtidas, comentários, perfis e conversas com foto e áudio.

---

## 17. Como rodar / deploy

**Pré-requisitos:** JDK 17+, Apache **Tomcat 10.1.55** (precisa ser 10+, pois usa
`jakarta.*`) e Maven (ou Eclipse).

1. `git clone` do repositório.
2. Colocar a chave do Firebase em `src/main/resources/serviceAccountKey.json`
   (não vem no clone — é secreta).
3. **Eclipse:** Import → Maven → Existing Maven Projects; criar servidor Tomcat 10.1.55;
   `Run As → Run on Server`.
   **Linha de comando:** `mvn clean package` e copiar o `.war` para `webapps/` do Tomcat.
4. Acessar `http://localhost:8080/postly-web/`.

---

## 18. Segurança — pontos de atenção

- A **chave de serviço do Firebase** dá acesso administrativo total; por isso fica fora
  do git (`.gitignore`) e nunca deve ser publicada. Se vazar, gerar uma nova no Console
  do Firebase.
- Toda **autorização** é feita no servidor (verificação de token + checagens de autor
  nas regras de negócio), já que o Admin SDK ignora as regras do Firestore.
- O encoding das requisições é **UTF-8** (definido no `doPost`), garantindo acentos
  corretos independentemente do sistema operacional.
- Saídas JSON são escapadas para evitar quebra de formato.

---

*Documento gerado para o projeto Postly Web — rede social Java Web + Firebase Firestore.*
