package com.victor.postlyweb.service;

import com.victor.postlyweb.modelo.Comentario;
import com.victor.postlyweb.persistencia.firebase.FirebaseComentarioDAO;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class PostlyComentarioService {

    private final FirebaseComentarioDAO comentarioDAO;

    public PostlyComentarioService() throws IOException {
        this.comentarioDAO = new FirebaseComentarioDAO();
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

    public void excluirComentario(String postId, String comentarioId)
            throws ExecutionException, InterruptedException {
        if (estaVazio(postId) || estaVazio(comentarioId)) {
            throw new IllegalArgumentException("Comentario invalido.");
        }
        comentarioDAO.excluir(postId, comentarioId);
    }

    private boolean estaVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
