package com.sena.GA7_AA3_EV01_springboot.servicio;

import com.sena.GA7_AA3_EV01_springboot.modelo.Proyecto;
import com.sena.GA7_AA3_EV01_springboot.repositorio.ProyectoRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ProyectoService {

    // Inyección del repositorio para acceso a datos
    private final ProyectoRepository proyectoRepository;

    // =================== OPERACIONES CRUD ===================

    // Crear nuevo proyecto con validaciones de negocio
    public Proyecto crearProyecto(@Valid Proyecto proyecto) {
        log.info("🆕 Creando nuevo proyecto: {}", proyecto.getNombreObra());

        validarNitUnico(proyecto.getNitCliente(), null);

        Proyecto proyectoGuardado = proyectoRepository.save(proyecto);
        log.info("✅ Proyecto creado exitosamente con ID: {}", proyectoGuardado.getId());

        return proyectoGuardado;
    }

    // Listar todos los proyectos ordenados por fecha de creación
    @Transactional(readOnly = true)
    public List<Proyecto> listarTodosLosProyectos() {
        log.debug("📋 Listando todos los proyectos");
        return proyectoRepository.findAllByOrderByFechaCreacionDesc();
    }

    // Buscar proyecto por ID
    @Transactional(readOnly = true)
    public Optional<Proyecto> buscarPorId(Long id) {
        log.debug("🔍 Buscando proyecto con ID: {}", id);
        return proyectoRepository.findById(id);
    }

    // Obtener proyecto por ID (lanza excepción si no existe)
    @Transactional(readOnly = true)
    public Proyecto obtenerPorId(Long id) {
        log.debug("📖 Obteniendo proyecto con ID: {}", id);
        return proyectoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado con ID: " + id));
    }

    // Actualizar proyecto existente
    public Proyecto actualizarProyecto(@Valid Proyecto proyecto) {
        log.info("📝 Actualizando proyecto ID: {}", proyecto.getId());

        // Verificar que el proyecto existe
        if (!proyectoRepository.existsById(proyecto.getId())) {
            throw new RuntimeException("No se puede actualizar. Proyecto no encontrado con ID: " + proyecto.getId());
        }

        // Validar NIT único (excluyendo el proyecto actual)
        validarNitUnico(proyecto.getNitCliente(), proyecto.getId());

        Proyecto proyectoActualizado = proyectoRepository.save(proyecto);
        log.info("✅ Proyecto actualizado exitosamente: {}", proyectoActualizado.getNombreObra());

        return proyectoActualizado;
    }

    // Eliminar proyecto por ID
    public void eliminarProyecto(Long id) {
        log.warn("🗑️ Eliminando proyecto con ID: {}", id);

        Proyecto proyecto = obtenerPorId(id);
        proyectoRepository.deleteById(id);

        log.warn("✅ Proyecto eliminado: {} - {}", id, proyecto.getNombreObra());
    }

    // =================== BÚSQUEDAS ESPECIALIZADAS ===================

    // Buscar proyecto por NIT del cliente
    @Transactional(readOnly = true)
    public Optional<Proyecto> buscarPorNit(String nitCliente) {
        log.debug("🔍 Buscando proyecto por NIT: {}", nitCliente);
        return proyectoRepository.findByNitCliente(nitCliente);
    }

    // Buscar proyectos por nombre de obra (búsqueda parcial)
    @Transactional(readOnly = true)
    public List<Proyecto> buscarPorNombreObra(String nombreObra) {
        log.debug("🔍 Buscando proyectos por nombre de obra: {}", nombreObra);
        return proyectoRepository.findByNombreObraContainingIgnoreCase(nombreObra);
    }

    // Buscar proyectos por nombre del cliente (búsqueda parcial)
    @Transactional(readOnly = true)
    public List<Proyecto> buscarPorNombreCliente(String nombreCliente) {
        log.debug("🔍 Buscando proyectos por nombre de cliente: {}", nombreCliente);
        return proyectoRepository.findByNombreClienteContainingIgnoreCase(nombreCliente);
    }

    // Buscar proyectos por estado específico
    @Transactional(readOnly = true)
    public List<Proyecto> buscarPorEstado(String estado) {
        log.debug("🔍 Buscando proyectos por estado: {}", estado);
        return proyectoRepository.findByEstado(estado);
    }

    // Listar solo proyectos activos
    @Transactional(readOnly = true)
    public List<Proyecto> listarProyectosActivos() {
        log.debug("📋 Listando proyectos activos");
        return proyectoRepository.findProyectosActivos();
    }

    // =================== VALIDACIONES DE NEGOCIO ===================

    // Validar que el NIT sea único (excluyendo un ID específico para edición)
    private void validarNitUnico(String nitCliente, Long idExcluir) {
        Long idParaExcluir = (idExcluir != null) ? idExcluir : -1L;

        boolean nitYaExiste = proyectoRepository.existsByNitClienteAndIdNot(nitCliente, idParaExcluir);

        if (nitYaExiste) {
            String mensaje = String.format("Ya existe un proyecto registrado con el NIT: %s", nitCliente);
            log.warn("❌ Validación fallida: {}", mensaje);
            throw new RuntimeException(mensaje);
        }
    }

    // Verificar si un proyecto existe por ID
    @Transactional(readOnly = true)
    public boolean existeProyecto(Long id) {
        return proyectoRepository.existsById(id);
    }

    // Verificar si un NIT ya está registrado
    @Transactional(readOnly = true)
    public boolean existeNit(String nitCliente) {
        return proyectoRepository.findByNitCliente(nitCliente).isPresent();
    }

    // =================== ESTADÍSTICAS Y CONTADORES ===================

    // Contar total de proyectos
    @Transactional(readOnly = true)
    public long contarTotalProyectos() {
        long total = proyectoRepository.count();
        log.debug("📊 Total de proyectos: {}", total);
        return total;
    }

    // Contar proyectos por estado específico
    @Transactional(readOnly = true)
    public long contarPorEstado(String estado) {
        long count = proyectoRepository.countByEstado(estado);
        log.debug("📊 Proyectos en estado '{}': {}", estado, count);
        return count;
    }

    // Obtener estadísticas completas de proyectos
    @Transactional(readOnly = true)
    public EstadisticasProyecto obtenerEstadisticas() {
        log.debug("📊 Generando estadísticas completas");

        return EstadisticasProyecto.builder()
                .totalProyectos(proyectoRepository.count())
                .proyectosActivos(proyectoRepository.countByEstado("ACTIVO"))
                .proyectosPausados(proyectoRepository.countByEstado("PAUSADO"))
                .proyectosFinalizados(proyectoRepository.countByEstado("FINALIZADO"))
                .proyectosCancelados(proyectoRepository.countByEstado("CANCELADO"))
                .build();
    }

    // =================== MÉTODOS UTILITARIOS ===================

    // Cambiar estado de un proyecto
    public Proyecto cambiarEstado(Long id, String nuevoEstado) {
        log.info("🔄 Cambiando estado del proyecto ID: {} a: {}", id, nuevoEstado);

        Proyecto proyecto = obtenerPorId(id);
        proyecto.setEstado(nuevoEstado);

        return proyectoRepository.save(proyecto);
    }

    // Activar proyecto (cambiar estado a ACTIVO)
    public Proyecto activarProyecto(Long id) {
        return cambiarEstado(id, "ACTIVO");
    }

    // Pausar proyecto (cambiar estado a PAUSADO)
    public Proyecto pausarProyecto(Long id) {
        return cambiarEstado(id, "PAUSADO");
    }

    // Finalizar proyecto (cambiar estado a FINALIZADO)
    public Proyecto finalizarProyecto(Long id) {
        return cambiarEstado(id, "FINALIZADO");
    }

    // Cancelar proyecto (cambiar estado a CANCELADO)
    public Proyecto cancelarProyecto(Long id) {
        return cambiarEstado(id, "CANCELADO");
    }

    // Verificar si un proyecto puede ser editado
    @Transactional(readOnly = true)
    public boolean puedeSerEditado(Long id) {
        return buscarPorId(id)
                .map(Proyecto::isEditable)
                .orElse(false);
    }

    // =================== CLASE INTERNA PARA ESTADÍSTICAS ===================

    // Record para encapsular estadísticas de proyectos
    public record EstadisticasProyecto(
            long totalProyectos,
            long proyectosActivos,
            long proyectosPausados,
            long proyectosFinalizados,
            long proyectosCancelados
    ) {
        // Builder pattern para construcción fluida
        public static EstadisticasProyectoBuilder builder() {
            return new EstadisticasProyectoBuilder();
        }

        public static class EstadisticasProyectoBuilder {
            private long totalProyectos;
            private long proyectosActivos;
            private long proyectosPausados;
            private long proyectosFinalizados;
            private long proyectosCancelados;

            public EstadisticasProyectoBuilder totalProyectos(long totalProyectos) {
                this.totalProyectos = totalProyectos;
                return this;
            }

            public EstadisticasProyectoBuilder proyectosActivos(long proyectosActivos) {
                this.proyectosActivos = proyectosActivos;
                return this;
            }

            public EstadisticasProyectoBuilder proyectosPausados(long proyectosPausados) {
                this.proyectosPausados = proyectosPausados;
                return this;
            }

            public EstadisticasProyectoBuilder proyectosFinalizados(long proyectosFinalizados) {
                this.proyectosFinalizados = proyectosFinalizados;
                return this;
            }

            public EstadisticasProyectoBuilder proyectosCancelados(long proyectosCancelados) {
                this.proyectosCancelados = proyectosCancelados;
                return this;
            }

            public EstadisticasProyecto build() {
                return new EstadisticasProyecto(
                        totalProyectos,
                        proyectosActivos,
                        proyectosPausados,
                        proyectosFinalizados,
                        proyectosCancelados
                );
            }
        }
    }

}

