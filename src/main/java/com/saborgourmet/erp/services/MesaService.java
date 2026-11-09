package com.saborgourmet.erp.services;

import com.saborgourmet.erp.models.Mesa;
import com.saborgourmet.erp.repositories.MesaRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Locale;

@Service
public class MesaService {

    private final MesaRepository mesaRepository;

    public MesaService(MesaRepository mesaRepository) {
        this.mesaRepository = mesaRepository;
    }

    // Listar todas las mesas
    public List<Mesa> listarTodas() {
        return mesaRepository.listarTodas();
    }

    // Buscar mesa por ID
    public Mesa buscarPorId(Long id) {
        return mesaRepository.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Mesa no encontrada"
                ));
    }

    // Listar mesas disponibles
    public List<Mesa> listarDisponibles() {
        return mesaRepository.listarDisponibles();
    }

    // Buscar mesas por estado
    public List<Mesa> buscarPorEstado(String estado) {

        String estadoNormalizado = normalizarEstado(estado);

        return mesaRepository.buscarPorEstado(estadoNormalizado);
    }

    // Registrar una nueva mesa
    public synchronized Mesa registrar(Mesa mesa) {

        validarMesa(mesa);

        if (mesaRepository.buscarPorNumero(
                mesa.getNumero()).isPresent()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe una mesa con ese numero"
            );
        }

        // El servidor genera el ID
        mesa.setId(null);

        return mesaRepository.guardar(mesa);
    }

    // Actualizar una mesa
    public synchronized Mesa actualizar(Long id, Mesa datos) {

        validarMesa(datos);

        Mesa mesaExistente = buscarPorId(id);

        // Evitar numeros duplicados
        mesaRepository.buscarPorNumero(datos.getNumero())
                .ifPresent(otraMesa -> {

                    if (!otraMesa.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Ya existe una mesa con ese numero"
                        );
                    }
                });

        mesaExistente.setNumero(datos.getNumero());
        mesaExistente.setCapacidad(datos.getCapacidad());
        mesaExistente.setUbicacion(datos.getUbicacion());
        mesaExistente.setEstado(datos.getEstado());
        mesaExistente.setActiva(datos.isActiva());

        return mesaRepository.guardar(mesaExistente);
    }

    // Cambiar solamente el estado de una mesa
    public synchronized Mesa cambiarEstado(Long id, String estado) {

        Mesa mesa = buscarPorId(id);

        if (!mesa.isActiva()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "No se puede cambiar el estado de una mesa inactiva"
            );
        }

        String nuevoEstado = normalizarEstado(estado);

        mesa.setEstado(nuevoEstado);

        return mesaRepository.guardar(mesa);
    }

    // Eliminar una mesa
    public synchronized void eliminar(Long id) {

        Mesa mesa = buscarPorId(id);

        if ("OCUPADA".equalsIgnoreCase(mesa.getEstado()) ||
            "RESERVADA".equalsIgnoreCase(mesa.getEstado())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "No se puede eliminar una mesa ocupada o reservada"
            );
        }

        mesaRepository.eliminar(id);
    }

    // Validar datos de una mesa
    private void validarMesa(Mesa mesa) {

        if (mesa == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos de la mesa son obligatorios"
            );
        }

        if (mesa.getNumero() == null ||
            mesa.getNumero() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El numero de mesa debe ser mayor que cero"
            );
        }

        if (mesa.getCapacidad() == null ||
            mesa.getCapacidad() <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La capacidad debe ser mayor que cero"
            );
        }

        if (mesa.getUbicacion() == null ||
            mesa.getUbicacion().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La ubicacion es obligatoria"
            );
        }

        mesa.setUbicacion(mesa.getUbicacion().trim());

        mesa.setEstado(
                normalizarEstado(mesa.getEstado())
        );
    }

    // Validar y normalizar el estado
    private String normalizarEstado(String estado) {

        if (estado == null || estado.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El estado es obligatorio"
            );
        }

        String valor = estado.trim()
                .toUpperCase(Locale.ROOT);

        if (!valor.equals("DISPONIBLE") &&
            !valor.equals("OCUPADA") &&
            !valor.equals("RESERVADA")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Estado invalido. Use DISPONIBLE, OCUPADA o RESERVADA"
            );
        }

        return valor;
    }
}
