(function () {
  var firebaseConfig = {
    apiKey: "AIzaSyBYxax375bqx_tJgQ_bpAuCVl6PsB9fg0g",
    authDomain: "postly-a.firebaseapp.com",
    projectId: "postly-a",
    storageBucket: "postly-a.firebasestorage.app",
    messagingSenderId: "771729527718"
  };

  if (!window.firebase) {
    return;
  }

  if (!firebase.apps.length) {
    firebase.initializeApp(firebaseConfig);
  }

  var auth = firebase.auth();
  auth.languageCode = "pt-BR";
  var contextPath = document.body.getAttribute("data-context-path") || "";

  function endpoint(path) {
    return contextPath + path;
  }

  function post(path, data) {
    return fetch(endpoint(path), {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
      credentials: "same-origin",
      body: new URLSearchParams(data)
    }).then(function (response) {
      return response.json();
    });
  }

  function showMessage(form, message, type) {
    var alertType = type || "danger";
    var box = form.querySelector("[data-auth-message]");
    if (!box) {
      box = document.createElement("p");
      box.setAttribute("data-auth-message", "");
      form.insertBefore(box, form.firstChild);
    }
    box.className = "alert " + alertType;
    box.hidden = false;
    box.textContent = message;
  }

  function setBusy(form, busy) {
    var fields = form.querySelectorAll("button, input, textarea");
    for (var i = 0; i < fields.length; i += 1) {
      fields[i].disabled = busy;
    }
  }

  function assertOk(result) {
    if (!result.ok) {
      throw new Error(result.error || "Nao foi possivel concluir a acao.");
    }
    return result;
  }

  function emailFromLogin(login) {
    if (login.indexOf("@") >= 0) {
      return Promise.resolve(login);
    }

    return post("/auth/resolve-username", { username: login })
      .then(assertOk)
      .then(function (result) {
        return result.email;
      });
  }

  function openServerSession(user) {
    return user.getIdToken(true).then(function (idToken) {
      return post("/auth/session", { idToken: idToken });
    });
  }

  function redirectTo(result) {
    window.location.href = result.redirect || endpoint("/home");
  }

  function showGoogleProfileBox(form, result, idToken) {
    var profileBox = form.querySelector("[data-google-profile]");
    if (!profileBox) {
      showMessage(form, "Essa conta Google ainda nao tem perfil. Escolha um usuario para continuar.");
      return;
    }

    profileBox.hidden = false;
    profileBox.setAttribute("data-id-token", idToken);
    profileBox.querySelector("[data-google-photo]").src = result.photo || endpoint("/assets/img/avatar-demo.svg");
    profileBox.querySelector("[data-google-name]").textContent = result.name || "Conta Google";
    profileBox.querySelector("[data-google-email]").textContent = result.email || "";
    profileBox.querySelector("[name='googleName']").value = result.name || "";
    profileBox.querySelector("[name='googleUsername']").value = result.suggestedUsername || "";
    profileBox.querySelector("[name='googleUsername']").focus();
    showMessage(form, "Conta Google validada. Escolha um usuario para criar o perfil.", "success");
  }

  var loginForm = document.querySelector("[data-auth-login]");
  if (loginForm) {
    bindLoginForm(loginForm);
  }

  var registerForm = document.querySelector("[data-auth-register]");
  if (registerForm) {
    bindRegisterForm(registerForm);
  }

  var changePasswordForm = document.querySelector("[data-change-password]");
  if (changePasswordForm) {
    bindChangePasswordForm(changePasswordForm);
  }

  function bindLoginForm(form) {
    var googleButton = form.querySelector("[data-google-login]");
    var resetButton = form.querySelector("[data-reset-password]");
    var completeButton = form.querySelector("[data-complete-google]");

    form.addEventListener("submit", function (event) {
      event.preventDefault();
      setBusy(form, true);

      emailFromLogin(form.login.value.trim())
        .then(function (email) {
          return auth.signInWithEmailAndPassword(email, form.password.value);
        })
        .then(function (credential) {
          return openServerSession(credential.user).then(function (result) {
            return { result: result, user: credential.user };
          });
        })
        .then(function (data) {
          if (data.result.needsProfile === "true") {
            return data.user.getIdToken(true).then(function (idToken) {
              showGoogleProfileBox(form, data.result, idToken);
            });
          }
          assertOk(data.result);
          redirectTo(data.result);
        })
        .catch(function (error) {
          showMessage(form, error.message);
        })
        .finally(function () {
          setBusy(form, false);
        });
    });

    if (googleButton) {
      googleButton.addEventListener("click", function () {
        setBusy(form, true);
        auth.signOut()
          .then(function () {
            return auth.signInWithPopup(new firebase.auth.GoogleAuthProvider());
          })
          .then(function (credential) {
            return credential.user.getIdToken(true);
          })
          .then(function (idToken) {
            return post("/auth/session", { idToken: idToken }).then(function (result) {
              return { result: result, idToken: idToken };
            });
          })
          .then(function (data) {
            if (data.result.needsProfile === "true") {
              showGoogleProfileBox(form, data.result, data.idToken);
              return;
            }
            assertOk(data.result);
            redirectTo(data.result);
          })
          .catch(function (error) {
            showMessage(form, error.message);
          })
          .finally(function () {
            setBusy(form, false);
          });
      });
    }

    if (resetButton) {
      resetButton.addEventListener("click", function () {
        var login = form.login.value.trim();
        if (!login) {
          showMessage(form, "Informe e-mail ou usuario para redefinir a senha.");
          return;
        }

        emailFromLogin(login)
          .then(function (email) {
            return auth.sendPasswordResetEmail(email);
          })
          .then(function () {
            showMessage(form, "E-mail de redefinicao enviado.", "success");
          })
          .catch(function (error) {
            showMessage(form, error.message);
          });
      });
    }

    if (completeButton) {
      completeButton.addEventListener("click", function () {
        var profileBox = form.querySelector("[data-google-profile]");
        setBusy(form, true);

        post("/auth/google-complete", {
          idToken: profileBox.getAttribute("data-id-token"),
          username: profileBox.querySelector("[name='googleUsername']").value,
          name: profileBox.querySelector("[name='googleName']").value
        })
          .then(assertOk)
          .then(redirectTo)
          .catch(function (error) {
            showMessage(form, error.message);
          })
          .finally(function () {
            setBusy(form, false);
          });
      });
    }
  }

  function bindRegisterForm(form) {
    form.addEventListener("submit", function (event) {
      event.preventDefault();
      setBusy(form, true);

      var email = form.email.value.trim();
      var password = form.password.value;
      var confirmPassword = form.confirmPassword.value;
      var username = form.username.value.trim();
      var name = form.name.value.trim();
      var createdUser = null;

      if (password !== confirmPassword) {
        showMessage(form, "As senhas nao conferem.");
        setBusy(form, false);
        return;
      }

      post("/auth/check-account", { username: username, email: email })
        .then(assertOk)
        .then(function () {
          return auth.createUserWithEmailAndPassword(email, password);
        })
        .then(function (credential) {
          createdUser = credential.user;
          return credential.user.getIdToken(true);
        })
        .then(function (idToken) {
          return post("/auth/register", {
            idToken: idToken,
            username: username,
            name: name,
            email: email
          });
        })
        .then(function (result) {
          if (!result.ok && createdUser) {
            createdUser.delete()["catch"](function () {});
          }
          assertOk(result);
          redirectTo(result);
        })
        .catch(function (error) {
          showMessage(form, error.message);
        })
        .finally(function () {
          setBusy(form, false);
        });
    });
  }

  function bindChangePasswordForm(form) {
    form.addEventListener("submit", function (event) {
      event.preventDefault();
      setBusy(form, true);

      var user = auth.currentUser;
      var currentPassword = form.currentPassword.value;
      var newPassword = form.newPassword.value;
      var confirmPassword = form.confirmPassword.value;

      if (!user || !user.email) {
        showMessage(form, "Entre novamente para alterar a senha.");
        setBusy(form, false);
        return;
      }
      if (newPassword !== confirmPassword) {
        showMessage(form, "As senhas nao conferem.");
        setBusy(form, false);
        return;
      }

      var credential = firebase.auth.EmailAuthProvider.credential(user.email, currentPassword);
      user.reauthenticateWithCredential(credential)
        .then(function () {
          return user.updatePassword(newPassword);
        })
        .then(function () {
          form.reset();
          showMessage(form, "Senha alterada com sucesso.", "success");
        })
        .catch(function (error) {
          showMessage(form, error.message);
        })
        .finally(function () {
          setBusy(form, false);
        });
    });
  }
}());
