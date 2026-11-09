package com.saborgourmet.erp.services;

import com.saborgourmet.erp.models.Insumo;
import com.saborgourmet.erp.repositories.InsumoRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;

@Service
public class InsumoService {

    private final InsumoRepository insumoRepository;

    public InsumoService(InsumoRepository insumoRepository) {
        this.insumoRepository = insumoRepository;
    }

    // ==========================================
    // LISTAR TODOS LOS INSUMOS
    // ==========================================
    public List<Insumo> listarTodos() {
        return insumoRepository.listarTodos();
    }

    // ==========================================
    // BUSCAR INSUMO POR ID
    // ==========================================
    public Insumo buscarPorId(Long id) {

        return insumoRepository.buscarPorId(id)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Insumo no encontrado"
                        )
                );
    }

    // ==========================================
    // BUSCAR INSUMOS POR CATEGORIA
    // ==========================================
    public List<Insumo> buscarPorCategoria(String categoria) {

        return insumoRepository.buscarPorCategoria(categoria);
    }

    // ==========================================
    // LISTAR INSUMOS ACTIVOS
    // ==========================================
    public List<Insumo> listarActivos() {

        return insumoRepository.listarActivos();
    }

    // ==========================================
    // LISTAR INSUMOS CON STOCK BAJO
    // ==========================================
    public List<Insumo> listarStockBajo() {

        return insumoRepository.listarStockBajo();
    }

    // ==========================================
    // LISTAR INSUMOS AGOTADOS
    // ==========================================
    public List<Insumo> listarAgotados() {

        return insumoRepository.listarAgotados();
    }

    // ==========================================
    // REGISTRAR INSUMO
    // ==========================================
    public synchronized Insumo registrar(Insumo datos) {

        if (datos == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos del insumo son obligatorios"
            );
        }

        validarDatos(datos);

        String nombre = datos.getNombre().trim();

        if (insumoRepository.existePorNombre(nombre)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Ya existe un insumo con ese nombre"
            );
        }

        Insumo insumo = new Insumo();

        insumo.setNombre(nombre);
        insumo.setDescripcion(
                limpiarTexto(datos.getDescripcion())
        );

        insumo.setCategoria(
                datos.getCategoria().trim()
        );

        insumo.setUnidadMedida(
                datos.getUnidadMedida()
                        .trim()
                        .toUpperCase(Locale.ROOT)
        );

        insumo.setStockActual(datos.getStockActual());
        insumo.setStockMinimo(datos.getStockMinimo());
        insumo.setCostoUnitario(datos.getCostoUnitario());

        insumo.setActivo(true);
        insumo.setFechaCreacion(LocalDateTime.now());
        insumo.setFechaActualizacion(LocalDateTime.now());

        return insumoRepository.guardar(insumo);
    }

    // ==========================================
    // ACTUALIZAR INFORMACION DEL INSUMO
    // ==========================================
    public synchronized Insumo actualizar(
            Long id,
            Insumo datos) {

        Insumo insumo = buscarPorId(id);

        if (datos == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Los datos son obligatorios"
            );
        }

        validarDatos(datos);

        String nuevoNombre = datos.getNombre().trim();

        insumoRepository.buscarPorNombre(nuevoNombre)
                .ifPresent(existente -> {

                    if (!existente.getId().equals(id)) {
                        throw new ResponseStatusException(
                                HttpStatus.CONFLICT,
                                "Ya existe otro insumo con ese nombre"
                        );
                    }
                });

        insumo.setNombre(nuevoNombre);

        insumo.setDescripcion(
                limpiarTexto(datos.getDescripcion())
        );

        insumo.setCategoria(
                datos.getCategoria().trim()
        );

        insumo.setUnidadMedida(
                datos.getUnidadMedida()
                        .trim()
                        .toUpperCase(Locale.ROOT)
        );

        // El stock actual se modifica mediante
        // operaciones de entrada o salida.
        // No se reemplaza directamente al editar.

        insumo.setStockMinimo(datos.getStockMinimo());
        insumo.setCostoUnitario(datos.getCostoUnitario());

        insumo.setFechaActualizacion(LocalDateTime.now());

        return insumoRepository.guardar(insumo);
    }

    // ==========================================
    // REGISTRAR ENTRADA DE STOCK
    // ==========================================
    public synchronized Insumo registrarEntrada(
            Long id,
            BigDecimal cantidad) {

        Insumo insumo = buscarPorId(id);

        validarInsumoActivo(insumo);
        validarCantidad(cantidad);

        BigDecimal nuevoStock =
                insumo.getStockActual().add(cantidad);

        insumo.setStockActual(nuevoStock);
        insumo.setFechaActualizacion(LocalDateTime.now());

        return insumoRepository.guardar(insumo);
    }

    // ==========================================
    // REGISTRAR SALIDA DE STOCK
    // ==========================================
    public synchronized Insumo registrarSalida(
            Long id,
            BigDecimal cantidad) {

        Insumo insumo = buscarPorId(id);

        validarInsumoActivo(insumo);
        validarCantidad(cantidad);

        if (insumo.getStockActual().compareTo(cantidad) < 0) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Stock insuficiente para realizar la salida"
            );
        }

        BigDecimal nuevoStock =
                insumo.getStockActual().subtract(cantidad);

        insumo.setStockActual(nuevoStock);
        insumo.setFechaActualizacion(LocalDateTime.now());

        return insumoRepository.guardar(insumo);
    }

    // ==========================================
    // DESACTIVAR INSUMO
    // ==========================================
    public synchronized Insumo desactivar(Long id) {

        Insumo insumo = buscarPorId(id);

        if (!insumo.isActivo()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El insumo ya esta desactivado"
            );
        }

        insumo.setActivo(false);
        insumo.setFechaActualizacion(LocalDateTime.now());

        return insumoRepository.guardar(insumo);
    }

    // ==========================================
    // ACTIVAR INSUMO
    // ==========================================
    public synchronized Insumo activar(Long id) {

        Insumo insumo = buscarPorId(id);

        insumo.setActivo(true);
        insumo.setFechaActualizacion(LocalDateTime.now());

        return insumoRepository.guardar(insumo);
    }

    // ==========================================
    // CONTAR INSUMOS
    // ==========================================
    public long contarTodos() {

        return insumoRepository.contarTodos();
    }

    // ==========================================
    // CONTAR INSUMOS CON STOCK BAJO
    // ==========================================
    public long contarStockBajo() {

        return insumoRepository.contarStockBajo();
    }

    // ==========================================
    // VALIDAR DATOS DEL INSUMO
    // ==========================================
    private void validarDatos(Insumo datos) {

        if (datos.getNombre() == null
                || datos.getNombre().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "El nombre del insumo es obligatorio"
            );
        }

        if (datos.getCategoria() == null
                || datos.getCategoria().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La categoria es obligatoria"
            );
        }

        if (datos.getUnidadMedida() == null
                || datos.getUnidadMedida().isBlank()) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La unidad de medida es obligatoria"
            );
        }

        validarNoNegativo(
                datos.getStockMinimo(),
                "El stock minimo"
        );

        validarNoNegativo(
                datos.getCostoUnitario(),
                "El costo unitario"
        );

        // Para registrar un insumo, el stock es obligatorio.
        // En las actualizaciones se conserva el stock existente,
        // aunque el cuerpo incluya otro valor.
        if (datos.getStockActual() != null) {
            validarNoNegativo(
                    datos.getStockActual(),
                    "El stock actual"
            );
        }
    }

    // ==========================================
    // VALIDAR NUMEROS NO NEGATIVOS
    // ==========================================
    private void validarNoNegativo(
            BigDecimal valor,
            String campo) {

        if (valor == null
                || valor.compareTo(BigDecimal.ZERO) < 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    campo + " debe ser mayor o igual a cero"
            );
        }
    }

    // ==========================================
    // VALIDAR CANTIDAD POSITIVA
    // ==========================================
    private void validarCantidad(BigDecimal cantidad) {

        if (cantidad == null
                || cantidad.compareTo(BigDecimal.ZERO) <= 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "La cantidad debe ser mayor a cero"
            );
        }
    }

    // ==========================================
    // VALIDAR INSUMO ACTIVO
    // ==========================================
    private void validarInsumoActivo(Insumo insumo) {

        if (!insumo.isActivo()) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "El insumo esta desactivado"
            );
        }
    }

    // ==========================================
    // LIMPIAR TEXTO
    // ==========================================
    private String limpiarTexto(String texto) {

        if (texto == null || texto.isBlank()) {
            return null;
        }

        return texto.trim();
    }
}
