package com.victor.postlyweb.controllers;

import com.victor.postlyweb.service.PostlyDemoService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet(urlPatterns = {
        "/welcome",
        "/criar-conta",
        "/entrar",
        "/home",
        "/postar",
        "/editar-post",
        "/mensagens",
        "/chat",
        "/perfil",
        "/configuracoes",
        "/mudar-senha",
        "/post"
})
public class PostlyPageController extends HttpServlet {

    private final PostlyDemoService demoService = new PostlyDemoService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        carregarDadosComuns(request);
        request.getRequestDispatcher(viewPara(request.getServletPath())).forward(request, response);
    }

    private void carregarDadosComuns(HttpServletRequest request) {
        request.setAttribute("usuario", demoService.usuarioAtual());
        request.setAttribute("outroUsuario", demoService.outroUsuario());
        request.setAttribute("posts", demoService.posts());
        request.setAttribute("postPrincipal", demoService.posts().get(0));
        request.setAttribute("comentarios", demoService.comentarios());
        request.setAttribute("conversas", demoService.conversas());
        request.setAttribute("mensagens", demoService.mensagens());
    }

    private String viewPara(String path) {
        return switch (path) {
            case "/criar-conta" -> "/WEB-INF/views/criar-conta.jsp";
            case "/entrar" -> "/WEB-INF/views/entrar.jsp";
            case "/home" -> "/WEB-INF/views/home.jsp";
            case "/postar" -> "/WEB-INF/views/postar.jsp";
            case "/editar-post" -> "/WEB-INF/views/editar-post.jsp";
            case "/mensagens" -> "/WEB-INF/views/mensagens.jsp";
            case "/chat" -> "/WEB-INF/views/chat.jsp";
            case "/perfil" -> "/WEB-INF/views/perfil.jsp";
            case "/configuracoes" -> "/WEB-INF/views/configuracoes.jsp";
            case "/mudar-senha" -> "/WEB-INF/views/mudar-senha.jsp";
            case "/post" -> "/WEB-INF/views/post.jsp";
            default -> "/WEB-INF/views/welcome.jsp";
        };
    }
}
