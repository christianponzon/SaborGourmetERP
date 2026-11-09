CREATE DATABASE IF NOT EXISTS sabor_gourmet_erp
CHARACTER SET utf8mb4
COLLATE utf8mb4_unicode_ci;

USE sabor_gourmet_erp;

-- =====================================================
-- 1. ROLES
-- =====================================================

CREATE TABLE IF NOT EXISTS roles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(30) NOT NULL UNIQUE,
    descripcion VARCHAR(150)
);

-- =====================================================
-- 2. USUARIOS
-- =====================================================

CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rol_id BIGINT NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    telefono VARCHAR(20),
    verificado BOOLEAN NOT NULL DEFAULT FALSE,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rol_id) REFERENCES roles(id)
);

-- =====================================================
-- 3. ZONAS DEL RESTAURANTE
-- =====================================================

CREATE TABLE IF NOT EXISTS zonas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(80) NOT NULL,
    descripcion VARCHAR(200)
);

-- =====================================================
-- 4. MESAS
-- =====================================================

CREATE TABLE IF NOT EXISTS mesas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero INT NOT NULL UNIQUE,
    zona_id BIGINT NOT NULL,
    capacidad INT NOT NULL,
    estado ENUM(
        'DISPONIBLE',
        'OCUPADA',
        'RESERVADA',
        'INACTIVA'
    ) NOT NULL DEFAULT 'DISPONIBLE',
    FOREIGN KEY (zona_id) REFERENCES zonas(id)
);

-- =====================================================
-- 5. RESERVAS
-- =====================================================

CREATE TABLE IF NOT EXISTS reservas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT,
    mesa_id BIGINT NOT NULL,
    nombre_cliente VARCHAR(150) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    fecha DATE NOT NULL,
    hora TIME NOT NULL,
    cantidad_personas INT NOT NULL,
    observaciones VARCHAR(255),
    estado ENUM(
        'PENDIENTE',
        'CONFIRMADA',
        'CANCELADA',
        'COMPLETADA'
    ) NOT NULL DEFAULT 'PENDIENTE',
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (mesa_id) REFERENCES mesas(id)
);

-- =====================================================
-- 6. CATEGORIAS
-- =====================================================

CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- =====================================================
-- 7. PLATOS
-- =====================================================

CREATE TABLE IF NOT EXISTS platos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    categoria_id BIGINT NOT NULL,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(255),
    precio DECIMAL(10,2) NOT NULL,
    disponible BOOLEAN NOT NULL DEFAULT TRUE,
    tiempo_preparacion INT DEFAULT 20,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- =====================================================
-- 8. PEDIDOS
-- =====================================================

CREATE TABLE IF NOT EXISTS pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cliente_id BIGINT,
    mesero_id BIGINT,
    mesa_id BIGINT,
    tipo ENUM(
        'SALON',
        'DELIVERY'
    ) NOT NULL,
    estado ENUM(
        'PENDIENTE',
        'EN_PREPARACION',
        'LISTO',
        'ENTREGADO',
        'CANCELADO'
    ) NOT NULL DEFAULT 'PENDIENTE',
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0,
    igv DECIMAL(10,2) NOT NULL DEFAULT 0,
    total DECIMAL(10,2) NOT NULL DEFAULT 0,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (cliente_id) REFERENCES usuarios(id),
    FOREIGN KEY (mesero_id) REFERENCES usuarios(id),
    FOREIGN KEY (mesa_id) REFERENCES mesas(id)
);

-- =====================================================
-- 9. DETALLE DE PEDIDOS
-- =====================================================

CREATE TABLE IF NOT EXISTS detalle_pedidos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    plato_id BIGINT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    observaciones VARCHAR(255),
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (plato_id) REFERENCES platos(id)
);

-- =====================================================
-- 10. COMANDAS DE COCINA
-- =====================================================

CREATE TABLE IF NOT EXISTS comandas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    cocinero_id BIGINT,
    estado ENUM(
        'PENDIENTE',
        'EN_PREPARACION',
        'LISTO'
    ) NOT NULL DEFAULT 'PENDIENTE',
    fecha_recepcion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_inicio DATETIME,
    fecha_finalizacion DATETIME,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (cocinero_id) REFERENCES usuarios(id)
);

