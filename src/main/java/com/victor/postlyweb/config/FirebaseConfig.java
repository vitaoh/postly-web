package com.victor.postlyweb.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.FileInputStream;
import java.io.IOException;

public final class FirebaseConfig {

    private static final String CREDENTIALS_ENV = "GOOGLE_APPLICATION_CREDENTIALS";
    private static final String CREDENTIALS_PROPERTY = "firebase.serviceAccountKey";

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

        String caminhoCredenciais = System.getProperty(CREDENTIALS_PROPERTY);
        if (estaVazio(caminhoCredenciais)) {
            caminhoCredenciais = System.getenv(CREDENTIALS_ENV);
        }

        if (estaVazio(caminhoCredenciais)) {
            throw new IllegalStateException(
                    "Configure a variavel GOOGLE_APPLICATION_CREDENTIALS ou a propriedade firebase.serviceAccountKey "
                            + "com o caminho do arquivo serviceAccountKey.json.");
        }

        try (FileInputStream serviceAccount = new FileInputStream(caminhoCredenciais)) {
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            return FirebaseApp.initializeApp(options);
        }
    }

    private static boolean estaVazio(String valor) {
        return valor == null || valor.trim().isEmpty();
    }
}
