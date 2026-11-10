import { createRouter, createWebHistory } from 'vue-router'
import { obtenerToken } from '../services/authService'

const routes = [
  {
    path: '/',
    name: 'inicio',
    component: () => import('../views/HomeView.vue')
  },
  {
    path: '/login',
    name: 'login',
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/dashboard/gerente',
    name: 'dashboard-gerente',
    component: () => import('../views/DashboardGerenteView.vue'),
    meta: { requiereAuth: true }
  },
  {
    path: '/menus',
    name: 'menus',
    component: () => import('../views/MenuView.vue'),
    meta: { requiereAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  if (to.meta.requiereAuth && !obtenerToken()) {
    return '/login'
  }
})

export default router