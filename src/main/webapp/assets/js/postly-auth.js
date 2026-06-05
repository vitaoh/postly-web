import { initializeApp } from "https://www.gstatic.com/firebasejs/10.12.5/firebase-app.js";
import {
  EmailAuthProvider,
  GoogleAuthProvider,
  createUserWithEmailAndPassword,
  getAuth,
  reauthenticateWithCredential,
  sendPasswordResetEmail,
  signInWithEmailAndPassword,
  signInWithPopup,
  signOut,
  updatePassword
} from "https://www.gstatic.com/firebasejs/10.12.5/firebase-auth.js";

const firebaseConfig = {
  apiKey: "AIzaSyBYxax375bqx_tJgQ_bpAuCVl6PsB9fg0g",
  authDomain: "postly-a.firebaseapp.com",
  projectId: "postly-a",
  storageBucket: "postly-a.firebasestorage.app",
  messagingSenderId: "771729527718"
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
auth.languageCode = "pt-BR";

const contextPath = document.body.dataset.contextPath || "";

function endpoint(path) {
  return `${contextPath}${path}`;
}

async function post(path, data) {
  const response = await fetch(endpoint(path), {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
    credentials: "same-origin",
    body: new URLSearchParams(data)
  });
  const json = await response.json();
  return json;
}

function showMessage(form, message, type = "danger") {
  let box = form.querySelector("[data-auth-message]");
  if (!box) {
    box = document.createElement("p");
    box.dataset.authMessage = "";
    form.prepend(box);
  }
  box.className = `alert ${type}`;
  box.hidden = false;
  box.textContent = message;
}

function setBusy(form, busy) {
  form.querySelectorAll("button, input, textarea").forEach((element) => {
    element.disabled = busy;
  });
}

function assertOk(result) {
  if (!result.ok) {
    throw new Error(result.error || "Nao foi possivel concluir a acao.");
  }
  return result;
}

async function emailFromLogin(login) {
  if (login.includes("@")) {
    return login;
  }

  const result = assertOk(await post("/auth/resolve-username", { username: login }));
  return result.email;
}

async function openServerSession(user) {
  const idToken = await user.getIdToken(true);
  return post("/auth/session", { idToken });
}

function redirectTo(result) {
  window.location.href = result.redirect || endpoint("/home");
}

function showGoogleProfileBox(form, result, idToken) {
  const profileBox = form.querySelector("[data-google-profile]");
  if (!profileBox) {
    showMessage(form, "Esse Google ainda nao tem perfil no Firestore. Informe um usuario para completar.");
    return;
  }

  profileBox.hidden = false;
  profileBox.dataset.idToken = idToken;
  profileBox.querySelector("[name='googleName']").value = result.name || "";
  profileBox.querySelector("[name='googleUsername']").value = result.suggestedUsername || "";
  profileBox.querySelector("[name='googleUsername']").focus();
  showMessage(form, "Conta Google validada. Escolha um usuario para criar o perfil.", "success");
}

const loginForm = document.querySelector("[data-auth-login]");
if (loginForm) {
  const googleButton = loginForm.querySelector("[data-google-login]");
  const resetButton = loginForm.querySelector("[data-reset-password]");
  const completeButton = loginForm.querySelector("[data-complete-google]");

  loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    setBusy(loginForm, true);

    try {
      const login = loginForm.login.value.trim();
      const password = loginForm.password.value;
      const email = await emailFromLogin(login);
      const credential = await signInWithEmailAndPassword(auth, email, password);
      const result = await openServerSession(credential.user);

      if (result.needsProfile === "true") {
        const token = await credential.user.getIdToken(true);
        showGoogleProfileBox(loginForm, result, token);
        return;
      }

      assertOk(result);
      redirectTo(result);
    } catch (error) {
      showMessage(loginForm, error.message);
    } finally {
      setBusy(loginForm, false);
    }
  });

  googleButton?.addEventListener("click", async () => {
    setBusy(loginForm, true);

    try {
      await signOut(auth);
      const credential = await signInWithPopup(auth, new GoogleAuthProvider());
      const idToken = await credential.user.getIdToken(true);
      const result = await post("/auth/session", { idToken });

      if (result.needsProfile === "true") {
        showGoogleProfileBox(loginForm, result, idToken);
        return;
      }

      assertOk(result);
      redirectTo(result);
    } catch (error) {
      showMessage(loginForm, error.message);
    } finally {
      setBusy(loginForm, false);
    }
  });

  resetButton?.addEventListener("click", async () => {
    try {
      const login = loginForm.login.value.trim();
      if (!login) {
        throw new Error("Informe e-mail ou usuario para redefinir a senha.");
      }

      const email = await emailFromLogin(login);
      await sendPasswordResetEmail(auth, email);
      showMessage(loginForm, "E-mail de redefinicao enviado.", "success");
    } catch (error) {
      showMessage(loginForm, error.message);
    }
  });

  completeButton?.addEventListener("click", async () => {
    const profileBox = loginForm.querySelector("[data-google-profile]");
    setBusy(loginForm, true);

    try {
      const result = assertOk(await post("/auth/google-complete", {
        idToken: profileBox.dataset.idToken,
        username: profileBox.querySelector("[name='googleUsername']").value,
        name: profileBox.querySelector("[name='googleName']").value
      }));
      redirectTo(result);
    } catch (error) {
      showMessage(loginForm, error.message);
    } finally {
      setBusy(loginForm, false);
    }
  });
}

const registerForm = document.querySelector("[data-auth-register]");
if (registerForm) {
  registerForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    setBusy(registerForm, true);

    try {
      const email = registerForm.email.value.trim();
      const password = registerForm.password.value;
      const confirmPassword = registerForm.confirmPassword.value;
      const username = registerForm.username.value.trim();
      const name = registerForm.name.value.trim();

      if (password !== confirmPassword) {
        throw new Error("As senhas nao conferem.");
      }
      assertOk(await post("/auth/check-username", { username }));

      const credential = await createUserWithEmailAndPassword(auth, email, password);
      const idToken = await credential.user.getIdToken(true);
      const result = await post("/auth/register", { idToken, username, name, email });

      if (!result.ok) {
        await credential.user.delete().catch(() => {});
      }

      assertOk(result);
      redirectTo(result);
    } catch (error) {
      showMessage(registerForm, error.message);
    } finally {
      setBusy(registerForm, false);
    }
  });
}

const changePasswordForm = document.querySelector("[data-change-password]");
if (changePasswordForm) {
  changePasswordForm.addEventListener("submit", async (event) => {
    event.preventDefault();
    setBusy(changePasswordForm, true);

    try {
      const currentPassword = changePasswordForm.currentPassword.value;
      const newPassword = changePasswordForm.newPassword.value;
      const confirmPassword = changePasswordForm.confirmPassword.value;
      const user = auth.currentUser;

      if (!user || !user.email) {
        throw new Error("Entre novamente para alterar a senha.");
      }
      if (newPassword !== confirmPassword) {
        throw new Error("As senhas nao conferem.");
      }

      const credential = EmailAuthProvider.credential(user.email, currentPassword);
      await reauthenticateWithCredential(user, credential);
      await updatePassword(user, newPassword);
      changePasswordForm.reset();
      showMessage(changePasswordForm, "Senha alterada com sucesso.", "success");
    } catch (error) {
      showMessage(changePasswordForm, error.message);
    } finally {
      setBusy(changePasswordForm, false);
    }
  });
}
