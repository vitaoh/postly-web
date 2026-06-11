<div align="center">

<img src="src/main/webapp/assets/img/logo.png" alt="Postly" width="96" height="96">

# Postly Web

**Rede social estilo Twitter — versão web do app Postly Mobile, compartilhando o mesmo Firebase.**

![Java](https://img.shields.io/badge/Java-17+-orange?logo=openjdk&logoColor=white)
![Jakarta EE](https://img.shields.io/badge/Jakarta%20EE-10-blue)
![Tomcat](https://img.shields.io/badge/Tomcat-10.1-yellow?logo=apachetomcat&logoColor=white)
![Firebase](https://img.shields.io/badge/Firebase-Firestore-ffca28?logo=firebase&logoColor=black)
![Maven](https://img.shields.io/badge/Maven-build-red?logo=apachemaven)

</div>

---

## ✨ Funcionalidades

- 📝 **Feed de publicações** com texto, foto e localização
- 🔄 **Paginação por cursor** estilo Twitter ("Carregar mais" sem recarregar a página)
- ❤️ **Curtidas** com estado visual (curtido/não curtido) sem sair da tela
- 💬 **Comentários** com exclusão pelo autor ou dono do post
- 👤 **Perfis** com seguidores/seguindo, edição de nome, usuário e foto
- 🔍 **Busca** por descrição, cidade ou usuário + filtro "Para você" / "Seguindo"
- 📨 **Chat em tempo real** entre usuários, compatível com o app mobile:
  - mensagens de texto
  - 📷 envio de **fotos**
  - 🎤 gravação e envio de **áudios** direto do navegador (MediaRecorder)
- 🔐 **Autenticação Firebase** (e-mail/senha e Google)
- 🌗 **Tema claro e escuro** automáticos, seguindo o sistema

## 🏛️ Arquitetura em camadas

```
┌─────────────────────────────────────────────┐
│  View (JSP + JSTL)          webapp/WEB-INF  │
├─────────────────────────────────────────────┤
│  Controller (Servlet)       controllers/    │
├─────────────────────────────────────────────┤
│  Service (regras de negócio) service/       │
├─────────────────────────────────────────────┤
│  DAO (persistência)         persistencia/   │
├─────────────────────────────────────────────┤
│  Modelo (entidades)         modelo/         │
└─────────────────────────────────────────────┘
                     │
                     ▼
            ☁️ Cloud Firestore
```

| Conceito | Como foi aplicado |
|---|---|
| Separação de Interesses | Cada camada tem responsabilidade única (view, controle, negócio, persistência) |
| Persistência | Cloud Firestore (NoSQL) via Firebase Admin SDK |
| Conexão | `FirebaseConfig` singleton thread-safe reutiliza a conexão gRPC do Firestore |
| Transações | `runTransaction` (curtidas) e `WriteBatch` atômico (mensagens do chat) |
| Paginação | Cursor por `timestamp` com `startAfter` + botão "Carregar mais" |
| Upload de arquivos | Multipart (`web.xml`) com redimensionamento e compressão JPEG no servidor |

## 🛠️ Tecnologias

- **Java 17** + **Jakarta Servlet 6 / JSP 3.1 / JSTL 3**
- **Apache Tomcat 10.1**
- **Maven** (empacotamento WAR)
- **Firebase Admin SDK 9** (Firestore + verificação de tokens)
- **Firebase Auth** no front-end (e-mail/senha e Google)
- CSS puro com variáveis (tema claro/escuro), sem frameworks

## 🚀 Como rodar

### Pré-requisitos

- JDK 17+
- Apache Tomcat **10.1+** (precisa ser 10+, o projeto usa `jakarta.*`)
- Maven (ou Eclipse for Enterprise Java)

### Passo a passo

```bash
git clone <url-do-repositorio>
cd postly-web
```

**1. Credencial do Firebase** *(não vem no clone — é secreta)*

Baixe a chave da conta de serviço no [Console do Firebase](https://console.firebase.google.com)
(`Configurações do projeto → Contas de serviço → Gerar nova chave privada`) e salve em:

```
private/serviceAccountKey.json
```

> Qualquer `.json` dentro de `private/` é aceito. Alternativa: variável de ambiente
> `GOOGLE_APPLICATION_CREDENTIALS` apontando para o arquivo.

**2a. Rodar pelo Eclipse**

1. `File → Import → Maven → Existing Maven Projects`
2. Aba `Servers` → `New → Server → Tomcat v10.1`
3. Botão direito no projeto → `Run As → Run on Server`

**2b. Rodar pela linha de comando**

```bash
mvn clean package
cp target/postly-web-*.war $TOMCAT_HOME/webapps/postly-web.war
$TOMCAT_HOME/bin/startup.sh   # Windows: startup.bat
```

Acesse: **http://localhost:8080/postly-web/**

## 📁 Estrutura do projeto

```
postly-web/
├── pom.xml
├── private/                         # 🔑 chave do Firebase (fora do git)
└── src/main/
    ├── java/com/victor/postlyweb/
    │   ├── config/                  # FirebaseConfig (singleton)
    │   ├── controllers/             # PostlyPageController (rotas GET/POST)
    │   ├── modelo/                  # Post, Usuario, ChatMessage, ChatThread...
    │   ├── persistencia/firebase/   # DAOs do Firestore
    │   └── service/                 # regras de negócio, auth, imagens, tempo
    └── webapp/
        ├── assets/                  # CSS, JS e imagens
        └── WEB-INF/views/           # páginas JSP
```

## 📱 Projeto irmão

O **[Postly Mobile](../postly-mobile)** (Android/Kotlin) usa o mesmo projeto Firebase:
posts, perfis, curtidas e conversas (incluindo fotos e áudios) sincronizam entre web e mobile.

---

<div align="center">
Feito com 💜 por <a href="https://github.com/victorherculini">Victor Herculini</a>
</div>
