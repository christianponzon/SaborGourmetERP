import axios from 'axios'

const api = axios.create({
  baseURL: 'http://localhost:8080/api',
  headers: {
    'Content-Type': 'application/json'
  }
})

// Iniciar sesión
export const login = async (correo, password) => {
  const response = await api.post('/auth/login', {
    correo,
    password
  })

  return response.data
}

// Guardar token JWT durante la sesión
export const guardarToken = (token) => {
  sessionStorage.setItem('sabor_gourmet_token', token)
}

// Obtener token JWT
export const obtenerToken = () => {
  return sessionStorage.getItem('sabor_gourmet_token')
}

// Cerrar sesión
export const cerrarSesion = () => {
  sessionStorage.removeItem('sabor_gourmet_token')
}

// Agregar token a las peticiones protegidas
api.interceptors.request.use((config) => {
  const token = obtenerToken()

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

export default api