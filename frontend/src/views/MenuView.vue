<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { obtenerPlatos } from '../services/menuService'
import { cerrarSesion } from '../services/authService'

const router = useRouter()

const platos = ref([])
const cargando = ref(true)
const error = ref('')
const busqueda = ref('')

const cargarPlatos = async () => {
  cargando.value = true
  error.value = ''

  try {
    const respuesta = await obtenerPlatos()

    platos.value = Array.isArray(respuesta)
      ? respuesta
      : Array.isArray(respuesta?.data)
        ? respuesta.data
        : []

  } catch (err) {
    if (err.response?.status === 401) {
      cerrarSesion()
      router.replace('/login')
      return
    }

    if (err.response?.status === 403) {
      error.value = 'No tienes permisos para consultar los platos.'
    } else {
      error.value = 'No se pudieron cargar los platos.'
    }
  } finally {
    cargando.value = false
  }
}

import { computed } from 'vue'

const platosFiltrados = computed(() => {
  const texto = busqueda.value.toLowerCase().trim()

  return platos.value.filter((plato) =>
    String(plato.nombre ?? '').toLowerCase().includes(texto) ||
    String(plato.categoria ?? '').toLowerCase().includes(texto)
  )
})

const salir = () => {
  cerrarSesion()
  router.replace('/login')
}

onMounted(cargarPlatos)
</script>

<template>
  <div class="menu-page">

    <aside class="sidebar">
      <div class="brand">
        <i class="bi bi-cup-hot-fill"></i>
        <h4>Sabor Gourmet</h4>
        <span>ERP</span>
      </div>

      <p class="section-label">ADMINISTRACIÓN</p>

      <nav class="nav-links">
        <router-link to="/dashboard/gerente">
          <i class="bi bi-grid-1x2-fill"></i>
          Dashboard
        </router-link>

        <router-link to="/menus" class="active">
          <i class="bi bi-journal-text"></i>
          Menús y Platos
        </router-link>
      </nav>

      <button class="logout" @click="salir">
        <i class="bi bi-box-arrow-left"></i>
        Cerrar sesión
      </button>
    </aside>

    <main class="content">
      <header class="page-header">
        <div>
          <h1>Menús y Platos</h1>
          <p>Administra los platos disponibles en el restaurante.</p>
        </div>

        <button
          class="btn btn-warning"
          @click="cargarPlatos"
          :disabled="cargando"
        >
          <i class="bi bi-arrow-clockwise me-2"></i>
          Actualizar
        </button>
      </header>

      <section class="table-card">
        <div class="table-heading">
          <h5>Listado de platos</h5>

          <div class="search-box">
            <i class="bi bi-search"></i>
            <input
              v-model="busqueda"
              type="search"
              placeholder="Buscar plato o categoría..."
              aria-label="Buscar platos"
            />
          </div>
        </div>

        <div v-if="cargando" class="text-center py-5">
          <div class="spinner-border text-warning"></div>
          <p class="mt-3 text-muted">Cargando platos...</p>
        </div>

        <div v-else-if="error" class="alert alert-danger m-4">
          {{ error }}
        </div>

        <div v-else-if="platosFiltrados.length === 0"
             class="empty-state">
          <i class="bi bi-journal-x"></i>
          <h5>No se encontraron platos</h5>
          <p>
            {{
              busqueda
                ? 'Prueba con otro nombre o categoría.'
                : 'Todavía no hay platos registrados.'
            }}
          </p>
        </div>

        <div v-else class="table-responsive">
          <table class="table align-middle mb-0">
            <thead>
              <tr>
                <th>ID</th>
                <th>Nombre</th>
                <th>Categoría</th>
                <th>Descripción</th>
                <th>Precio</th>
                <th>Disponibilidad</th>
              </tr>
            </thead>

            <tbody>
              <tr v-for="plato in platosFiltrados" :key="plato.id">
                <td>#{{ plato.id }}</td>

                <td class="fw-semibold">
                  {{ plato.nombre }}
                </td>

                <td>{{ plato.categoria || 'Sin categoría' }}</td>

                <td class="description">
                  {{ plato.descripcion || 'Sin descripción' }}
                </td>

                <td class="price">
                  S/ {{ Number(plato.precio ?? 0).toFixed(2) }}
                </td>

                <td>
                  <span
                    class="status"
                    :class="plato.disponible ? 'available' : 'unavailable'"
                  >
                    {{ plato.disponible ? 'Disponible' : 'No disponible' }}
                  </span>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </section>
    </main>
  </div>
</template>

<style scoped>
.menu-page {
  display: flex;
  min-height: 100vh;
  background: #f5f6fa;
}

.sidebar {
  width: 260px;
  flex-shrink: 0;
  background: #201d1a;
  color: white;
  padding: 30px 20px;
  display: flex;
  flex-direction: column;
}

.brand {
  text-align: center;
  margin-bottom: 45px;
}

.brand i {
  font-size: 38px;
  color: #d9a35f;
}

.brand h4 {
  font-weight: 800;
  margin: 12px 0 0;
}

.brand span {
  color: #d9a35f;
  font-weight: bold;
  letter-spacing: 3px;
}

.section-label {
  color: #888;
  font-size: 11px;
  letter-spacing: 2px;
}

.nav-links {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.nav-links a {
  display: flex;
  align-items: center;
  gap: 14px;
  color: #bbb;
  padding: 14px;
  border-radius: 9px;
  text-decoration: none;
}

.nav-links a.active,
.nav-links a.router-link-active {
  background: #c58a47;
  color: white;
}

.logout {
  margin-top: auto;
  background: transparent;
  border: 1px solid #655a50;
  color: #ddd;
  padding: 13px;
  border-radius: 8px;
  text-align: left;
}

.content {
  flex: 1;
  min-width: 0;
  padding: 35px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  margin-bottom: 30px;
}

.page-header h1 {
  font-size: 28px;
  font-weight: 800;
}

.page-header p {
  color: #888;
}

.table-card {
  background: white;
  border-radius: 14px;
  overflow: hidden;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.04);
}

.table-heading {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
  padding: 25px;
}

.table-heading h5 {
  font-weight: 700;
  margin: 0;
}

.search-box {
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid #ddd;
  border-radius: 9px;
  padding: 10px 15px;
}

.search-box input {
  border: none;
  outline: none;
  min-width: 220px;
}

table th {
  background: #fafafa;
  color: #777;
  font-size: 13px;
  padding: 18px;
}

table td {
  padding: 18px;
}

.description {
  max-width: 260px;
  color: #777;
}

.price {
  font-weight: 700;
  color: #a96e30;
}

.status {
  display: inline-block;
  padding: 7px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 700;
}

.available {
  background: #e4f7ec;
  color: #24935a;
}

.unavailable {
  background: #ffebeb;
  color: #d9534f;
}

.empty-state {
  text-align: center;
  padding: 65px 20px;
  color: #888;
}

.empty-state i {
  font-size: 45px;
  color: #c58a47;
}

.empty-state h5 {
  margin-top: 15px;
  color: #333;
}

@media (max-width: 768px) {
  .menu-page {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }

  .content {
    padding: 20px;
  }

  .page-header,
  .table-heading {
    flex-direction: column;
    align-items: stretch;
  }

  .search-box input {
    min-width: 0;
    width: 100%;
  }
}
</style>