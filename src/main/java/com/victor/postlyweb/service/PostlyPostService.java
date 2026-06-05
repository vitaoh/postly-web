package com.victor.postlyweb.service;

import com.victor.postlyweb.modelo.Post;
import com.victor.postlyweb.persistencia.firebase.FirebasePostDAO;
import com.victor.postlyweb.persistencia.firebase.FirebaseUsuarioDAO;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class PostlyPostService {

    private final FirebasePostDAO postDAO;
    private final FirebaseUsuarioDAO usuarioDAO;

    public PostlyPostService() throws IOException {
        this.postDAO = new FirebasePostDAO();
        this.usuarioDAO = new FirebaseUsuarioDAO();
    }

    public List<Post> carregarFeedForYou(String busca) throws ExecutionException, InterruptedException {
        return filtrar(postDAO.listarPrimeiraPagina(), busca);
    }

    public List<Post> carregarFeedFollowing(String usuarioAtualUid, String busca)
            throws ExecutionException, InterruptedException {
        if (estaVazio(usuarioAtualUid)) {
            return List.of();
        }

        List<String> seguindo = usuarioDAO.listarSeguindoIds(usuarioAtualUid);
        return filtrar(postDAO.listarPorUsuarios(seguindo), busca);
    }

    public List<Post> listarPostsDoUsuario(String uid) throws ExecutionException, InterruptedException {
        if (estaVazio(uid)) {
            return List.of();
        }
        return postDAO.listarPorUsuario(uid);
    }

    public Optional<Post> buscarPost(String postId) throws ExecutionException, InterruptedException {
        if (estaVazio(postId)) {
            return Optional.empty();
        }
        return postDAO.buscarPorId(postId);
    }

    public Post salvarNovoPost(String usuarioAtualUid, String descricao, String imagemBase64,
                               Double latitude, Double longitude, String locationName)
            throws ExecutionException, InterruptedException {
        if (estaVazio(usuarioAtualUid)) {
            throw new IllegalArgumentException("Usuario nao autenticado.");
        }
        if (estaVazio(descricao)) {
            throw new IllegalArgumentException("Escreva uma descricao para publicar.");
        }

        Post post = new Post();
        post.setUserId(usuarioAtualUid);
        post.setDescription(descricao.trim());
        post.setImage(imagemBase64);
        post.setTimestamp(System.currentTimeMillis());
        post.setLatitude(latitude);
        post.setLongitude(longitude);
        post.setLocationName(locationName);
        return postDAO.adicionar(post);
    }

    public void atualizarPost(String usuarioAtualUid, Post postAtualizado)
            throws ExecutionException, InterruptedException {
        if (postAtualizado == null || estaVazio(postAtualizado.getId())) {
            throw new IllegalArgumentException("Publicacao invalida.");
        }

        Post atual = postDAO.buscarPorId(postAtualizado.getId())
                .orElseThrow(() -> new IllegalArgumentException("Publicacao nao encontrada."));
        if (!atual.getUserId().equals(usuarioAtualUid)) {
            throw new IllegalArgumentException("Voce nao pode editar esta publicacao.");
        }

        postAtualizado.setUserId(atual.getUserId());
        postAtualizado.setTimestamp(atual.getTimestamp());
        postAtualizado.setCommentCount(atual.getCommentCount());
        postAtualizado.setLikeCount(atual.getLikeCount());
        postAtualizado.setLikedBy(atual.getLikedBy());
        postDAO.atualizar(postAtualizado);
    }

    public Post alternarCurtida(String postId, String usuarioAtualUid)
            throws ExecutionException, InterruptedException {
        if (estaVazio(postId) || estaVazio(usuarioAtualUid)) {
            throw new IllegalArgumentException("Curtida invalida.");
        }

        Post post = postDAO.buscarPorId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Publicacao nao encontrada."));
        boolean removerCurtida = post.getLikedBy() != null && post.getLikedBy().contains(usuarioAtualUid);
        postDAO.alternarCurtida(postId, usuarioAtualUid, removerCurtida);
        return postDAO.buscarPorId(postId).orElse(post);
    }

    public void excluirPost(String postId, String usuarioAtualUid) throws ExecutionException, InterruptedException {
        Post post = postDAO.buscarPorId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Publicacao nao encontrada."));
        if (!post.getUserId().equals(usuarioAtualUid)) {
            throw new IllegalArgumentException("Voce nao pode excluir esta publicacao.");
        }
        postDAO.excluir(postId);
    }

    private List<Post> filtrar(List<Post> posts, String busca) {
        String termo = busca == null ? "" : busca.trim().toLowerCase(Locale.ROOT);
        if (termo.isEmpty()) {
            return posts;
        }

        return posts.stream()
                .filter(post -> contem(post.getDescription(), termo)
                        || contem(post.getLocationName(), termo)
                        || contem(post.getUserId(), termo))
                .collect(Collectors.toList());
    }

    private boolean contem(String valor, String termo) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(termo);
    }

    private boolean estaVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
