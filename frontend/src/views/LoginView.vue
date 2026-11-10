<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'

import {
  login,
  guardarToken
} from '../services/authService'

const router = useRouter()

const correo = ref('')
const password = ref('')
const mostrarPassword = ref(false)

const cargando = ref(false)
const error = ref('')
const mensaje = ref('')

const iniciarSesion = async () => {
  error.value = ''
  mensaje.value = ''

  if (!correo.value || !password.value) {
    error.value = 'Completa todos los campos.'
    return
  }

  cargando.value = true

  try {
    const respuesta = await login(
      correo.value,
      password.value
    )

    if (!respuesta.token) {
      error.value = 'El servidor no devolvió un token JWT.'
      return
    }

    // Guardar token JWT
    guardarToken(respuesta.token)

    mensaje.value = 'Inicio de sesión correcto.'

    // Por ahora, el login dirige al dashboard
    // del gerente. Después configuraremos
    // la navegación según el rol real del usuario.
    await router.replace('/dashboard/gerente')

  } catch (err) {
    if (err.response?.status === 401) {
      error.value = 'Correo o contraseña incorrectos.'
    } else if (err.response?.status === 403) {
      error.value = 'Acceso denegado.'
    } else if (!err.response) {
      error.value = 'No se pudo conectar con Spring Boot.'
    } else {
      error.value =
        err.response?.data?.mensaje ||
        err.response?.data?.message ||
        'Ocurrió un error al iniciar sesión.'
    }

  } finally {
    cargando.value = false
  }
}
</script>

<template>
  <div class="login-page">
    <div class="login-container">

      <!-- Panel izquierdo -->
      <div class="login-brand">
        <div class="brand-content">
          <div class="brand-icon">
            <i class="bi bi-cup-hot-fill"></i>
          </div>

          <h1>Sabor Gourmet</h1>
          <h2>ERP</h2>

          <p>
            La mejor manera de gestionar
            tu restaurante en un solo lugar.
          </p>

          <div class="brand-footer">
            <i class="bi bi-shield-check me-2"></i>
            Gestión segura e inteligente
          </div>
        </div>
      </div>

      <!-- Panel derecho -->
      <div class="login-form-section">
        <div class="login-form">

          <div class="mb-4">
            <span class="welcome-label">
              BIENVENIDO
            </span>

            <h3>Iniciar sesión</h3>

            <p class="subtitle">
              Ingresa tus credenciales para acceder
              al sistema.
            </p>
          </div>

          <form @submit.prevent="iniciarSesion">
          <div
            v-if="error"
            class="alert alert-danger"
            role="alert"
        >
            <i class="bi bi-exclamation-circle me-2"></i>
            {{ error }}
        </div>

        <div
            v-if="mensaje"
            class="alert alert-success"
            role="status"
        >
            <i class="bi bi-check-circle me-2"></i>
            {{ mensaje }}
        </div>

            <!-- Correo -->
            <div class="mb-4">
              <label class="form-label">
                Correo electrónico
              </label>

              <div class="input-group">
                <span class="input-group-text">
                  <i class="bi bi-envelope"></i>
                </span>

                <input
                  v-model="correo"
                  type="email"
                  class="form-control"
                  placeholder="ejemplo@correo.com"
                  autocomplete="username"
                  required
                />
              </div>
            </div>

            <!-- Contraseña -->
            <div class="mb-3">
              <label class="form-label">
                Contraseña
              </label>

              <div class="input-group">
                <span class="input-group-text">
                  <i class="bi bi-lock"></i>
                </span>

                <input
                  v-model="password"
                  :type="mostrarPassword ? 'text' : 'password'"
                  class="form-control"
                  placeholder="Ingresa tu contraseña"
                  autocomplete="current-password"
                  required
                />

                <button
                  type="button"
                  class="btn btn-outline-secondary"
                  @click="mostrarPassword = !mostrarPassword"
                  :aria-label="mostrarPassword ? 'Ocultar contraseña' : 'Mostrar contraseña'"
                >
                  <i
                    :class="mostrarPassword
                      ? 'bi bi-eye-slash'
                      : 'bi bi-eye'"
                  ></i>
                </button>
              </div>
            </div>

            <!-- Recordarme -->
            <div class="d-flex justify-content-between align-items-center mb-4">
              <div class="form-check">
                <input
                  class="form-check-input"
                  type="checkbox"
                  id="recordarme"
                />

                <label
                  class="form-check-label"
                  for="recordarme"
                >
                  Recordarme
                </label>
              </div>

              <span class="help-text">
                Acceso seguro
              </span>
            </div>

            <!-- Botón -->
            <button
  type="submit"
  class="btn-login w-100"
  :disabled="cargando"
