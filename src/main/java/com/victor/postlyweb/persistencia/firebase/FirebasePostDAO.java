package com.victor.postlyweb.persistencia.firebase;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.victor.postlyweb.config.FirebaseConfig;
import com.victor.postlyweb.modelo.Post;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FirebasePostDAO {

    private static final String COLECAO_POSTS = "posts";
    private static final int PAGE_SIZE = 5;

    private final Firestore firestore;

    public FirebasePostDAO() throws IOException {
        this.firestore = FirebaseConfig.getFirestore();
    }

    public Post adicionar(Post post) throws ExecutionException, InterruptedException {
        DocumentReference documento = firestore.collection(COLECAO_POSTS).document();
        post.setId(documento.getId());
        documento.set(post).get();
        return post;
    }

    public void atualizar(Post post) throws ExecutionException, InterruptedException {
        firestore.collection(COLECAO_POSTS).document(post.getId()).set(post).get();
    }

    public Optional<Post> buscarPorId(String postId) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection(COLECAO_POSTS).document(postId).get().get();
        if (!snapshot.exists()) {
            return Optional.empty();
        }

        Post post = snapshot.toObject(Post.class);
        if (post != null && estaVazio(post.getId())) {
            post.setId(snapshot.getId());
        }

        return Optional.ofNullable(post);
    }

    public List<Post> listarPrimeiraPagina() throws ExecutionException, InterruptedException {
        return firestore.collection(COLECAO_POSTS)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(PAGE_SIZE)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(this::converterPost)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Post> listarPorUsuario(String userId) throws ExecutionException, InterruptedException {
        return firestore.collection(COLECAO_POSTS)
                .whereEqualTo("userId", userId)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(this::converterPost)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(this::timestamp).reversed())
                .collect(Collectors.toList());
    }

    public void alternarCurtida(String postId, String userId, boolean removerCurtida)
            throws ExecutionException, InterruptedException {
        DocumentReference postRef = firestore.collection(COLECAO_POSTS).document(postId);

        firestore.runTransaction(transaction -> {
            transaction.update(postRef, Map.of(
                    "likeCount", FieldValue.increment(removerCurtida ? -1 : 1),
                    "likedBy", removerCurtida ? FieldValue.arrayRemove(userId) : FieldValue.arrayUnion(userId)
            ));
            return null;
        }).get();
    }

    public void excluir(String postId) throws ExecutionException, InterruptedException {
        firestore.collection(COLECAO_POSTS).document(postId).delete().get();
    }

    private Post converterPost(DocumentSnapshot documento) {
        Post post = documento.toObject(Post.class);
        if (post != null && estaVazio(post.getId())) {
            post.setId(documento.getId());
        }
        return post;
    }

    private boolean estaVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private long timestamp(Post post) {
        return post.getTimestamp() == null ? 0L : post.getTimestamp();
    }
}
