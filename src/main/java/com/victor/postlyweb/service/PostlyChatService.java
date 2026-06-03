package com.victor.postlyweb.service;

import com.victor.postlyweb.modelo.ChatMessage;
import com.victor.postlyweb.modelo.ChatThread;
import com.victor.postlyweb.modelo.Usuario;
import com.victor.postlyweb.persistencia.firebase.FirebaseChatDAO;
import com.victor.postlyweb.persistencia.firebase.FirebaseUsuarioDAO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class PostlyChatService {

    private final FirebaseChatDAO chatDAO;
    private final FirebaseUsuarioDAO usuarioDAO;

    public PostlyChatService() throws IOException {
        this.chatDAO = new FirebaseChatDAO();
        this.usuarioDAO = new FirebaseUsuarioDAO();
    }

    public List<ChatThread> listarConversas(String usuarioAtualUid)
            throws ExecutionException, InterruptedException {
        if (estaVazio(usuarioAtualUid)) {
            return List.of();
        }
        return chatDAO.listarConversas(usuarioAtualUid);
    }

    public ChatAberto abrirConversa(String usuarioAtualUid, String usuarioAlvoUid, String chatId)
            throws ExecutionException, InterruptedException {
        if (estaVazio(usuarioAtualUid) || estaVazio(usuarioAlvoUid)) {
            throw new IllegalArgumentException("Conversa invalida.");
        }

        ChatThread chat = estaVazio(chatId)
                ? chatDAO.criarOuBuscarChat(usuarioAtualUid, usuarioAlvoUid)
                : chatDAO.buscarPorId(chatId)
                        .orElseGet(() -> criarChat(usuarioAtualUid, usuarioAlvoUid));
        validarParticipantes(chat, usuarioAtualUid, usuarioAlvoUid);

        List<ChatMessage> mensagens = chatDAO.listarMensagens(chat.getId());
        Optional<Usuario> outroUsuario = usuarioDAO.buscarPorUid(usuarioAlvoUid);

        return new ChatAberto(chat, mensagens, outroUsuario.orElse(null));
    }

    public ChatMessage enviarMensagem(String chatId, String senderId, String texto)
            throws ExecutionException, InterruptedException {
        ChatThread chat = chatDAO.buscarPorId(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Conversa nao encontrada."));
        if (chat.getParticipants() == null || !chat.getParticipants().contains(senderId)) {
            throw new IllegalArgumentException("Voce nao pode enviar mensagem nesta conversa.");
        }

        return chatDAO.enviarMensagem(chatId, senderId, texto);
    }

    private boolean estaVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private ChatThread criarChat(String usuarioAtualUid, String usuarioAlvoUid) {
        try {
            return chatDAO.criarOuBuscarChat(usuarioAtualUid, usuarioAlvoUid);
        } catch (ExecutionException | InterruptedException e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            throw new IllegalStateException("Nao foi possivel abrir a conversa.", e);
        }
    }

    private void validarParticipantes(ChatThread chat, String usuarioAtualUid, String usuarioAlvoUid) {
        if (chat.getParticipants() == null
                || !chat.getParticipants().contains(usuarioAtualUid)
                || !chat.getParticipants().contains(usuarioAlvoUid)) {
            throw new IllegalArgumentException("Voce nao pode abrir esta conversa.");
        }
    }

    public record ChatAberto(ChatThread chat, List<ChatMessage> mensagens, Usuario outroUsuario) {
    }
}
