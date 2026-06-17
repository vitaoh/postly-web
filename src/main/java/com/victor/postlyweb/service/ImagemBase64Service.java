package com.victor.postlyweb.service;

import jakarta.servlet.http.Part;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Iterator;

public class ImagemBase64Service {

    private static final int MAX_DIMENSION = 800;
    private static final float JPEG_QUALITY = 0.70f;
    private static final String AVATAR_PADRAO = "assets/img/avatar-demo.svg";

    static {
        // No Linux (servidor sem interface grafica) o AWT tenta conectar ao X11 e falha
        // ao processar imagens. Em modo headless o redimensionamento funciona normalmente.
        System.setProperty("java.awt.headless", "true");
    }

    public String partParaBase64(Part part) throws IOException {
        if (part == null || part.getSize() == 0) {
            return null;
        }

        BufferedImage imagem = ImageIO.read(part.getInputStream());
        if (imagem == null) {
            throw new IllegalArgumentException("Arquivo de imagem invalido.");
        }

        BufferedImage reduzida = reduzir(imagem);
        byte[] bytes = jpegBytes(reduzida);
        return Base64.getEncoder().encodeToString(bytes);
    }

    public String partParaBase64Bruto(Part part, int maxBytes) throws IOException {
        if (part == null || part.getSize() == 0) {
            return null;
        }
        if (part.getSize() > maxBytes) {
            throw new IllegalArgumentException("Arquivo muito grande. Limite de "
                    + (maxBytes / 1024) + " KB.");
        }
        byte[] bytes = part.getInputStream().readAllBytes();
        return Base64.getEncoder().encodeToString(bytes);
    }

    public String paraDataUri(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        if (valor.startsWith("assets/") || valor.startsWith("http://") || valor.startsWith("https://")
                || valor.startsWith("data:")) {
            return valor;
        }
        return "data:image/jpeg;base64," + valor;
    }

    public String src(String contextPath, String valor) {
        String src = paraDataUri(valor);
        if (src == null) {
            return caminhoAsset(contextPath, AVATAR_PADRAO);
        }
        if (src.startsWith("assets/")) {
            return caminhoAsset(contextPath, src);
        }
        return src;
    }

    private String caminhoAsset(String contextPath, String asset) {
        String base = contextPath == null ? "" : contextPath;
        return base + "/" + asset;
    }

    private BufferedImage reduzir(BufferedImage imagem) {
        int largura = imagem.getWidth();
        int altura = imagem.getHeight();
        if (largura <= MAX_DIMENSION && altura <= MAX_DIMENSION) {
            return paraRgb(imagem, largura, altura);
        }

        double proporcao = Math.min((double) MAX_DIMENSION / largura, (double) MAX_DIMENSION / altura);
        int novaLargura = Math.max(1, (int) Math.round(largura * proporcao));
        int novaAltura = Math.max(1, (int) Math.round(altura * proporcao));

        BufferedImage reduzida = new BufferedImage(novaLargura, novaAltura, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = reduzida.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.drawImage(imagem, 0, 0, novaLargura, novaAltura, null);
        graphics.dispose();
        return reduzida;
    }

    private BufferedImage paraRgb(BufferedImage imagem, int largura, int altura) {
        if (imagem.getType() == BufferedImage.TYPE_INT_RGB) {
            return imagem;
        }

        BufferedImage rgb = new BufferedImage(largura, altura, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = rgb.createGraphics();
        graphics.drawImage(imagem, 0, 0, null);
        graphics.dispose();
        return rgb;
    }

    private byte[] jpegBytes(BufferedImage imagem) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
        if (!writers.hasNext()) {
            throw new IOException("Nenhum writer JPEG disponivel.");
        }

        ImageWriter writer = writers.next();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ImageOutputStream output = ImageIO.createImageOutputStream(baos)) {
            writer.setOutput(output);
            ImageWriteParam params = writer.getDefaultWriteParam();
            if (params.canWriteCompressed()) {
                params.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                params.setCompressionQuality(JPEG_QUALITY);
            }
            writer.write(null, new IIOImage(imagem, null, null), params);
            return baos.toByteArray();
        } finally {
            writer.dispose();
        }
    }
}
