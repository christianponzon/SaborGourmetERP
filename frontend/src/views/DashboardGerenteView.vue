<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import api, { cerrarSesion } from '../services/authService'

const router = useRouter()

const cargando = ref(true)
const error = ref('')
const datos = ref(null)

const obtenerDashboard = async () => {
  cargando.value = true
  error.value = ''

  try {
    const response = await api.get('/dashboard/gerente')
    datos.value = response.data
  } catch (err) {
    if (err.response?.status === 401) {
      cerrarSesion()
      router.replace('/login')
      return
    }

    if (err.response?.status === 403) {
      error.value = 'No tienes permisos para acceder a este dashboard.'
    } else {
      error.value = 'No se pudieron cargar las estadísticas.'
    }
  } finally {
    cargando.value = false
  }
}

const salir = () => {
  cerrarSesion()
  router.replace('/login')
}

onMounted(obtenerDashboard)
</script>

<template>
  <div class="dashboard">

    <!-- Menú lateral -->
    <aside class="sidebar">
      <div class="logo">
        <i class="bi bi-cup-hot-fill"></i>
        <h2>Sabor Gourmet</h2>
        <span>ERP</span>
      </div>

      <div class="menu-title">ADMINISTRACIÓN</div>

      <nav class="menu">
        <a class="active" href="#">
          <i class="bi bi-grid-1x2-fill"></i>
          Dashboard
        </a>

        <div class="menu-item">
          <i class="bi bi-receipt"></i>
          Pedidos
        </div>

        <div class="menu-item">
          <i class="bi bi-table"></i>
          Mesas
        </div>

        <div class="menu-item">
          <i class="bi bi-journal-text"></i>
          Menús
        </div>

        <div class="menu-item">
          <i class="bi bi-box-seam"></i>
          Inventario
        </div>

        <div class="menu-item">
          <i class="bi bi-truck"></i>
          Proveedores
        </div>
      </nav>

      <button class="logout" @click="salir">
        <i class="bi bi-box-arrow-left"></i>
        Cerrar sesión
      </button>
    </aside>

    <!-- Contenido principal -->
    <main class="main-content">

      <header class="topbar">
        <div>
          <h1>Dashboard del gerente</h1>
          <p>Resumen general del restaurante</p>
        </div>

        <div class="user-info">
          <i class="bi bi-person-circle"></i>
          Gerente
        </div>
      </header>

      <div v-if="cargando" class="loading">
        <div class="spinner-border text-warning"></div>
        <p>Cargando estadísticas...</p>
      </div>

      <div v-else-if="error" class="alert alert-danger">
        {{ error }}
        <button
          class="btn btn-outline-danger btn-sm ms-2"
          @click="obtenerDashboard"
        >
          Reintentar
        </button>
      </div>

      <template v-else-if="datos">

        <!-- Tarjetas de estadísticas -->
        <section class="stats-grid">

          <div class="stat-card">
            <div class="stat-icon orange">
              <i class="bi bi-receipt"></i>
            </div>
            <p>Total de pedidos</p>
            <h2>{{ datos.totalPedidos ?? 0 }}</h2>
          </div>

          <div class="stat-card">
            <div class="stat-icon green">
              <i class="bi bi-cash-stack"></i>
            </div>
            <p>Ingresos totales</p>
            <h2>
              S/ {{ Number(datos.ingresosTotales ?? 0).toFixed(2) }}
            </h2>
          </div>

          <div class="stat-card">
            <div class="stat-icon blue">
              <i class="bi bi-table"></i>
            </div>
            <p>Mesas disponibles</p>
            <h2>{{ datos.mesasDisponibles ?? 0 }}</h2>
          </div>

          <div class="stat-card">
            <div class="stat-icon red">
              <i class="bi bi-exclamation-triangle"></i>
            </div>
            <p>Insumos con stock bajo</p>
            <h2>{{ datos.insumosStockBajo ?? 0 }}</h2>
          </div>

        </section>

        <!-- Información adicional -->
        <section class="details-grid">

          <div class="detail-card">
            <h3>
              <i class="bi bi-clipboard-data me-2"></i>
              Estado de pedidos
            </h3>

            <div class="detail-row">
              <span>Pendientes</span>
              <strong>{{ datos.pedidosPendientes ?? 0 }}</strong>
            </div>

            <div class="detail-row">
              <span>En preparación</span>
              <strong>{{ datos.pedidosEnPreparacion ?? 0 }}</strong>
            </div>

            <div class="detail-row">
              <span>Entregados</span>
              <strong>{{ datos.pedidosEntregados ?? 0 }}</strong>
            </div>

            <div class="detail-row">
              <span>Cancelados</span>
              <strong>{{ datos.pedidosCancelados ?? 0 }}</strong>
            </div>
          </div>

          <div class="detail-card">
            <h3>
              <i class="bi bi-building me-2"></i>
              Información general
            </h3>

            <div class="detail-row">
              <span>Total de mesas</span>
              <strong>{{ datos.totalMesas ?? 0 }}</strong>
            </div>

            <div class="detail-row">
              <span>Platos disponibles</span>
              <strong>{{ datos.platosDisponibles ?? 0 }}</strong>
            </div>

            <div class="detail-row">
              <span>Proveedores activos</span>
              <strong>{{ datos.proveedoresActivos ?? 0 }}</strong>
            </div>

            <div class="detail-row">
              <span>Ingresos del día</span>
              <strong>
                S/ {{ Number(datos.ingresosDelDia ?? 0).toFixed(2) }}
              </strong>
            </div>
          </div>

        </section>

        <button
          class="btn btn-warning mt-4"
          @click="obtenerDashboard"
        >
          <i class="bi bi-arrow-clockwise me-2"></i>
          Actualizar estadísticas
        </button>

      </template>

    </main>
  </div>
