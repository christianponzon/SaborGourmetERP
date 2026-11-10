import api from './authService'

// Obtener todos los platos
export const obtenerPlatos = async () => {
  const response = await api.get('/menus')
  return response.data
}

// Obtener un plato por ID
export const obtenerPlatoPorId = async (id) => {
  const response = await api.get(`/menus/${id}`)
  return response.data
}

// Registrar un nuevo plato
export const crearPlato = async (plato) => {
  const response = await api.post('/menus', plato)
  return response.data
}

// Actualizar un plato
export const actualizarPlato = async (id, plato) => {
  const response = await api.put(`/menus/${id}`, plato)
  return response.data
}

// Eliminar un plato
export const eliminarPlato = async (id) => {
  const response = await api.delete(`/menus/${id}`)
  return response.data
}