-- =====================================================
-- 11. PAGOS
-- =====================================================

CREATE TABLE IF NOT EXISTS pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    cajero_id BIGINT,
    metodo ENUM(
        'EFECTIVO',
        'TARJETA',
        'YAPE',
        'PLIN'
    ) NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    monto_recibido DECIMAL(10,2),
    vuelto DECIMAL(10,2) DEFAULT 0,
    estado ENUM(
        'PENDIENTE',
        'PAGADO',
        'ANULADO'
    ) NOT NULL DEFAULT 'PENDIENTE',
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (cajero_id) REFERENCES usuarios(id)
);

-- =====================================================
-- 12. COMPROBANTES
-- =====================================================

CREATE TABLE IF NOT EXISTS comprobantes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pago_id BIGINT NOT NULL UNIQUE,
    tipo ENUM('BOLETA', 'FACTURA') NOT NULL,
    serie VARCHAR(10) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (serie, numero),
    FOREIGN KEY (pago_id) REFERENCES pagos(id)
);

-- =====================================================
-- 13. REPARTIDORES
-- =====================================================

CREATE TABLE IF NOT EXISTS repartidores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    estado ENUM(
        'DISPONIBLE',
        'OCUPADO',
        'INACTIVO'
    ) NOT NULL DEFAULT 'DISPONIBLE',
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- =====================================================
-- 14. ENTREGAS DELIVERY
-- =====================================================

CREATE TABLE IF NOT EXISTS entregas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL UNIQUE,
    repartidor_id BIGINT,
    nombre_receptor VARCHAR(150),
    telefono VARCHAR(20),
    direccion VARCHAR(255) NOT NULL,
    referencia VARCHAR(255),
    costo_envio DECIMAL(10,2) DEFAULT 0,
    estado ENUM(
        'PENDIENTE',
        'ASIGNADO',
        'EN_CAMINO',
        'ENTREGADO',
        'CANCELADO'
    ) NOT NULL DEFAULT 'PENDIENTE',
    fecha_salida DATETIME,
    fecha_entrega DATETIME,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (repartidor_id) REFERENCES repartidores(id)
);

-- =====================================================
-- 15. PROVEEDORES
-- =====================================================

CREATE TABLE IF NOT EXISTS proveedores (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    razon_social VARCHAR(150) NOT NULL,
    ruc VARCHAR(11) NOT NULL UNIQUE,
    telefono VARCHAR(20),
    correo VARCHAR(150),
    direccion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- =====================================================
-- 16. INSUMOS / INVENTARIO
-- =====================================================

CREATE TABLE IF NOT EXISTS insumos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    proveedor_id BIGINT,
    nombre VARCHAR(150) NOT NULL,
    categoria VARCHAR(100),
    unidad_medida VARCHAR(30) NOT NULL,
    stock_actual DECIMAL(12,2) NOT NULL DEFAULT 0,
    stock_minimo DECIMAL(12,2) NOT NULL DEFAULT 0,
    costo_unitario DECIMAL(10,2) DEFAULT 0,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id)
);

-- =====================================================
-- 17. MOVIMIENTOS DE STOCK
-- =====================================================

