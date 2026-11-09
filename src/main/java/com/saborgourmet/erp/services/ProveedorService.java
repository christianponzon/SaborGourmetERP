package com.saborgourmet.erp.services;

import com.saborgourmet.erp.models.Proveedor;
import com.saborgourmet.erp.repositories.ProveedorRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProveedorService {

    private final ProveedorRepository repository;

    public ProveedorService(ProveedorRepository repository) {
        this.repository = repository;
    }

    // ==========================================
    // LISTAR TODOS LOS PROVEEDORES
    // ==========================================
    public List<Proveedor> listarTodos() {
        return repository.listarTodos();
    }

    // ==========================================
    // BUSCAR PROVEEDOR POR ID
    // ==========================================
    public Proveedor buscarPorId(Long id) {

        return repository.buscarPorId(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Proveedor no encontrado"
                        )
                );
    }

    // ==========================================
    // BUSCAR PROVEEDOR POR RUC
    // ==========================================
    public Proveedor buscarPorRuc(String ruc) {

        return repository.buscarPorRuc(ruc)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Proveedor con RUC no encontrado"
                        )
                );
    }

    // ==========================================
    // BUSCAR POR RAZON SOCIAL
    // ==========================================
    public List<Proveedor> buscarPorRazonSocial(String nombre) {
        return repository.buscarPorRazonSocial(nombre);
    }

    // ==========================================
    // BUSCAR POR CATEGORIA
    // ==========================================
    public List<Proveedor> buscarPorCategoria(String categoria) {
        return repository.buscarPorCategoria(categoria);
    }

    // ==========================================
    // LISTAR PROVEEDORES ACTIVOS
    // ==========================================
    public List<Proveedor> listarActivos() {
        return repository.listarActivos();
    }

    // ==========================================
    // LISTAR PROVEEDORES INACTIVOS
    // ==========================================
    public List<Proveedor> listarInactivos() {
        return repository.listarInactivos();
    }

    // ==========================================
    // REGISTRAR PROVEEDOR
    // ==========================================
    public synchronized Proveedor registrar(Proveedor proveedor) {

        validarProveedor(proveedor);

        if (repository.existePorRuc(proveedor.getRuc())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un proveedor con este RUC"
            );
        }

        proveedor.setId(null);
        proveedor.setActivo(true);
        proveedor.setFechaCreacion(LocalDateTime.now());
        proveedor.setFechaActualizacion(LocalDateTime.now());

        return repository.guardar(proveedor);
    }

    // ==========================================
    // ACTUALIZAR PROVEEDOR
    // ==========================================
    public synchronized Proveedor actualizar(
            Long id,
            Proveedor datos) {

        Proveedor proveedor = buscarPorId(id);

        validarProveedor(datos);

        repository.buscarPorRuc(datos.getRuc())
                .ifPresent(existente -> {
                    if (!existente.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "El RUC pertenece a otro proveedor"
                        );
                    }
                });

        proveedor.setRazonSocial(datos.getRazonSocial());
        proveedor.setRuc(datos.getRuc());
        proveedor.setNombreContacto(datos.getNombreContacto());
        proveedor.setTelefono(datos.getTelefono());
        proveedor.setCorreo(datos.getCorreo());
        proveedor.setDireccion(datos.getDireccion());
        proveedor.setCategoria(datos.getCategoria());
        proveedor.setFechaActualizacion(LocalDateTime.now());

        return repository.guardar(proveedor);
    }

    // ==========================================
    // ACTIVAR PROVEEDOR
    // ==========================================
    public synchronized Proveedor activar(Long id) {

        Proveedor proveedor = buscarPorId(id);

        proveedor.setActivo(true);
        proveedor.setFechaActualizacion(LocalDateTime.now());

        return repository.guardar(proveedor);
    }

    // ==========================================
    // DESACTIVAR PROVEEDOR
    // ==========================================
    public synchronized Proveedor desactivar(Long id) {

        Proveedor proveedor = buscarPorId(id);

        proveedor.setActivo(false);
        proveedor.setFechaActualizacion(LocalDateTime.now());

        return repository.guardar(proveedor);
    }

    // ==========================================
    // ELIMINAR PROVEEDOR
    // ==========================================
    public synchronized void eliminar(Long id) {

        buscarPorId(id);

        repository.eliminar(id);
    }

    // ==========================================
    // INDICADORES
    // ==========================================
    public long contarTodos() {
        return repository.contarTodos();
    }

    public long contarActivos() {
        return repository.contarActivos();
    }

    public long contarInactivos() {
        return repository.contarInactivos();
    }

    // ==========================================
    // VALIDACIONES
    // ==========================================
    private void validarProveedor(Proveedor proveedor) {

        if (proveedor == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos del proveedor son obligatorios"
            );
        }

        if (proveedor.getRazonSocial() == null ||
                proveedor.getRazonSocial().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La razon social es obligatoria"
            );
        }

        if (proveedor.getRuc() == null ||
                !proveedor.getRuc().matches("\\d{11}")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El RUC debe contener exactamente 11 digitos"
            );
        }

        if (proveedor.getCorreo() != null &&
                !proveedor.getCorreo().isBlank() &&
                !proveedor.getCorreo().matches(
                        "^[\\w.+-]+@[\\w.-]+\\.[A-Za-z]{2,}$")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El correo electronico no es valido"
            );
        }

        if (proveedor.getTelefono() != null &&
                !proveedor.getTelefono().isBlank() &&
                !proveedor.getTelefono().matches("\\d{7,15}")) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El telefono debe contener entre 7 y 15 digitos"
            );
        }
    }
}
