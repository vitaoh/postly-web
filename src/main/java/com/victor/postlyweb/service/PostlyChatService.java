package com.victor.postlyweb.service;

import com.victor.postlyweb.modelo.ChatMessage;
import com.victor.postlyweb.modelo.ChatThread;
import com.victor.postlyweb.persistencia.firebase.FirebaseChatDAO;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public class PostlyChatService {

    private final FirebaseChatDAO chatDAO;

    public PostlyChatService() throws IOException {
        this.chatDAO = new FirebaseChatDAO();
    }

    public ChatMessage enviarMensagem(String chatId, String senderId, String texto)
            throws ExecutionException, InterruptedException {
        validarRemetente(chatId, senderId);
        return chatDAO.enviarMensagem(chatId, senderId, texto);
    }

    public ChatMessage enviarMidia(String chatId, String senderId, String type,
                                   String mediaBase64, String mediaMimeType)
            throws ExecutionException, InterruptedException {
        validarRemetente(chatId, senderId);
        return chatDAO.enviarMensagemMidia(chatId, senderId, type, mediaBase64, mediaMimeType);
    }

    private void validarRemetente(String chatId, String senderId)
            throws ExecutionException, InterruptedException {
        ChatThread chat = chatDAO.buscarPorId(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Conversa nao encontrada."));
        if (chat.getParticipants() == null || !chat.getParticipants().contains(senderId)) {
            throw new IllegalArgumentException("Voce nao pode enviar mensagem nesta conversa.");
        }
    }
}