CREATE TABLE IF NOT EXISTS movimientos_stock (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    insumo_id BIGINT NOT NULL,
    usuario_id BIGINT,
    tipo ENUM('ENTRADA', 'SALIDA') NOT NULL,
    cantidad DECIMAL(12,2) NOT NULL,
    motivo VARCHAR(255),
    stock_anterior DECIMAL(12,2) NOT NULL,
    stock_resultante DECIMAL(12,2) NOT NULL,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (insumo_id) REFERENCES insumos(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- =====================================================
-- 18. COMPRAS
-- =====================================================

CREATE TABLE IF NOT EXISTS compras (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    proveedor_id BIGINT NOT NULL,
    usuario_id BIGINT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL DEFAULT 0,
    estado ENUM(
        'PENDIENTE',
        'RECIBIDA',
        'CANCELADA'
    ) NOT NULL DEFAULT 'PENDIENTE',
    FOREIGN KEY (proveedor_id) REFERENCES proveedores(id),
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
);

-- =====================================================
-- 19. DETALLE DE COMPRAS
-- =====================================================

CREATE TABLE IF NOT EXISTS detalle_compras (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    compra_id BIGINT NOT NULL,
    insumo_id BIGINT NOT NULL,
    cantidad DECIMAL(12,2) NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    subtotal DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (compra_id) REFERENCES compras(id),
    FOREIGN KEY (insumo_id) REFERENCES insumos(id)
);

-- =====================================================
-- 20. CONFIGURACION DEL RESTAURANTE
-- =====================================================

CREATE TABLE IF NOT EXISTS configuracion_restaurante (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre_comercial VARCHAR(150) NOT NULL,
    razon_social VARCHAR(150),
    ruc VARCHAR(11),
    telefono VARCHAR(20),
    correo VARCHAR(150),
    direccion VARCHAR(255),
    igv_porcentaje DECIMAL(5,2) DEFAULT 18.00,
    moneda VARCHAR(10) DEFAULT 'PEN',
    costo_delivery DECIMAL(10,2) DEFAULT 5.00,
    tiempo_preparacion INT DEFAULT 25,
    hora_apertura TIME,
    hora_cierre TIME,
    estado ENUM('ABIERTO', 'CERRADO') DEFAULT 'ABIERTO'
);

-- =====================================================
-- DATOS DE PRUEBA
-- =====================================================

-- ROLES
INSERT INTO roles (id, nombre, descripcion) VALUES
(1, 'GERENTE', 'Administrador general'),
(2, 'MESERO', 'Atencion de mesas y pedidos'),
(3, 'COCINERO', 'Preparacion de pedidos'),
(4, 'CAJERO', 'Gestion de cobros'),
(5, 'REPARTIDOR', 'Entrega de pedidos delivery'),
(6, 'CLIENTE', 'Cliente del restaurante');

-- USUARIOS
-- Hash BCrypt de ejemplo: contraseña "password".
-- Son cuentas ficticias para una base de demostración.
INSERT INTO usuarios
(id, rol_id, nombre, apellido, correo,
 password_hash, telefono, verificado, activo)
VALUES
(1, 1, 'Ana', 'Torres', 'gerente@demo.local',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiC1G8JpYxP1x8hU4nVYpZg0XyYxN5K',
 '987111111', TRUE, TRUE),
(2, 2, 'Luis', 'Ramos', 'mesero@demo.local',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiC1G8JpYxP1x8hU4nVYpZg0XyYxN5K',
 '987222222', TRUE, TRUE),
(3, 3, 'Pedro', 'Flores', 'cocinero@demo.local',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiC1G8JpYxP1x8hU4nVYpZg0XyYxN5K',
 '987333333', TRUE, TRUE),
(4, 4, 'Maria', 'Lopez', 'cajero@demo.local',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiC1G8JpYxP1x8hU4nVYpZg0XyYxN5K',
 '987444444', TRUE, TRUE),
(5, 5, 'Jose', 'Diaz', 'repartidor@demo.local',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiC1G8JpYxP1x8hU4nVYpZg0XyYxN5K',
 '987555555', TRUE, TRUE),
(6, 6, 'Carlos', 'Mendoza', 'cliente@demo.local',
 '$2a$10$7EqJtq98hPqEX7fNZaFWoOhiC1G8JpYxP1x8hU4nVYpZg0XyYxN5K',
 '987666666', TRUE, TRUE);

-- ZONAS
INSERT INTO zonas (id, nombre, descripcion) VALUES
(1, 'Salon Principal', 'Zona interior'),
(2, 'Terraza', 'Zona al aire libre'),
(3, 'Zona VIP', 'Mesas especiales');

-- MESAS
INSERT INTO mesas (id, numero, zona_id, capacidad, estado) VALUES
(1, 1, 1, 4, 'OCUPADA'),
(2, 2, 1, 4, 'DISPONIBLE'),
(3, 3, 1, 2, 'DISPONIBLE'),
(4, 4, 2, 4, 'RESERVADA'),
(5, 5, 2, 6, 'DISPONIBLE'),
(6, 6, 3, 8, 'DISPONIBLE');

-- RESERVAS
INSERT INTO reservas
(id, usuario_id, mesa_id, nombre_cliente, telefono,
 fecha, hora, cantidad_personas, estado)
VALUES
(1, 6, 4, 'Carlos Mendoza', '987666666',
 '2026-11-15', '19:00:00', 4, 'CONFIRMADA'),
(2, NULL, 5, 'Lucia Perez', '987777777',
 '2026-11-16', '20:00:00', 5, 'PENDIENTE');

-- CATEGORIAS
INSERT INTO categorias (id, nombre, descripcion) VALUES
(1, 'Entradas', 'Platos para comenzar'),
(2, 'Platos Principales', 'Especialidades'),
(3, 'Bebidas', 'Bebidas frias y calientes'),
(4, 'Postres', 'Postres de la casa');

-- PLATOS
INSERT INTO platos
(id, categoria_id, nombre, descripcion, precio,
 disponible, tiempo_preparacion)
VALUES
(1, 2, 'Lomo Saltado', 'Lomo con papas y arroz', 38.00, TRUE, 25),
(2, 2, 'Arroz con Pollo', 'Arroz verde con pollo', 25.00, TRUE, 20),
(3, 2, 'Aji de Gallina', 'Plato tradicional peruano', 28.00, TRUE, 25),
(4, 1, 'Causa Limena', 'Causa rellena de pollo', 18.00, TRUE, 15),
(5, 3, 'Chicha Morada', 'Bebida tradicional', 8.00, TRUE, 5),
(6, 3, 'Limonada', 'Limonada natural', 7.00, TRUE, 5),
(7, 4, 'Suspiro Limeno', 'Postre tradicional', 15.00, TRUE, 10),
(8, 4, 'Torta de Chocolate', 'Porcion de torta', 14.00, TRUE, 5);

-- PEDIDOS
-- Los precios de ejemplo incluyen IGV.

INSERT INTO pedidos
(id, cliente_id, mesero_id, mesa_id, tipo, estado,
 subtotal, igv, total)
VALUES
(1, 6, 2, 1, 'SALON', 'ENTREGADO',
 38.98, 7.02, 46.00),
(2, 6, NULL, NULL, 'DELIVERY', 'EN_PREPARACION',
 38.14, 6.86, 45.00);

-- DETALLES
INSERT INTO detalle_pedidos
(pedido_id, plato_id, cantidad, precio_unitario,
 subtotal, observaciones)
VALUES
(1, 1, 1, 38.00, 38.00, 'Sin cebolla'),
(1, 5, 1, 8.00, 8.00, NULL),
(2, 2, 1, 25.00, 25.00, NULL),
(2, 4, 1, 18.00, 18.00, NULL);

-- COMANDAS
INSERT INTO comandas
(id, pedido_id, cocinero_id, estado)
VALUES
(1, 1, 3, 'LISTO'),
(2, 2, 3, 'EN_PREPARACION');

-- PAGOS
INSERT INTO pagos
(id, pedido_id, cajero_id, metodo, monto,
 monto_recibido, vuelto, estado)
VALUES
(1, 1, 4, 'YAPE', 46.00, 46.00, 0.00, 'PAGADO');

-- COMPROBANTES
INSERT INTO comprobantes
(id, pago_id, tipo, serie, numero)
VALUES
(1, 1, 'BOLETA', 'B001', '00000001');

-- REPARTIDORES
INSERT INTO repartidores
(id, usuario_id, codigo, estado)
VALUES
(1, 5, 'REP-001', 'DISPONIBLE');

-- ENTREGAS
INSERT INTO entregas
(id, pedido_id, repartidor_id, nombre_receptor,
 telefono, direccion, referencia, costo_envio, estado)
VALUES
(1, 2, NULL, 'Carlos Mendoza',
 '987666666', 'Av. Principal 123, Lima',
 'Frente al parque', 2.00, 'PENDIENTE');

-- PROVEEDORES
INSERT INTO proveedores
(id, razon_social, ruc, telefono, correo, activo)
VALUES
(1, 'Distribuidora Andina SAC', '20123456789',
 '014445555', 'ventas@andina.example', TRUE),
(2, 'Productos del Campo SAC', '20987654321',
 '016667777', 'contacto@campo.example', TRUE);

-- INSUMOS
INSERT INTO insumos
(id, proveedor_id, nombre, categoria, unidad_medida,
 stock_actual, stock_minimo, costo_unitario)
VALUES
(1, 1, 'Arroz', 'Granos', 'KG', 50, 10, 4.50),
(2, 1, 'Aceite', 'Abarrotes', 'LITRO', 20, 5, 9.00),
(3, 2, 'Pollo', 'Carnes', 'KG', 30, 8, 12.00),
(4, 2, 'Papa', 'Verduras', 'KG', 40, 10, 3.50),
(5, 2, 'Cebolla', 'Verduras', 'KG', 15, 5, 3.00);

-- MOVIMIENTOS DE STOCK
INSERT INTO movimientos_stock
(id, insumo_id, usuario_id, tipo, cantidad,
 motivo, stock_anterior, stock_resultante)
VALUES
(1, 1, 1, 'ENTRADA', 20, 'Compra de arroz', 30, 50),
(2, 3, 1, 'SALIDA', 5, 'Uso en cocina', 35, 30);

-- COMPRAS
INSERT INTO compras
(id, proveedor_id, usuario_id, total, estado)
VALUES
(1, 1, 1, 90.00, 'RECIBIDA');

-- DETALLE DE COMPRAS
INSERT INTO detalle_compras
(compra_id, insumo_id, cantidad, precio_unitario, subtotal)
VALUES
(1, 1, 20, 4.50, 90.00);

-- CONFIGURACION
INSERT INTO configuracion_restaurante
(id, nombre_comercial, razon_social, ruc,
 telefono, correo, direccion, igv_porcentaje,
 moneda, costo_delivery, tiempo_preparacion,
 hora_apertura, hora_cierre, estado)
VALUES
(1, 'Sabor Gourmet', 'Sabor Gourmet SAC',
 '20601234567', '014567890',
 'contacto@saborgourmet.example',
 'Av. Gastronomia 456, Lima',
 18.00, 'PEN', 5.00, 25,
 '11:00:00', '23:00:00', 'ABIERTO');

-- =====================================================
-- CONSULTAS PARA EL PROFESOR
-- =====================================================

-- Usuarios y sus roles
SELECT u.id, u.nombre, u.apellido, u.correo,
       r.nombre AS rol
FROM usuarios u
JOIN roles r ON r.id = u.rol_id;

-- Carta del restaurante
SELECT c.nombre AS categoria, p.nombre AS plato,
       p.precio, p.disponible
FROM platos p
JOIN categorias c ON c.id = p.categoria_id;

-- Mesas y disponibilidad
SELECT m.numero, z.nombre AS zona,
       m.capacidad, m.estado
FROM mesas m
JOIN zonas z ON z.id = m.zona_id;

-- Pedidos con sus detalles
SELECT p.id AS pedido, p.tipo, p.estado,
       pl.nombre AS plato, d.cantidad,
       d.subtotal
FROM pedidos p
JOIN detalle_pedidos d ON d.pedido_id = p.id
JOIN platos pl ON pl.id = d.plato_id;

-- Ventas pagadas
SELECT COUNT(*) AS pagos_realizados,
       COALESCE(SUM(monto), 0) AS ingresos
FROM pagos
WHERE estado = 'PAGADO';

-- Inventario con stock bajo
SELECT nombre, stock_actual, stock_minimo
FROM insumos
WHERE stock_actual <= stock_minimo;

-- Seguimiento de delivery
SELECT e.id, p.id AS pedido,
       e.direccion, e.estado,
       e.costo_envio
FROM entregas e
JOIN pedidos p ON p.id = e.pedido_id;