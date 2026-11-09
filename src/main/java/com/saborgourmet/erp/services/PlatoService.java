package com.saborgourmet.erp.services;

import com.saborgourmet.erp.models.Plato;
import com.saborgourmet.erp.repositories.PlatoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class PlatoService {

    private final PlatoRepository platoRepository;

    public PlatoService(PlatoRepository platoRepository) {
        this.platoRepository = platoRepository;
    }

    // Listar todos los platos
    public List<Plato> listarTodos() {
        return platoRepository.listarTodos();
    }

    // Buscar un plato por ID
    public Plato buscarPorId(Long id) {
        return platoRepository.buscarPorId(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Plato no encontrado"
                ));
    }

    // Buscar platos por categoria
    public List<Plato> buscarPorCategoria(String categoria) {
        return platoRepository.buscarPorCategoria(categoria);
    }

    // Listar platos disponibles
    public List<Plato> listarDisponibles() {
        return platoRepository.listarDisponibles();
    }

    // Registrar un plato
    public Plato registrar(Plato plato) {

        validarPlato(plato);

        // El ID lo genera el servidor
        plato.setId(null);

        return platoRepository.guardar(plato);
    }

    // Actualizar un plato
    public synchronized Plato actualizar(Long id, Plato datos) {

        validarPlato(datos);

        Plato platoExistente = buscarPorId(id);

        platoExistente.setNombre(datos.getNombre().trim());
        platoExistente.setDescripcion(datos.getDescripcion());
        platoExistente.setPrecio(datos.getPrecio());
        platoExistente.setCategoria(datos.getCategoria().trim());
        platoExistente.setImagen(datos.getImagen());
        platoExistente.setDisponible(datos.isDisponible());

        return platoRepository.guardar(platoExistente);
    }

    // Eliminar un plato
    public void eliminar(Long id) {

        boolean eliminado = platoRepository.eliminar(id);

        if (!eliminado) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Plato no encontrado"
            );
        }
    }

    // Validar datos de un plato
    private void validarPlato(Plato plato) {

        if (plato == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos del plato son obligatorios"
            );
        }

        if (plato.getNombre() == null ||
                plato.getNombre().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre del plato es obligatorio"
            );
        }

        if (plato.getCategoria() == null ||
                plato.getCategoria().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La categoria del plato es obligatoria"
            );
        }

        if (plato.getPrecio() == null ||
                plato.getPrecio().compareTo(BigDecimal.ZERO) <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio debe ser mayor que cero"
            );
        }

        if (plato.getPrecio().scale() > 2) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El precio no puede tener mas de dos decimales"
            );
        }

        if (plato.getNombre().trim().length() > 150) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre no puede superar 150 caracteres"
            );
        }

        // Normalizar los datos
        plato.setNombre(plato.getNombre().trim());
        plato.setCategoria(plato.getCategoria().trim());
    }
}
