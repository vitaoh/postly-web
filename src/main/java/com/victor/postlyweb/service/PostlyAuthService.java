package com.victor.postlyweb.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.victor.postlyweb.modelo.Usuario;

public class PostlyAuthService {

    public Usuario criarUsuarioAuth(String email, String senha, String name, String username)
            throws FirebaseAuthException {
        validarEmailSenha(email, senha);

        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email.trim())
                .setPassword(senha)
                .setDisplayName(name == null ? "" : name.trim());

        UserRecord userRecord = FirebaseAuth.getInstance().createUser(request);

        Usuario usuario = new Usuario();
        usuario.setUid(userRecord.getUid());
        usuario.setName(name == null ? "" : name.trim());
        usuario.setUsername(normalizarUsername(username));
        usuario.setEmail(email.trim());
        return usuario;
    }

    public String gerarLinkRedefinicaoSenha(String email) throws FirebaseAuthException {
        if (estaVazio(email)) {
            throw new IllegalArgumentException("Informe o e-mail para redefinir a senha.");
        }
        return FirebaseAuth.getInstance().generatePasswordResetLink(email.trim());
    }

    public void alterarSenhaComoAdmin(String uid, String novaSenha) throws FirebaseAuthException {
        if (estaVazio(uid)) {
            throw new IllegalArgumentException("Usuario nao autenticado.");
        }
        if (novaSenha == null || novaSenha.length() < 6) {
            throw new IllegalArgumentException("A nova senha deve ter ao menos 6 caracteres.");
        }

        UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(uid)
                .setPassword(novaSenha);
        FirebaseAuth.getInstance().updateUser(request);
    }

    public String verificarIdToken(String idToken) throws FirebaseAuthException {
        if (estaVazio(idToken)) {
            throw new IllegalArgumentException("Token de autenticacao ausente.");
        }
        return FirebaseAuth.getInstance().verifyIdToken(idToken).getUid();
    }

    private void validarEmailSenha(String email, String senha) {
        if (estaVazio(email)) {
            throw new IllegalArgumentException("Informe seu e-mail.");
        }
        if (senha == null || senha.length() < 6) {
            throw new IllegalArgumentException("Minimo de 6 caracteres.");
        }
    }

    private String normalizarUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    private boolean estaVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
