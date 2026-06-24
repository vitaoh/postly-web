package com.victor.postlyweb.service;

import com.victor.postlyweb.modelo.Usuario;
import com.victor.postlyweb.persistencia.firebase.FirebaseUsuarioDAO;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class PostlyUsuarioService {

    private final FirebaseUsuarioDAO usuarioDAO;

    public PostlyUsuarioService() throws IOException {
        this.usuarioDAO = new FirebaseUsuarioDAO();
    }

    public Usuario salvarComUsernameUnico(Usuario usuario) throws ExecutionException, InterruptedException {
        return salvarComUsernameEEmailUnicos(usuario);
    }

    public Usuario salvarComUsernameEEmailUnicos(Usuario usuario) throws ExecutionException, InterruptedException {
        validarUsuario(usuario);
        usuario.setUsername(normalizarUsername(usuario.getUsername()));
        usuario.setEmail(normalizarEmail(usuario.getEmail()));

        Optional<Usuario> existente = usuarioDAO.buscarPorUsername(usuario.getUsername());
        if (existente.isPresent() && !existente.get().getUid().equals(usuario.getUid())) {
            throw new IllegalArgumentException("Nome de usuario ja esta em uso.");
        }

        if (!estaVazio(usuario.getEmail())) {
            Optional<Usuario> emailExistente = usuarioDAO.buscarPorEmail(usuario.getEmail());
            if (emailExistente.isPresent() && !emailExistente.get().getUid().equals(usuario.getUid())) {
                throw new IllegalArgumentException("E-mail ja esta em uso.");
            }
        }

        return usuarioDAO.salvar(usuario);
    }

    public Optional<Usuario> buscarPorUid(String uid) throws ExecutionException, InterruptedException {
        if (estaVazio(uid)) {
            return Optional.empty();
        }
        return usuarioDAO.buscarPorUid(uid);
    }

    public Optional<Usuario> buscarPorUsername(String username) throws ExecutionException, InterruptedException {
        if (estaVazio(username)) {
            return Optional.empty();
        }
        return usuarioDAO.buscarPorUsername(username);
    }

    public Optional<Usuario> buscarPorEmail(String email) throws ExecutionException, InterruptedException {
        if (estaVazio(email)) {
            return Optional.empty();
        }
        return usuarioDAO.buscarPorEmail(email);
    }

    public List<String> listarSeguindoIds(String uid) throws ExecutionException, InterruptedException {
        if (estaVazio(uid)) {
            return List.of();
        }
        return usuarioDAO.listarSeguindoIds(uid);
    }

    public boolean alternarSeguir(String usuarioAtualUid, String usuarioAlvoUid)
            throws ExecutionException, InterruptedException {
        if (estaVazio(usuarioAtualUid) || estaVazio(usuarioAlvoUid) || usuarioAtualUid.equals(usuarioAlvoUid)) {
            throw new IllegalArgumentException("Usuarios invalidos para seguir.");
        }

        boolean jaSegue = usuarioDAO.estaSeguindo(usuarioAtualUid, usuarioAlvoUid);
        if (jaSegue) {
            usuarioDAO.deixarDeSeguir(usuarioAtualUid, usuarioAlvoUid);
            return false;
        }

        usuarioDAO.seguir(usuarioAtualUid, usuarioAlvoUid);
        return true;
    }

    private void validarUsuario(Usuario usuario) {
        if (usuario == null || estaVazio(usuario.getUid())) {
            throw new IllegalArgumentException("Usuario invalido.");
        }
        if (estaVazio(usuario.getName())) {
            throw new IllegalArgumentException("Informe seu nome.");
        }
        if (estaVazio(usuario.getUsername())) {
            throw new IllegalArgumentException("Informe um nome de usuario.");
        }
        if (normalizarUsername(usuario.getUsername()).length() < 3) {
            throw new IllegalArgumentException("Minimo de 3 caracteres.");
        }
    }

    private String normalizarUsername(String username) {
        return username == null ? "" : username.trim().toLowerCase();
    }

    private String normalizarEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private boolean estaVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
