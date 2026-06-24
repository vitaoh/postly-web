package com.victor.postlyweb.service;

import com.victor.postlyweb.modelo.Post;
import com.victor.postlyweb.persistencia.firebase.FirebasePostDAO;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class PostlyPostService {

    private final FirebasePostDAO postDAO;

    public PostlyPostService() throws IOException {
        this.postDAO = new FirebasePostDAO();
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

    private boolean estaVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