/*
================================================================================
DOCUMENTACIÓN COMPLETA DE ProyectoService.java
================================================================================

PROPÓSITO:
----------
Capa de servicio que contiene la lógica de negocio para la gestión de proyectos.
Esta clase actúa como intermediario entre el controlador y el repositorio,
implementando validaciones, transacciones y reglas de negocio específicas
del dominio de la aplicación.

INFORMACIÓN ACADÉMICA:
----------------------
Programa:     Análisis y Desarrollo de Software - SENA
Evidencia:    GA7-220501096-AA3-EV01
Capa:         Servicio (Lógica de Negocio)
Framework:    Spring Boot 3.2.0 + Spring Data JPA
Autor:        Nelson Rodriguez

ARQUITECTURA Y PATRÓN:
---------------------
- Implementa el patrón Service Layer del diseño por capas
- Actúa como fachada entre el controlador y el repositorio
- Encapsula la lógica de negocio y las validaciones
- Gestiona transacciones automáticamente con @Transactional
- Utiliza inyección de dependencias con @RequiredArgsConstructor

ANOTACIONES SPRING UTILIZADAS:
-----------------------------

1. @Service:
   - Marca la clase como componente de servicio de Spring
   - Permite detección automática durante el escaneo de componentes
   - Facilita la inyección de dependencias en otras capas

2. @Transactional:
   - Gestiona transacciones de base de datos automáticamente
   - Nivel de clase: todas las operaciones son transaccionales
   - @Transactional(readOnly = true): Optimiza consultas de solo lectura

3. @RequiredArgsConstructor (Lombok):
   - Genera constructor con campos final/required automáticamente
   - Elimina la necesidad de @Autowired explícito
   - Inmutabilidad del campo proyectoRepository

4. @Slf4j (Lombok):
   - Genera automáticamente logger estático
   - Facilita logging para auditoría y debugging

OPERACIONES CRUD IMPLEMENTADAS:
------------------------------

1. CREAR (Create):
   - crearProyecto(): Crea nuevo proyecto con validaciones
   - Valida NIT único antes de guardar
   - Logs de auditoría automáticos

2. LEER (Read):
   - listarTodosLosProyectos(): Lista todos ordenados por fecha
   - buscarPorId(): Búsqueda opcional por ID
   - obtenerPorId(): Búsqueda obligatoria por ID (lanza excepción)

3. ACTUALIZAR (Update):
   - actualizarProyecto(): Actualiza proyecto existente
   - Valida existencia antes de actualizar
   - Valida NIT único excluyendo el proyecto actual

4. ELIMINAR (Delete):
   - eliminarProyecto(): Eliminación por ID con logs de auditoría

BÚSQUEDAS ESPECIALIZADAS:
------------------------

1. Por Atributos Únicos:
   - buscarPorNit(): Búsqueda exacta por NIT del cliente

2. Por Atributos con Coincidencia Parcial:
   - buscarPorNombreObra(): Búsqueda por nombre de obra (case-insensitive)
   - buscarPorNombreCliente(): Búsqueda por cliente (case-insensitive)

3. Por Estado:
   - buscarPorEstado(): Filtra proyectos por estado específico
   - listarProyectosActivos(): Solo proyectos en estado ACTIVO

VALIDACIONES DE NEGOCIO:
-----------------------

1. Validación de NIT Único:
   - validarNitUnico(): Previene duplicación de NITs
   - Excluye el ID actual para permitir edición
   - Lanza RuntimeException si encuentra duplicados

2. Validaciones de Existencia:
   - existeProyecto(): Verifica existencia por ID
   - existeNit(): Verifica si NIT ya está registrado

3. Validaciones de Estado:
   - puedeSerEditado(): Verifica si proyecto puede modificarse

GESTIÓN DE ESTADOS:
------------------
Estados permitidos: ACTIVO, PAUSADO, FINALIZADO, CANCELADO

Métodos de cambio de estado:
- cambiarEstado(): Cambio genérico de estado
- activarProyecto(): Cambia a ACTIVO
- pausarProyecto(): Cambia a PAUSADO
- finalizarProyecto(): Cambia a FINALIZADO
- cancelarProyecto(): Cambia a CANCELADO

ESTADÍSTICAS Y MÉTRICAS:
-----------------------

1. Contadores Simples:
   - contarTotalProyectos(): Total de proyectos registrados
   - contarPorEstado(): Cuenta proyectos por estado específico

2. Estadísticas Completas:
   - obtenerEstadisticas(): Record con todas las métricas
   - Incluye conteos por cada estado posible

MANEJO DE TRANSACCIONES:
-----------------------

1. Transacciones de Escritura:
   - @Transactional en métodos que modifican datos
   - Rollback automático en caso de excepción
   - Commit automático al finalizar exitosamente

2. Transacciones de Solo Lectura:
   - @Transactional(readOnly = true) para consultas
   - Optimización de rendimiento
   - Previene modificaciones accidentales

LOGGING Y AUDITORÍA:
-------------------

1. Logs Informativos:
   - Creación y actualización de proyectos
   - Cambios de estado importantes

2. Logs de Debug:
   - Búsquedas y consultas
   - Estadísticas y conteos

3. Logs de Advertencia:
   - Eliminaciones de proyectos
   - Validaciones fallidas

CLASE INTERNA EstadisticasProyecto:
---------------------------------
Record que encapsula métricas del sistema:
- totalProyectos: Conteo total
- proyectosActivos: Proyectos en estado ACTIVO
- proyectosPausados: Proyectos en estado PAUSADO
- proyectosFinalizados: Proyectos en estado FINALIZADO
- proyectosCancelados: Proyectos en estado CANCELADO

Incluye patrón Builder para construcción fluida:
```java
EstadisticasProyecto stats = EstadisticasProyecto.builder()
    .totalProyectos(100)
    .proyectosActivos(75)
    .build();
```

MANEJO DE EXCEPCIONES:
---------------------

1. RuntimeException para Errores de Negocio:
   - Proyecto no encontrado
   - NIT duplicado
   - Validaciones fallidas

2. Propagación Automática:
   - Las excepciones se propagan al controlador
   - El controlador decide cómo presentar errores al usuario

INTEGRACIÓN CON OTRAS CAPAS:
---------------------------

1. Con Repositorio:
   - Inyección automática de ProyectoRepository
   - Uso de métodos derivados y consultas personalizadas
   - Aprovecha optimizaciones de Spring Data JPA

2. Con Controlador:
   - Proporciona interfaz limpia para operaciones de negocio
   - Encapsula complejidad de validaciones y transacciones
   - Facilita testing unitario independiente

VENTAJAS SOBRE DAO TRADICIONAL:
------------------------------
- Transacciones automáticas vs manejo manual
- Inyección de dependencias vs lookup manual
- Logging integrado vs implementación manual
- Validaciones centralizadas vs dispersas
- Reutilización de código vs duplicación

TESTING Y MANTENIBILIDAD:
------------------------

1. Facilidad para Testing:
   - Métodos públicos bien definidos
   - Dependencias inyectadas (fácil mocking)
   - Lógica de negocio aislada

2. Separación de Responsabilidades:
   - Controlador: Presentación y validación de entrada
   - Servicio: Lógica de negocio y validaciones
   - Repositorio: Acceso a datos

CONFIGURACIÓN REQUERIDA:
-----------------------
- ProyectoRepository debe estar disponible en el contexto
- Transacciones habilitadas en Spring Boot (automático)
- Logging configurado en application.properties

EJEMPLOS DE USO:
---------------
```java
@Autowired
private ProyectoService proyectoService;

// Crear proyecto
Proyecto nuevo = proyectoService.crearProyecto(proyecto);

// Buscar proyecto
Optional<Proyecto> encontrado = proyectoService.buscarPorId(1L);

// Listar todos
List<Proyecto> todos = proyectoService.listarTodosLosProyectos();

// Obtener estadísticas
EstadisticasProyecto stats = proyectoService.obtenerEstadisticas();
```

EVIDENCIA DE COMPETENCIAS:
-------------------------
1. 🎯 Implementación correcta del patrón Service Layer
2. 🏗️ Uso efectivo de transacciones automáticas
3. 💾 Integración con capa de persistencia
4. 📝 Validaciones de negocio robustas
5. 🔧 Manejo de estados y ciclo de vida de entidades
6. 🧪 Diseño orientado a testing
7. 📊 Implementación de métricas y estadísticas

AUTOR: Nelson Rodriguez
EVIDENCIA: GA7-220501096-AA3-EV01
FECHA: 2025-07-07
VERSIÓN: 3.0 (Spring Boot Service Layer)

================================================================================
*/