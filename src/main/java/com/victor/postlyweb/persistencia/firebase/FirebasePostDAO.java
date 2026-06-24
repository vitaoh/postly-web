package com.victor.postlyweb.persistencia.firebase;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.victor.postlyweb.config.FirebaseConfig;
import com.victor.postlyweb.modelo.Post;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FirebasePostDAO {

    private static final String COLECAO_POSTS = "posts";

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

    /**
     * Pagina o feed por cursor (timestamp do ultimo post visto), como no Twitter:
     * a primeira pagina vem sem cursor e as seguintes continuam de onde pararam.
     */
    public List<Post> listarFeed(Long cursorTimestamp, int limite)
            throws ExecutionException, InterruptedException {
        Query query = firestore.collection(COLECAO_POSTS)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        if (cursorTimestamp != null && cursorTimestamp > 0L) {
            query = query.startAfter(cursorTimestamp);
        }

        return query.limit(limite)
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

    public List<Post> listarPorUsuarios(List<String> userIds) throws ExecutionException, InterruptedException {
        if (userIds == null || userIds.isEmpty()) {
            return List.of();
        }

        List<String> ids = userIds.stream()
                .filter(id -> !estaVazio(id))
                .distinct()
                .collect(Collectors.toList());

        if (ids.isEmpty()) {
            return List.of();
        }

        List<Post> posts = new ArrayList<>();
        for (int inicio = 0; inicio < ids.size(); inicio += 10) {
            List<String> bloco = ids.subList(inicio, Math.min(inicio + 10, ids.size()));
            posts.addAll(firestore.collection(COLECAO_POSTS)
                    .whereIn("userId", bloco)
                    .get()
                    .get()
                    .getDocuments()
                    .stream()
                    .map(this::converterPost)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList()));
        }

        return posts.stream()
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
