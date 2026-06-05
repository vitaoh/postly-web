package com.victor.postlyweb.persistencia.firebase;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.victor.postlyweb.config.FirebaseConfig;
import com.victor.postlyweb.modelo.Comentario;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FirebaseComentarioDAO {

    private static final String COLECAO_POSTS = "posts";
    private static final String SUBCOLECAO_COMMENTS = "comments";

    private final Firestore firestore;

    public FirebaseComentarioDAO() throws IOException {
        this.firestore = FirebaseConfig.getFirestore();
    }

    public Comentario adicionar(String postId, Comentario comentario)
            throws ExecutionException, InterruptedException {
        DocumentReference postRef = firestore.collection(COLECAO_POSTS).document(postId);
        DocumentReference comentarioRef = postRef.collection(SUBCOLECAO_COMMENTS).document();

        comentario.setId(comentarioRef.getId());
        comentario.setPostId(postId);

        firestore.runTransaction(transaction -> {
            transaction.set(comentarioRef, comentario);
            transaction.update(postRef, "commentCount", FieldValue.increment(1));
            return null;
        }).get();

        return comentario;
    }

    public List<Comentario> listarPorPost(String postId) throws ExecutionException, InterruptedException {
        return firestore.collection(COLECAO_POSTS)
                .document(postId)
                .collection(SUBCOLECAO_COMMENTS)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(documento -> converterComentario(documento, postId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public int contarPorUsuario(String userId) throws ExecutionException, InterruptedException {
        if (userId == null || userId.trim().isEmpty()) {
            return 0;
        }

        int total = 0;
        List<? extends DocumentSnapshot> posts = firestore.collection(COLECAO_POSTS)
                .get()
                .get()
                .getDocuments();
        for (DocumentSnapshot post : posts) {
            total += post.getReference()
                    .collection(SUBCOLECAO_COMMENTS)
                    .whereEqualTo("userId", userId)
                    .get()
                    .get()
                    .size();
        }
        return total;
    }

    public Optional<Comentario> buscarPorId(String postId, String comentarioId)
            throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection(COLECAO_POSTS)
                .document(postId)
                .collection(SUBCOLECAO_COMMENTS)
                .document(comentarioId)
                .get()
                .get();

        if (!snapshot.exists()) {
            return Optional.empty();
        }

        return Optional.ofNullable(converterComentario(snapshot, postId));
    }

    public void excluir(String postId, String comentarioId) throws ExecutionException, InterruptedException {
        DocumentReference postRef = firestore.collection(COLECAO_POSTS).document(postId);
        DocumentReference comentarioRef = postRef.collection(SUBCOLECAO_COMMENTS).document(comentarioId);

        firestore.runTransaction(transaction -> {
            transaction.delete(comentarioRef);
            transaction.update(postRef, "commentCount", FieldValue.increment(-1));
            return null;
        }).get();
    }

    private Comentario converterComentario(DocumentSnapshot documento, String postId) {
        Comentario comentario = documento.toObject(Comentario.class);
        if (comentario != null) {
            if (comentario.getId() == null || comentario.getId().trim().isEmpty()) {
                comentario.setId(documento.getId());
            }
            if (comentario.getPostId() == null || comentario.getPostId().trim().isEmpty()) {
                comentario.setPostId(postId);
            }
        }
        return comentario;
    }
}
