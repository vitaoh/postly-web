package com.victor.postlyweb.controllers;

import com.google.firebase.auth.FirebaseToken;
import com.victor.postlyweb.modelo.ChatMessage;
import com.victor.postlyweb.modelo.ChatThread;
import com.victor.postlyweb.modelo.Comentario;
import com.victor.postlyweb.modelo.Post;
import com.victor.postlyweb.modelo.Usuario;
import com.victor.postlyweb.persistencia.firebase.FirebaseChatDAO;
import com.victor.postlyweb.persistencia.firebase.FirebaseComentarioDAO;
import com.victor.postlyweb.persistencia.firebase.FirebasePostDAO;
import com.victor.postlyweb.persistencia.firebase.FirebaseUsuarioDAO;
import com.victor.postlyweb.service.ImagemBase64Service;
import com.victor.postlyweb.service.PostlyAuthService;
import com.victor.postlyweb.service.PostlyChatService;
import com.victor.postlyweb.service.PostlyComentarioService;
import com.victor.postlyweb.service.PostlyDemoService;
import com.victor.postlyweb.service.PostlyPostService;
import com.victor.postlyweb.service.PostlyUsuarioService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class PostlyPageController extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String SESSION_UID = "postly.uid";
    private static final String FLASH_ERRO = "flash.erro";
    private static final String FLASH_MENSAGEM = "flash.mensagem";

    private final PostlyDemoService demoService = new PostlyDemoService();
    private final ImagemBase64Service imagemService = new ImagemBase64Service();
    private final PostlyAuthService authService = new PostlyAuthService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if (path.startsWith("/auth")) {
            response.sendRedirect(request.getContextPath() + "/entrar");
            return;
        }

        if (rotaProtegida(path) && estaVazio(usuarioAutenticadoUid(request))) {
            adicionarFlash(request, FLASH_ERRO, "Entre na conta para acessar o Postly Web.");
            response.sendRedirect(request.getContextPath() + "/entrar");
            return;
        }

        carregarFlash(request);
        if (!carregarDadosComuns(request, !rotaProtegida(path))) {
            response.sendRedirect(request.getContextPath() + "/entrar");
            return;
        }
        request.getRequestDispatcher(viewPara(path)).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        String path = request.getServletPath();

        try {
            switch (path) {
                case "/auth/resolve-username" -> resolverUsername(request, response);
                case "/auth/check-username" -> verificarUsername(request, response);
                case "/auth/session" -> abrirSessao(request, response);
                case "/auth/register" -> registrarUsuario(request, response);
                case "/auth/google-complete" -> completarUsuarioGoogle(request, response);
                case "/auth/logout" -> sair(request, response);
                case "/postar" -> criarPost(request, response);
                case "/editar-post" -> editarPost(request, response);
                case "/post" -> acaoPost(request, response);
                case "/chat" -> enviarMensagem(request, response);
                case "/configuracoes" -> salvarPerfil(request, response);
                case "/mudar-senha" -> senhaSemJavascript(request, response);
                default -> {
                    response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    response.getWriter().write("Rota nao encontrada.");
                }
            }
        } catch (Exception exception) {
            tratarErroPost(request, response, path, exception);
        }
    }

    private boolean carregarDadosComuns(HttpServletRequest request, boolean permitirDemo) {
        request.setAttribute("imagemService", imagemService);
        try {
            carregarDadosFirebase(request);
            return true;
        } catch (Exception exception) {
            if (!permitirDemo) {
                adicionarFlash(request, FLASH_ERRO, "Nao foi possivel carregar seus dados do Firestore: "
                        + mensagemCurta(exception));
                return false;
            }
            carregarDadosDemo(request, "Modo demo: " + mensagemCurta(exception));
            return true;
        }
    }

    private void carregarDadosFirebase(HttpServletRequest request) throws Exception {
        FirebaseUsuarioDAO usuarioDAO = new FirebaseUsuarioDAO();
        FirebasePostDAO postDAO = new FirebasePostDAO();
        FirebaseComentarioDAO comentarioDAO = new FirebaseComentarioDAO();
        FirebaseChatDAO chatDAO = new FirebaseChatDAO();

        String uidSessao = usuarioAutenticadoUid(request);
        List<Usuario> usuarios = usuarioDAO.listarPrimeiros(30);
        Usuario usuarioAtual = usuarioAtual(usuarioDAO, usuarios, uidSessao);

        String busca = param(request, "busca");
        String feedAtivo = feedAtivo(request);
        List<Post> posts = carregarPostsDaTela(request, usuarioDAO, postDAO, usuarioAtual.getUid(), feedAtivo);

        List<ChatThread> conversas = estaVazio(uidSessao) ? List.of() : chatDAO.listarConversas(uidSessao);
        ChatThread chatAtual = chatAtual(chatDAO, conversas, param(request, "chatId"), uidSessao);
        List<ChatMessage> mensagens = chatAtual == null ? List.of() : chatDAO.listarMensagens(chatAtual.getId());
        Usuario outroUsuario = outroUsuario(usuarioDAO, usuarios, usuarioAtual, conversas, chatAtual, param(request, "otherUid"));

        Map<String, Usuario> usuariosPorUid = usuariosPorUid(usuarioDAO, usuarios, usuarioAtual, outroUsuario, posts, List.of());
        posts = filtrarPosts(posts, busca, usuariosPorUid);

        String postId = param(request, "id");
        Post postPrincipal = postPrincipal(postDAO, posts, postId);
        List<Comentario> comentarios = postPrincipal == null
                ? List.of()
                : comentarioDAO.listarPorPost(postPrincipal.getId());
        usuariosPorUid = usuariosPorUid(usuarioDAO, usuarios, usuarioAtual, outroUsuario, posts, comentarios);

        request.setAttribute("usuario", usuarioAtual);
        request.setAttribute("outroUsuario", outroUsuario);
        request.setAttribute("usuariosPorUid", usuariosPorUid);
        request.setAttribute("posts", posts);
        request.setAttribute("postPrincipal", postPrincipal);
        request.setAttribute("comentarios", comentarios);
        request.setAttribute("conversas", conversas);
        request.setAttribute("chatAtual", chatAtual);
        request.setAttribute("mensagens", mensagens);
        request.setAttribute("firebaseOnline", true);
        request.setAttribute("firebaseStatus", "Firestore conectado");
        request.setAttribute("autenticado", !estaVazio(uidSessao));
        request.setAttribute("busca", busca);
        request.setAttribute("feedAtivo", feedAtivo);
        request.setAttribute("usuariosCount", usuarios.size());
        request.setAttribute("postsCount", posts.size());
        request.setAttribute("comentariosCount", comentarios.size());
        request.setAttribute("conversasCount", conversas.size());
    }

    private Usuario usuarioAtual(FirebaseUsuarioDAO usuarioDAO, List<Usuario> usuarios, String uidSessao)
            throws Exception {
        if (!estaVazio(uidSessao)) {
            return usuarioDAO.buscarPorUid(uidSessao)
                    .orElseThrow(() -> new IllegalStateException("Sessao encontrada, mas o perfil nao existe em users."));
        }

        return usuarios.stream().findFirst().orElse(demoService.usuarioAtual());
    }

    private List<Post> carregarPostsDaTela(HttpServletRequest request, FirebaseUsuarioDAO usuarioDAO,
                                           FirebasePostDAO postDAO, String usuarioAtualUid, String feedAtivo)
            throws Exception {
        if ("/perfil".equals(request.getServletPath()) && !estaVazio(usuarioAtualUid)) {
            return postDAO.listarPorUsuario(usuarioAtualUid);
        }
        if ("following".equals(feedAtivo)) {
            List<String> seguindo = usuarioDAO.listarSeguindoIds(usuarioAtualUid);
            return postDAO.listarPorUsuarios(seguindo);
        }
        return postDAO.listarPrimeiraPagina();
    }

    private String feedAtivo(HttpServletRequest request) {
        String feed = param(request, "feed").toLowerCase(Locale.ROOT);
        return "following".equals(feed) ? "following" : "for-you";
    }

    private List<Post> filtrarPosts(List<Post> posts, String busca, Map<String, Usuario> usuariosPorUid) {
        String termo = busca == null ? "" : busca.trim().toLowerCase(Locale.ROOT);
        if (termo.isEmpty()) {
            return posts;
        }

        return posts.stream()
                .filter(post -> contem(post.getDescription(), termo)
                        || contem(post.getLocationName(), termo)
                        || contem(post.getUserId(), termo)
                        || contemUsuario(usuariosPorUid.get(post.getUserId()), termo))
                .toList();
    }

    private boolean contemUsuario(Usuario usuario, String termo) {
        return usuario != null
                && (contem(usuario.getName(), termo)
                || contem(usuario.getUsername(), termo)
                || contem(usuario.getEmail(), termo));
    }

    private boolean contem(String valor, String termo) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(termo);
    }

    private Post postPrincipal(FirebasePostDAO postDAO, List<Post> posts, String postId) throws Exception {
        if (!estaVazio(postId)) {
            return postDAO.buscarPorId(postId).orElse(null);
        }

        return posts.stream().findFirst().orElse(null);
    }

    private ChatThread chatAtual(FirebaseChatDAO chatDAO, List<ChatThread> conversas, String chatId, String uidSessao)
            throws Exception {
        if (!estaVazio(chatId)) {
            Optional<ChatThread> chat = chatDAO.buscarPorId(chatId);
            if (chat.isPresent() && chat.get().getParticipants().contains(uidSessao)) {
                return chat.get();
            }
        }

        return conversas.stream().findFirst().orElse(null);
    }

    private Usuario outroUsuario(FirebaseUsuarioDAO usuarioDAO, List<Usuario> usuarios, Usuario usuarioAtual,
                                 List<ChatThread> conversas, ChatThread chatAtual, String otherUid) throws Exception {
        String uid = otherUid;
        if (estaVazio(uid) && chatAtual != null && chatAtual.getParticipants() != null) {
            uid = chatAtual.getParticipants().stream()
                    .filter(participante -> !participante.equals(usuarioAtual.getUid()))
                    .findFirst()
                    .orElse("");
        }
        if (estaVazio(uid) && !conversas.isEmpty() && conversas.get(0).getParticipants() != null) {
            uid = conversas.get(0).getParticipants().stream()
                    .filter(participante -> !participante.equals(usuarioAtual.getUid()))
                    .findFirst()
                    .orElse("");
        }
        if (!estaVazio(uid)) {
            Optional<Usuario> usuario = usuarioDAO.buscarPorUid(uid);
            if (usuario.isPresent()) {
                return usuario.get();
            }
        }

        return usuarios.stream()
                .filter(usuario -> !usuario.getUid().equals(usuarioAtual.getUid()))
                .findFirst()
                .orElse(demoService.outroUsuario());
    }

    private Map<String, Usuario> usuariosPorUid(FirebaseUsuarioDAO usuarioDAO, List<Usuario> usuarios, Usuario usuarioAtual,
                                                Usuario outroUsuario, List<Post> posts, List<Comentario> comentarios)
            throws Exception {
        Map<String, Usuario> mapa = new HashMap<>();
        adicionarUsuario(mapa, usuarioAtual);
        adicionarUsuario(mapa, outroUsuario);
        for (Usuario usuario : usuarios) {
            adicionarUsuario(mapa, usuario);
        }

        List<String> ids = new ArrayList<>();
        posts.forEach(post -> ids.add(post.getUserId()));
        comentarios.forEach(comentario -> ids.add(comentario.getUserId()));

        for (String uid : ids) {
            if (!estaVazio(uid) && !mapa.containsKey(uid)) {
                usuarioDAO.buscarPorUid(uid).ifPresent(usuario -> adicionarUsuario(mapa, usuario));
            }
        }
        return mapa;
    }

    private void adicionarUsuario(Map<String, Usuario> mapa, Usuario usuario) {
        if (usuario != null && !estaVazio(usuario.getUid())) {
            mapa.put(usuario.getUid(), usuario);
        }
    }

    private void carregarDadosDemo(HttpServletRequest request, String status) {
        Map<String, Usuario> usuariosPorUid = new HashMap<>();
        adicionarUsuario(usuariosPorUid, demoService.usuarioAtual());
        adicionarUsuario(usuariosPorUid, demoService.outroUsuario());

        request.setAttribute("usuario", demoService.usuarioAtual());
        request.setAttribute("outroUsuario", demoService.outroUsuario());
        request.setAttribute("usuariosPorUid", usuariosPorUid);
        request.setAttribute("posts", demoService.posts());
        request.setAttribute("postPrincipal", demoService.posts().get(0));
        request.setAttribute("comentarios", demoService.comentarios());
        request.setAttribute("conversas", demoService.conversas());
        request.setAttribute("chatAtual", demoService.conversas().get(0));
        request.setAttribute("mensagens", demoService.mensagens());
        request.setAttribute("firebaseOnline", false);
        request.setAttribute("firebaseStatus", status);
        request.setAttribute("autenticado", false);
        request.setAttribute("usuariosCount", 0);
        request.setAttribute("postsCount", demoService.posts().size());
        request.setAttribute("comentariosCount", demoService.comentarios().size());
        request.setAttribute("conversasCount", demoService.conversas().size());
    }

    private void resolverUsername(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = normalizarUsername(param(request, "username"));
        Optional<Usuario> usuario = new PostlyUsuarioService().buscarPorUsername(username);
        if (usuario.isEmpty() || estaVazio(usuario.get().getEmail())) {
            responderJson(response, false, Map.of("error", "Usuario nao encontrado."));
            return;
        }

        responderJson(response, true, Map.of("email", usuario.get().getEmail()));
    }

    private void verificarUsername(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String username = normalizarUsername(param(request, "username"));
        if (username.length() < 3) {
            responderJson(response, false, Map.of("error", "Minimo de 3 caracteres."));
            return;
        }

        boolean disponivel = new PostlyUsuarioService().buscarPorUsername(username).isEmpty();
        responderJson(response, disponivel, Map.of("available", String.valueOf(disponivel),
                "error", disponivel ? "" : "Nome de usuario ja esta em uso."));
    }

    private void abrirSessao(HttpServletRequest request, HttpServletResponse response) throws Exception {
        FirebaseToken token = authService.verificarToken(param(request, "idToken"));
        Optional<Usuario> usuario = new PostlyUsuarioService().buscarPorUid(token.getUid());
        if (usuario.isEmpty()) {
            responderJson(response, false, Map.of(
                    "needsProfile", "true",
                    "suggestedUsername", sugerirUsername(emailDoToken(token), nomeDoToken(token)),
                    "name", valorOuVazio(nomeDoToken(token)),
                    "email", valorOuVazio(emailDoToken(token))
            ));
            return;
        }

        definirSessao(request, usuario.get());
        responderJson(response, true, Map.of("redirect", request.getContextPath() + "/home"));
    }

    private void registrarUsuario(HttpServletRequest request, HttpServletResponse response) throws Exception {
        FirebaseToken token = authService.verificarToken(param(request, "idToken"));
        String username = normalizarUsername(param(request, "username"));
        String name = valorOuPadrao(param(request, "name"), nomeDoToken(token), "Usuario");
        String email = valorOuPadrao(emailDoToken(token), param(request, "email"), "");

        Usuario usuario = new Usuario(token.getUid(), name, username, email, imagemDoToken(token));
        Usuario salvo = new PostlyUsuarioService().salvarComUsernameUnico(usuario);
        definirSessao(request, salvo);
        responderJson(response, true, Map.of("redirect", request.getContextPath() + "/home"));
    }

    private void completarUsuarioGoogle(HttpServletRequest request, HttpServletResponse response) throws Exception {
        FirebaseToken token = authService.verificarToken(param(request, "idToken"));
        String username = normalizarUsername(param(request, "username"));
        String name = valorOuPadrao(param(request, "name"), nomeDoToken(token), "Usuario");
        String email = valorOuPadrao(emailDoToken(token), param(request, "email"), "");

        Usuario usuario = new Usuario(token.getUid(), name, username, email, imagemDoToken(token));
        Usuario salvo = new PostlyUsuarioService().salvarComUsernameUnico(usuario);
        definirSessao(request, salvo);
        responderJson(response, true, Map.of("redirect", request.getContextPath() + "/home"));
    }

    private void sair(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/welcome");
    }

    private void criarPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uid = uidObrigatorio(request);
        String imagem = imagemUpload(request, "imageFile");
        Double latitude = decimal(param(request, "latitude"));
        Double longitude = decimal(param(request, "longitude"));
        new PostlyPostService().salvarNovoPost(uid, param(request, "description"), imagem,
                latitude, longitude, param(request, "locationName"));

        adicionarFlash(request, FLASH_MENSAGEM, "Post publicado no Firestore.");
        response.sendRedirect(request.getContextPath() + "/home");
    }

    private void editarPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uid = uidObrigatorio(request);
        String postId = param(request, "postId");
        PostlyPostService postService = new PostlyPostService();
        Post atual = postService.buscarPost(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post nao encontrado."));

        String imagem = imagemUpload(request, "imageFile");
        Post atualizado = new Post();
        atualizado.setId(postId);
        atualizado.setDescription(param(request, "description"));
        atualizado.setImage(estaVazio(imagem) ? atual.getImage() : imagem);
        atualizado.setLatitude(decimal(param(request, "latitude")));
        atualizado.setLongitude(decimal(param(request, "longitude")));
        atualizado.setLocationName(param(request, "locationName"));

        postService.atualizarPost(uid, atualizado);
        adicionarFlash(request, FLASH_MENSAGEM, "Post atualizado.");
        response.sendRedirect(request.getContextPath() + "/post?id=" + encode(postId));
    }

    private void acaoPost(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uid = uidObrigatorio(request);
        String postId = param(request, "postId");
        String action = param(request, "action");

        switch (action) {
            case "comment" -> {
                new PostlyComentarioService().adicionarComentario(postId, uid, param(request, "comment"));
                adicionarFlash(request, FLASH_MENSAGEM, "Comentario salvo.");
                response.sendRedirect(request.getContextPath() + "/post?id=" + encode(postId));
            }
            case "delete-comment" -> {
                new PostlyComentarioService().excluirComentario(postId, param(request, "commentId"), uid);
                adicionarFlash(request, FLASH_MENSAGEM, "Comentario excluido.");
                response.sendRedirect(request.getContextPath() + "/post?id=" + encode(postId));
            }
            case "like" -> {
                new PostlyPostService().alternarCurtida(postId, uid);
                response.sendRedirect(request.getContextPath() + "/post?id=" + encode(postId));
            }
            case "delete-post" -> {
                new PostlyPostService().excluirPost(postId, uid);
                adicionarFlash(request, FLASH_MENSAGEM, "Post excluido.");
                response.sendRedirect(request.getContextPath() + "/home");
            }
            default -> throw new IllegalArgumentException("Acao de post invalida.");
        }
    }

    private void enviarMensagem(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uid = uidObrigatorio(request);
        String chatId = param(request, "chatId");
        new PostlyChatService().enviarMensagem(chatId, uid, param(request, "message"));
        response.sendRedirect(request.getContextPath() + "/chat?chatId=" + encode(chatId));
    }

    private void salvarPerfil(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String uid = uidObrigatorio(request);
        PostlyUsuarioService usuarioService = new PostlyUsuarioService();
        Usuario atual = usuarioService.buscarPorUid(uid)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado."));

        String novaFoto = imagemUpload(request, "photoFile");
        Usuario usuario = new Usuario();
        usuario.setUid(uid);
        usuario.setName(param(request, "name"));
        usuario.setUsername(normalizarUsername(param(request, "username")));
        usuario.setEmail(atual.getEmail());
        usuario.setPhoto(estaVazio(novaFoto) ? atual.getPhoto() : novaFoto);

        usuarioService.salvarComUsernameUnico(usuario);
        adicionarFlash(request, FLASH_MENSAGEM, "Perfil salvo no Firestore.");
        response.sendRedirect(request.getContextPath() + "/perfil");
    }

    private void senhaSemJavascript(HttpServletRequest request, HttpServletResponse response) throws IOException {
        adicionarFlash(request, FLASH_ERRO,
                "Para alterar senha, use a tela com JavaScript do Firebase Auth carregado. O servidor nao valida senha atual pelo Admin SDK.");
        response.sendRedirect(request.getContextPath() + "/mudar-senha");
    }

    private void tratarErroPost(HttpServletRequest request, HttpServletResponse response, String path, Exception exception)
            throws IOException {
        if (path.startsWith("/auth/")) {
            responderJson(response, false, Map.of("error", mensagemCurta(exception)));
            return;
        }

        adicionarFlash(request, FLASH_ERRO, mensagemCurta(exception));
        response.sendRedirect(request.getContextPath() + redirectErro(path, request));
    }

    private String redirectErro(String path, HttpServletRequest request) {
        return switch (path) {
            case "/post" -> "/post?id=" + encode(param(request, "postId"));
            case "/editar-post" -> "/editar-post?id=" + encode(param(request, "postId"));
            case "/chat" -> "/chat?chatId=" + encode(param(request, "chatId"));
            default -> path;
        };
    }

    private String uidObrigatorio(HttpServletRequest request) {
        String uid = usuarioAutenticadoUid(request);
        if (estaVazio(uid)) {
            throw new IllegalArgumentException("Usuario nao autenticado.");
        }
        return uid;
    }

    private void definirSessao(HttpServletRequest request, Usuario usuario) {
        HttpSession session = request.getSession(true);
        session.setAttribute(SESSION_UID, usuario.getUid());
        session.setAttribute("postly.username", usuario.getUsername());
    }

    private String usuarioAutenticadoUid(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return "";
        }
        Object uid = session.getAttribute(SESSION_UID);
        return uid == null ? "" : uid.toString();
    }

    private boolean rotaProtegida(String path) {
        return switch (path) {
            case "/home", "/postar", "/editar-post", "/mensagens", "/chat",
                    "/perfil", "/configuracoes", "/mudar-senha", "/post" -> true;
            default -> false;
        };
    }

    private void carregarFlash(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return;
        }

        copiarFlash(request, session, FLASH_ERRO, "erro");
        copiarFlash(request, session, FLASH_MENSAGEM, "mensagem");
    }

    private void copiarFlash(HttpServletRequest request, HttpSession session, String origem, String destino) {
        Object valor = session.getAttribute(origem);
        if (valor != null) {
            request.setAttribute(destino, valor);
            session.removeAttribute(origem);
        }
    }

    private void adicionarFlash(HttpServletRequest request, String chave, String valor) {
        request.getSession(true).setAttribute(chave, valor);
    }

    private String imagemUpload(HttpServletRequest request, String campo) throws IOException, ServletException {
        Part part = request.getPart(campo);
        if (part == null || part.getSize() == 0) {
            return null;
        }
        return imagemService.partParaBase64(part);
    }

    private Double decimal(String valor) {
        if (estaVazio(valor)) {
            return null;
        }
        return Double.valueOf(valor.replace(',', '.'));
    }

    private String param(HttpServletRequest request, String nome) {
        String valor = request.getParameter(nome);
        return valor == null ? "" : valor.trim();
    }

    private String normalizarUsername(String username) {
        String limpo = username == null ? "" : username.trim().toLowerCase();
        return limpo.startsWith("@") ? limpo.substring(1) : limpo;
    }

    private boolean estaVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private String valorOuVazio(String valor) {
        return valor == null ? "" : valor;
    }

    private String valorOuPadrao(String primeiro, String segundo, String padrao) {
        if (!estaVazio(primeiro)) {
            return primeiro.trim();
        }
        if (!estaVazio(segundo)) {
            return segundo.trim();
        }
        return padrao;
    }

    private String emailDoToken(FirebaseToken token) {
        return claim(token, "email");
    }

    private String nomeDoToken(FirebaseToken token) {
        return claim(token, "name");
    }

    private String imagemDoToken(FirebaseToken token) {
        return claim(token, "picture");
    }

    private String claim(FirebaseToken token, String nome) {
        Object valor = token.getClaims().get(nome);
        return valor == null ? "" : valor.toString();
    }

    private String sugerirUsername(String email, String nome) {
        String base = !estaVazio(email) && email.contains("@") ? email.substring(0, email.indexOf('@')) : nome;
        String limpo = base == null ? "" : base.toLowerCase().replaceAll("[^a-z0-9._]", "");
        if (limpo.isBlank()) {
            return "usuario";
        }
        return limpo.length() > 18 ? limpo.substring(0, 18) : limpo;
    }

    private void responderJson(HttpServletResponse response, boolean ok, Map<String, String> dados) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(ok ? HttpServletResponse.SC_OK : HttpServletResponse.SC_BAD_REQUEST);

        StringBuilder json = new StringBuilder();
        json.append("{\"ok\":").append(ok);
        for (Map.Entry<String, String> entry : dados.entrySet()) {
            json.append(",\"")
                    .append(jsonEscape(entry.getKey()))
                    .append("\":\"")
                    .append(jsonEscape(entry.getValue()))
                    .append("\"");
        }
        json.append('}');
        response.getWriter().write(json.toString());
    }

    private String jsonEscape(String valor) {
        if (valor == null) {
            return "";
        }
        return valor
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\r", "\\r")
                .replace("\n", "\\n");
    }

    private String encode(String valor) {
        return URLEncoder.encode(valorOuVazio(valor), StandardCharsets.UTF_8);
    }

    private String mensagemCurta(Exception exception) {
        String mensagem = exception.getMessage();
        if (mensagem == null || mensagem.trim().isEmpty()) {
            return exception.getClass().getSimpleName();
        }
        return mensagem.length() > 140 ? mensagem.substring(0, 137) + "..." : mensagem;
    }

    private String viewPara(String path) {
        return switch (path) {
            case "/criar-conta" -> "/WEB-INF/views/criar-conta.jsp";
            case "/entrar" -> "/WEB-INF/views/entrar.jsp";
            case "/home" -> "/WEB-INF/views/home.jsp";
            case "/postar" -> "/WEB-INF/views/postar.jsp";
            case "/editar-post" -> "/WEB-INF/views/editar-post.jsp";
            case "/mensagens" -> "/WEB-INF/views/mensagens.jsp";
            case "/chat" -> "/WEB-INF/views/chat.jsp";
            case "/perfil" -> "/WEB-INF/views/perfil.jsp";
            case "/configuracoes" -> "/WEB-INF/views/configuracoes.jsp";
            case "/mudar-senha" -> "/WEB-INF/views/mudar-senha.jsp";
            case "/post" -> "/WEB-INF/views/post.jsp";
            default -> "/WEB-INF/views/welcome.jsp";
        };
    }
}