</template>

<style scoped>
.dashboard {
  display: flex;
  min-height: 100vh;
  background: #f5f6fa;
}

.sidebar {
  width: 260px;
  background: #201d1a;
  color: white;
  padding: 30px 20px;
  display: flex;
  flex-direction: column;
}

.logo {
  text-align: center;
  margin-bottom: 45px;
}

.logo i {
  font-size: 38px;
  color: #d9a35f;
}

.logo h2 {
  font-size: 20px;
  font-weight: 800;
  margin-top: 12px;
}

.logo span {
  color: #d9a35f;
  font-weight: bold;
  letter-spacing: 3px;
}

.menu-title {
  color: #888;
  font-size: 11px;
  letter-spacing: 2px;
  margin-bottom: 15px;
}

.menu {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.menu a,
.menu-item {
  display: flex;
  align-items: center;
  gap: 14px;
  color: #bbb;
  padding: 14px;
  border-radius: 9px;
  text-decoration: none;
  font-size: 14px;
}

.menu a.active {
  background: #c58a47;
  color: white;
}

.menu-item {
  cursor: default;
}

.menu i {
  font-size: 18px;
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

.logout:hover {
  background: #3a3028;
}

.logout i {
  margin-right: 10px;
}

.main-content {
  flex: 1;
  min-width: 0;
  padding: 35px;
}

.topbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 35px;
}

.topbar h1 {
  font-size: 28px;
  font-weight: 800;
}

.topbar p {
  color: #888;
}

.user-info {
  background: white;
  padding: 12px 18px;
  border-radius: 9px;
  font-weight: 600;
}

.user-info i {
  margin-right: 8px;
  color: #c58a47;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
}

.stat-card,
.detail-card {
  background: white;
  padding: 25px;
  border-radius: 14px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.04);
}

.stat-card p {
  color: #888;
  margin-top: 15px;
  margin-bottom: 8px;
}

.stat-card h2 {
  font-size: 27px;
  font-weight: 800;
}

.stat-icon {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 12px;
  font-size: 24px;
}

.orange {
  background: #fff1e1;
  color: #c58a47;
}

.green {
  background: #e4f7ec;
  color: #24935a;
}

.blue {
  background: #e8f1ff;
  color: #3978c6;
}

.red {
  background: #ffebeb;
  color: #d9534f;
}

.details-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-top: 25px;
}

.detail-card h3 {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 25px;
}

.detail-row {
  display: flex;
  justify-content: space-between;
  padding: 13px 0;
  border-bottom: 1px solid #eee;
}

.loading {
  text-align: center;
  padding: 80px 0;
}

.loading p {
  margin-top: 15px;
}

@media (max-width: 1100px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .dashboard {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
  }

  .stats-grid,
  .details-grid {
    grid-template-columns: 1fr;
  }

  .main-content {
    padding: 20px;
  }

  .topbar {
    flex-direction: column;
    align-items: flex-start;
    gap: 15px;
  }
}
</style>