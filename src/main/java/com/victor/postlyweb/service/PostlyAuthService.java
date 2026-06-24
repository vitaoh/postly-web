package com.victor.postlyweb.service;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

public class PostlyAuthService {

    public FirebaseToken verificarToken(String idToken) throws FirebaseAuthException {
        if (estaVazio(idToken)) {
            throw new IllegalArgumentException("Token de autenticacao ausente.");
        }
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }

    private boolean estaVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
