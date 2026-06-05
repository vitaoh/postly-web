package com.victor.postlyweb.persistencia.firebase;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.WriteBatch;
import com.victor.postlyweb.config.FirebaseConfig;
import com.victor.postlyweb.modelo.ChatMessage;
import com.victor.postlyweb.modelo.ChatThread;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class FirebaseChatDAO {

    private static final String COLECAO_CHATS = "chats";
    private static final String SUBCOLECAO_MESSAGES = "messages";

    private final Firestore firestore;

    public FirebaseChatDAO() throws IOException {
        this.firestore = FirebaseConfig.getFirestore();
    }

    public String chatIdParaUsuarios(String primeiroUsuarioId, String segundoUsuarioId) {
        return Arrays.asList(primeiroUsuarioId, segundoUsuarioId)
                .stream()
                .sorted()
                .collect(Collectors.joining("_"));
    }

    public ChatThread criarOuBuscarChat(String usuarioAtualId, String usuarioAlvoId)
            throws ExecutionException, InterruptedException {
        if (estaVazio(usuarioAtualId) || estaVazio(usuarioAlvoId) || usuarioAtualId.equals(usuarioAlvoId)) {
            throw new IllegalArgumentException("Conversa invalida.");
        }

        String chatId = chatIdParaUsuarios(usuarioAtualId, usuarioAlvoId);
        DocumentReference chatRef = firestore.collection(COLECAO_CHATS).document(chatId);
        DocumentSnapshot snapshot = chatRef.get().get();

        if (snapshot.exists()) {
            return converterChat(snapshot);
        }

        long agora = System.currentTimeMillis();
        ChatThread chat = new ChatThread();
        chat.setId(chatId);
        chat.setParticipants(Arrays.asList(usuarioAtualId, usuarioAlvoId).stream().sorted().collect(Collectors.toList()));
        chat.setCreatedAt(agora);
        chat.setUpdatedAt(agora);

        chatRef.set(chat).get();
        return chat;
    }

    public Optional<ChatThread> buscarPorId(String chatId) throws ExecutionException, InterruptedException {
        if (estaVazio(chatId)) {
            return Optional.empty();
        }

        DocumentSnapshot snapshot = firestore.collection(COLECAO_CHATS).document(chatId).get().get();
        if (!snapshot.exists()) {
            return Optional.empty();
        }

        return Optional.ofNullable(converterChat(snapshot));
    }

    public List<ChatThread> listarConversas(String userId) throws ExecutionException, InterruptedException {
        return firestore.collection(COLECAO_CHATS)
                .whereArrayContains("participants", userId)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(this::converterChat)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingLong(this::lastTimestamp).reversed())
                .collect(Collectors.toList());
    }

    public List<ChatMessage> listarMensagens(String chatId) throws ExecutionException, InterruptedException {
        return firestore.collection(COLECAO_CHATS)
                .document(chatId)
                .collection(SUBCOLECAO_MESSAGES)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .get()
                .getDocuments()
                .stream()
                .map(documento -> converterMensagem(documento, chatId))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public ChatMessage enviarMensagem(String chatId, String senderId, String text)
            throws ExecutionException, InterruptedException {
        String textoLimpo = text == null ? "" : text.trim();
        if (estaVazio(chatId) || estaVazio(senderId) || estaVazio(textoLimpo)) {
            throw new IllegalArgumentException("Mensagem invalida.");
        }

        long agora = System.currentTimeMillis();
        DocumentReference chatRef = firestore.collection(COLECAO_CHATS).document(chatId);
        DocumentReference mensagemRef = chatRef.collection(SUBCOLECAO_MESSAGES).document();

        ChatMessage mensagem = new ChatMessage();
        mensagem.setId(mensagemRef.getId());
        mensagem.setChatId(chatId);
        mensagem.setSenderId(senderId);
        mensagem.setText(textoLimpo);
        mensagem.setTimestamp(agora);

        WriteBatch batch = firestore.batch();
        batch.set(mensagemRef, mensagem);
        batch.update(chatRef, Map.of(
                "lastMessage", textoLimpo,
                "lastSenderId", senderId,
                "lastTimestamp", agora,
                "updatedAt", agora
        ));
        batch.commit().get();

        return mensagem;
    }

    private ChatThread converterChat(DocumentSnapshot documento) {
        ChatThread chat = documento.toObject(ChatThread.class);
        if (chat != null && estaVazio(chat.getId())) {
            chat.setId(documento.getId());
        }
        return chat;
    }

    private ChatMessage converterMensagem(DocumentSnapshot documento, String chatId) {
        ChatMessage mensagem = documento.toObject(ChatMessage.class);
        if (mensagem != null) {
            if (estaVazio(mensagem.getId())) {
                mensagem.setId(documento.getId());
            }
            if (estaVazio(mensagem.getChatId())) {
                mensagem.setChatId(chatId);
            }
        }
        return mensagem;
    }

    private boolean estaVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private long lastTimestamp(ChatThread chat) {
        if (chat.getLastTimestamp() != null && chat.getLastTimestamp() > 0L) {
            return chat.getLastTimestamp();
        }
        if (chat.getUpdatedAt() != null && chat.getUpdatedAt() > 0L) {
            return chat.getUpdatedAt();
        }
        return chat.getCreatedAt() == null ? 0L : chat.getCreatedAt();
    }
}
