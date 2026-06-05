package com.victor.postlyweb.persistencia.firebase;

import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.cloud.firestore.WriteBatch;
import com.victor.postlyweb.config.FirebaseConfig;
import com.victor.postlyweb.modelo.Usuario;

import java.io.IOException;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FirebaseUsuarioDAO {

    private static final String COLECAO_USUARIOS = "users";
    private static final String SUBCOLECAO_FOLLOWERS = "followers";
    private static final String SUBCOLECAO_FOLLOWING = "following";

    private final Firestore firestore;

    public FirebaseUsuarioDAO() throws IOException {
        this.firestore = FirebaseConfig.getFirestore();
    }

    public Usuario salvar(Usuario usuario) throws ExecutionException, InterruptedException {
        usuario.setUsername(normalizarUsername(usuario.getUsername()));
        DocumentReference documento = definirDocumento(usuario);
        usuario.setUid(documento.getId());
        documento.set(usuario).get();

        return usuario;
    }

    public Optional<Usuario> buscarPorUid(String uid) throws ExecutionException, InterruptedException {
        DocumentSnapshot snapshot = firestore.collection(COLECAO_USUARIOS)
                .document(uid)
                .get()
                .get();

        if (!snapshot.exists()) {
            return Optional.empty();
        }

        return Optional.ofNullable(snapshot.toObject(Usuario.class));
    }

    public Optional<Usuario> buscarPorUsername(String username) throws ExecutionException, InterruptedException {
        QuerySnapshot resultado = firestore.collection(COLECAO_USUARIOS)
                .whereEqualTo("username", normalizarUsername(username))
                .limit(1)
                .get()
                .get();

        return resultado.getDocuments().stream()
                .findFirst()
                .map(documento -> documento.toObject(Usuario.class));
    }

    public List<Usuario> listarPrimeiros(int limite) throws ExecutionException, InterruptedException {
        return firestore.collection(COLECAO_USUARIOS)
                .limit(limite)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(this::converterUsuario)
                .flatMap(Optional::stream)
                .collect(Collectors.toList());
    }

    public void excluir(String uid) throws ExecutionException, InterruptedException {
        firestore.collection(COLECAO_USUARIOS)
                .document(uid)
                .delete()
                .get();
    }

    public boolean estaSeguindo(String usuarioAtualId, String usuarioAlvoId)
            throws ExecutionException, InterruptedException {
        return usuarioRef(usuarioAtualId)
                .collection(SUBCOLECAO_FOLLOWING)
                .document(usuarioAlvoId)
                .get()
                .get()
                .exists();
    }

    public int contarSeguidores(String uid) throws ExecutionException, InterruptedException {
        return usuarioRef(uid).collection(SUBCOLECAO_FOLLOWERS).get().get().size();
    }

    public int contarSeguindo(String uid) throws ExecutionException, InterruptedException {
        return usuarioRef(uid).collection(SUBCOLECAO_FOLLOWING).get().get().size();
    }

    public List<String> listarSeguindoIds(String uid) throws ExecutionException, InterruptedException {
        return usuarioRef(uid)
                .collection(SUBCOLECAO_FOLLOWING)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(documento -> {
                    String userId = documento.getString("userId");
                    return estaVazio(userId) ? documento.getId() : userId;
                })
                .filter(userId -> !estaVazio(userId))
                .collect(Collectors.toList());
    }

    public void seguir(String usuarioAtualId, String usuarioAlvoId) throws ExecutionException, InterruptedException {
        if (estaVazio(usuarioAtualId) || estaVazio(usuarioAlvoId) || usuarioAtualId.equals(usuarioAlvoId)) {
            throw new IllegalArgumentException("Usuarios invalidos para seguir.");
        }

        WriteBatch batch = firestore.batch();
        batch.set(
                usuarioRef(usuarioAtualId).collection(SUBCOLECAO_FOLLOWING).document(usuarioAlvoId),
                criarDocumentoFollow(usuarioAlvoId)
        );
        batch.set(
                usuarioRef(usuarioAlvoId).collection(SUBCOLECAO_FOLLOWERS).document(usuarioAtualId),
                criarDocumentoFollow(usuarioAtualId)
        );
        batch.commit().get();
    }

    public void deixarDeSeguir(String usuarioAtualId, String usuarioAlvoId)
            throws ExecutionException, InterruptedException {
        WriteBatch batch = firestore.batch();
        batch.delete(usuarioRef(usuarioAtualId).collection(SUBCOLECAO_FOLLOWING).document(usuarioAlvoId));
        batch.delete(usuarioRef(usuarioAlvoId).collection(SUBCOLECAO_FOLLOWERS).document(usuarioAtualId));
        batch.commit().get();
    }

    private DocumentReference definirDocumento(Usuario usuario) {
        CollectionReference usuarios = firestore.collection(COLECAO_USUARIOS);
        if (estaVazio(usuario.getUid())) {
            return usuarios.document();
        }

        return usuarios.document(usuario.getUid());
    }

    private DocumentReference usuarioRef(String uid) {
        return firestore.collection(COLECAO_USUARIOS).document(uid);
    }

    private Optional<Usuario> converterUsuario(DocumentSnapshot documento) {
        Usuario usuario = documento.toObject(Usuario.class);
        if (usuario == null) {
            return Optional.empty();
        }
        if (estaVazio(usuario.getUid())) {
            usuario.setUid(documento.getId());
        }
        return Optional.of(usuario);
    }

    private Map<String, Object> criarDocumentoFollow(String userId) {
        Map<String, Object> dados = new HashMap<>();
        dados.put("userId", userId);
        dados.put("createdAt", FieldValue.serverTimestamp());
        return dados;
    }

    private String normalizarUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    private boolean estaVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
