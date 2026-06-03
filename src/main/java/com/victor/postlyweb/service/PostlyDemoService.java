package com.victor.postlyweb.service;

import com.victor.postlyweb.modelo.ChatMessage;
import com.victor.postlyweb.modelo.ChatThread;
import com.victor.postlyweb.modelo.Comentario;
import com.victor.postlyweb.modelo.Post;
import com.victor.postlyweb.modelo.Usuario;

import java.util.Arrays;
import java.util.List;

public class PostlyDemoService {

    public Usuario usuarioAtual() {
        return new Usuario(
                "HvAUS6qaNxfNWY1HrTT1VVtdOhS2",
                "victor",
                "victor",
                "victor@gmail.com",
                "assets/img/avatar-demo.svg"
        );
    }

    public Usuario outroUsuario() {
        return new Usuario(
                "NJtn2U8H7nNJUVGGhkm1WUNFkgh2",
                "Silvio Luiz Santos",
                "silvio",
                "silvio@example.com",
                "assets/img/avatar-alt.svg"
        );
    }

    public List<Post> posts() {
        return Arrays.asList(
                post("8RcHtz2JAEidSAA5Psu7", "eeeeeeeeeeeeeeeeeebaaaa", "assets/img/post-demo.svg", 2, 5, 4),
                post("EpqOEukPYGKBYQcvuLaL", "olaaa", null, 1, 3, 5),
                post("Fzgds5mP48zWhF96xhRo", "OLaaaaa rede!!!", null, 0, 1, 6),
                post("P1Y0EeYzRpVDFIlmPxeV", "Postly web conectado ao mesmo Firestore do app mobile.", null, 4, 2, 8)
        );
    }

    public List<Comentario> comentarios() {
        return Arrays.asList(
                comentario("c1", "ola"),
                comentario("c2", "oll"),
                comentario("c3", "olaaa"),
                comentario("c4", "ficou massa"),
                comentario("c5", "testando pelo web")
        );
    }

    public List<ChatThread> conversas() {
        ChatThread conversa = new ChatThread();
        conversa.setId("HvAUS6qaNxfNWY1HrTT1VVtdOhS2_NJtn2U8H7nNJUVGGhkm1WUNFkgh2");
        conversa.setParticipants(Arrays.asList(usuarioAtual().getUid(), outroUsuario().getUid()));
        conversa.setLastMessage("ilaaa");
        conversa.setLastSenderId(usuarioAtual().getUid());
        conversa.setLastTimestamp(System.currentTimeMillis() - 86400000L);
        conversa.setUpdatedAt(conversa.getLastTimestamp());
        conversa.setCreatedAt(conversa.getLastTimestamp() - 3600000L);
        return List.of(conversa);
    }

    public List<ChatMessage> mensagens() {
        ChatMessage mensagem = new ChatMessage();
        mensagem.setId("m1");
        mensagem.setChatId(conversas().get(0).getId());
        mensagem.setSenderId(usuarioAtual().getUid());
        mensagem.setText("ilaaa");
        mensagem.setTimestamp(System.currentTimeMillis() - 86400000L);
        return List.of(mensagem);
    }

    private Post post(String id, String descricao, String image, int likes, int comments, int dias) {
        Post post = new Post();
        post.setId(id);
        post.setUserId(usuarioAtual().getUid());
        post.setDescription(descricao);
        post.setImage(image);
        post.setTimestamp(System.currentTimeMillis() - dias * 86400000L);
        post.setLikeCount(likes);
        post.setCommentCount(comments);
        post.setLikedBy(List.of(usuarioAtual().getUid()));
        return post;
    }

    private Comentario comentario(String id, String text) {
        Comentario comentario = new Comentario();
        comentario.setId(id);
        comentario.setPostId("8RcHtz2JAEidSAA5Psu7");
        comentario.setUserId(usuarioAtual().getUid());
        comentario.setText(text);
        comentario.setTimestamp(System.currentTimeMillis() - 86400000L);
        return comentario;
    }
}