>
  <span v-if="cargando">
    <span class="spinner-border spinner-border-sm me-2"></span>
    Iniciando sesión...
  </span>

  <span v-else>
    Iniciar sesión
    <i class="bi bi-arrow-right ms-2"></i>
  </span>
</button>

          </form>

          <div class="login-bottom">
            <p>
              © 2026 Sabor Gourmet ERP
            </p>
            <small>
              Sistema de Gestión de Restaurante
            </small>
          </div>

        </div>
      </div>

    </div>
  </div>
</template>

<style scoped>
.login-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f4f5f7;
  padding: 25px;
}

.login-container {
  display: grid;
  grid-template-columns: 1fr 1fr;
  width: 100%;
  max-width: 1100px;
  min-height: 650px;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.1);
}

.login-brand {
  background: linear-gradient(145deg, #181818, #393027);
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 50px;
  text-align: center;
}

.brand-content {
  max-width: 350px;
}

.brand-icon {
  font-size: 65px;
  color: #d9a35f;
  margin-bottom: 25px;
}

.login-brand h1 {
  font-size: 38px;
  font-weight: 800;
  margin-bottom: 0;
}

.login-brand h2 {
  font-size: 55px;
  font-weight: 800;
  color: #d9a35f;
}

.login-brand p {
  margin-top: 25px;
  color: #d2d2d2;
  font-size: 17px;
  line-height: 1.8;
}

.brand-footer {
  margin-top: 65px;
  font-size: 13px;
  color: #d9a35f;
}

.login-form-section {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 55px;
}

.login-form {
  width: 100%;
  max-width: 390px;
}

.welcome-label {
  color: #bd8140;
  font-size: 12px;
  font-weight: 800;
  letter-spacing: 3px;
}

.login-form h3 {
  font-size: 32px;
  font-weight: 800;
  color: #222;
  margin-top: 12px;
}

.subtitle {
  color: #888;
  font-size: 14px;
  line-height: 1.7;
}

.form-label {
  font-weight: 600;
  font-size: 14px;
  color: #444;
}

.input-group-text {
  background: #fafafa;
  border-color: #ddd;
  color: #888;
}

.form-control {
  padding: 13px;
  font-size: 14px;
}

.form-control:focus {
  border-color: #d9a35f;
  box-shadow: none;
}

.form-check-label,
.help-text {
  font-size: 13px;
  color: #777;
}

.form-check-input:checked {
  background-color: #bd8140;
  border-color: #bd8140;
}

.btn-login {
  background: #c58a47;
  color: white;
  border: none;
  padding: 15px;
  border-radius: 9px;
  font-weight: 700;
  transition: background 0.2s;
}

.btn-login:hover {
  background: #a96e30;
}

.login-bottom {
  margin-top: 65px;
  text-align: center;
  color: #aaa;
  font-size: 12px;
}

.login-bottom p {
  margin-bottom: 4px;
}

@media (max-width: 768px) {
  .login-container {
    grid-template-columns: 1fr;
  }

  .login-brand {
    padding: 30px;
  }

  .login-brand h1 {
    font-size: 28px;
  }

  .login-brand h2 {
    font-size: 38px;
  }

  .brand-footer {
    margin-top: 20px;
  }

  .login-form-section {
    padding: 35px 25px;
  }

  .login-page {
    padding: 12px;
  }
}
</style>