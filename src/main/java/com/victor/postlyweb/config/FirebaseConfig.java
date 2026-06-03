package com.victor.postlyweb.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.stream.Stream;

public final class FirebaseConfig {

    private static final String CREDENTIALS_ENV = "GOOGLE_APPLICATION_CREDENTIALS";
    private static final String CREDENTIALS_PROPERTY = "firebase.serviceAccountKey";
    private static final String[] CREDENTIALS_LOCAL_PATHS = {
            "private/serviceAccountKey.json",
            "serviceAccountKey.json"
    };

    private static volatile FirebaseApp app;

    private FirebaseConfig() {
    }

    public static FirebaseApp getApp() throws IOException {
        FirebaseApp currentApp = app;
        if (currentApp != null) {
            return currentApp;
        }

        synchronized (FirebaseConfig.class) {
            if (app == null) {
                app = inicializarFirebase();
            }

            return app;
        }
    }

    public static Firestore getFirestore() throws IOException {
        getApp();
        return FirestoreClient.getFirestore();
    }

    private static FirebaseApp inicializarFirebase() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        try (InputStream serviceAccount = abrirCredenciais()) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            return FirebaseApp.initializeApp(options);
        }
    }

    private static boolean estaVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }

    private static InputStream abrirCredenciais() throws IOException {
        String caminhoCredenciais = System.getProperty(CREDENTIALS_PROPERTY);
        if (estaVazio(caminhoCredenciais)) {
            caminhoCredenciais = System.getenv(CREDENTIALS_ENV);
        }

        if (!estaVazio(caminhoCredenciais)) {
            return new FileInputStream(caminhoCredenciais);
        }

        Path diretorioAtual = Path.of(System.getProperty("user.dir"));
        for (String caminhoLocal : CREDENTIALS_LOCAL_PATHS) {
            Path candidato = diretorioAtual.resolve(caminhoLocal);
            if (Files.exists(candidato)) {
                return Files.newInputStream(candidato);
            }
        }

        Optional<Path> credencialPrivada = buscarCredencialEmPrivate(diretorioAtual);
        if (credencialPrivada.isPresent()) {
            return Files.newInputStream(credencialPrivada.get());
        }

        throw new IllegalStateException(
                "Coloque o JSON da chave privada do Firebase em private/ ou configure "
                        + "firebase.serviceAccountKey com o caminho absoluto do arquivo.");
    }

    private static Optional<Path> buscarCredencialEmPrivate(Path diretorioAtual) throws IOException {
        Path privateDir = diretorioAtual.resolve("private");
        if (!Files.isDirectory(privateDir)) {
            return Optional.empty();
        }

        try (Stream<Path> arquivos = Files.list(privateDir)) {
            return arquivos
                    .filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".json"))
                    .filter(path -> !path.getFileName().toString().equals("serviceAccountKey.example.json"))
                    .findFirst();
        }
    }
}
