package com.victor.postlyweb.service;

import com.victor.postlyweb.modelo.Comentario;
import com.victor.postlyweb.modelo.Post;
import com.victor.postlyweb.persistencia.firebase.FirebaseComentarioDAO;
import com.victor.postlyweb.persistencia.firebase.FirebasePostDAO;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PostlyComentarioService {

    private final FirebaseComentarioDAO comentarioDAO;
    private final FirebasePostDAO postDAO;

    public PostlyComentarioService() throws IOException {
        this.comentarioDAO = new FirebaseComentarioDAO();
        this.postDAO = new FirebasePostDAO();
    }

    public List<Comentario> listarComentarios(String postId) throws ExecutionException, InterruptedException {
        if (estaVazio(postId)) {
            return List.of();
        }
        return comentarioDAO.listarPorPost(postId);
    }

    public Comentario adicionarComentario(String postId, String usuarioAtualUid, String texto)
            throws ExecutionException, InterruptedException {
        if (estaVazio(postId) || estaVazio(usuarioAtualUid) || estaVazio(texto)) {
            throw new IllegalArgumentException("Comentario invalido.");
        }

        Comentario comentario = new Comentario();
        comentario.setPostId(postId);
        comentario.setUserId(usuarioAtualUid);
        comentario.setText(texto.trim());
        comentario.setTimestamp(System.currentTimeMillis());
        return comentarioDAO.adicionar(postId, comentario);
    }

    public void excluirComentario(String postId, String comentarioId, String usuarioAtualUid)
            throws ExecutionException, InterruptedException {
        if (estaVazio(postId) || estaVazio(comentarioId) || estaVazio(usuarioAtualUid)) {
            throw new IllegalArgumentException("Comentario invalido.");
        }

        Comentario comentario = comentarioDAO.buscarPorId(postId, comentarioId)
                .orElseThrow(() -> new IllegalArgumentException("Comentario nao encontrado."));
        Post post = postDAO.buscarPorId(postId)
                .orElseThrow(() -> new IllegalArgumentException("Publicacao nao encontrada."));

        boolean autorComentario = usuarioAtualUid.equals(comentario.getUserId());
        boolean autorPost = usuarioAtualUid.equals(post.getUserId());
        if (!autorComentario && !autorPost) {
            throw new IllegalArgumentException("Voce nao pode excluir este comentario.");
        }

        comentarioDAO.excluir(postId, comentarioId);
    }

    private boolean estaVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
