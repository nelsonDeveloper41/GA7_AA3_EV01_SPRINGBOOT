package com.sena.GA7_AA3_EV01_springboot.repositorio;

import com.sena.GA7_AA3_EV01_springboot.modelo.Proyecto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProyectoRepository extends JpaRepository<Proyecto, Long> {

    // Buscar proyecto por NIT exacto
    Optional<Proyecto> findByNitCliente(String nitCliente);

    // Buscar proyectos por nombre de obra (parcial, ignorando mayúsculas)
    List<Proyecto> findByNombreObraContainingIgnoreCase(String nombreObra);

    // Buscar proyectos por nombre de cliente (parcial, ignorando mayúsculas)
    List<Proyecto> findByNombreClienteContainingIgnoreCase(String nombreCliente);

    // Buscar proyectos por estado
    List<Proyecto> findByEstado(String estado);

    // Verificar si existe un proyecto con el mismo NIT (excluyendo un ID específico)
    @Query("SELECT COUNT(p) > 0 FROM Proyecto p WHERE p.nitCliente = :nit AND p.id <> :id")
    boolean existsByNitClienteAndIdNot(@Param("nit") String nitCliente, @Param("id") Long id);

    // Obtener proyectos ordenados por fecha de creación descendente
    List<Proyecto> findAllByOrderByFechaCreacionDesc();

    // Contar proyectos por estado
    @Query("SELECT COUNT(p) FROM Proyecto p WHERE p.estado = :estado")
    long countByEstado(@Param("estado") String estado);

    // Buscar proyectos activos (estado 'ACTIVO')
    @Query("SELECT p FROM Proyecto p WHERE p.estado = 'ACTIVO' ORDER BY p.fechaCreacion DESC")
    List<Proyecto> findProyectosActivos();

}

/*
================================================================================
DOCUMENTACIÓN COMPLETA DE ProyectoRepository.java
================================================================================

PROPÓSITO:
----------
Esta interfaz define el repositorio de acceso a datos para la entidad Proyecto
utilizando Spring Data JPA. Reemplaza la anterior implementación con JDBC,
eliminando cientos de líneas de código boilerplate y proporcionando una
interfaz limpia y moderna para las operaciones de base de datos.

ARQUITECTURA Y PATRÓN:
---------------------
- Implementa el patrón Repository de Domain Driven Design (DDD)
- Utiliza Spring Data JPA para la generación automática de implementaciones
- Extiende JpaRepository<Proyecto, Long> que proporciona métodos CRUD básicos
- Spring Boot automáticamente crea un bean de esta interfaz en tiempo de ejecución

FUNCIONALIDADES IMPLEMENTADAS:
-----------------------------

1. OPERACIONES CRUD AUTOMÁTICAS (heredadas de JpaRepository):
   - save(proyecto): Inserta o actualiza un proyecto
   - findById(id): Busca proyecto por ID
   - findAll(): Lista todos los proyectos
   - deleteById(id): Elimina proyecto por ID
   - count(): Cuenta total de proyectos
   - existsById(id): Verifica si existe un proyecto

2. MÉTODOS DE CONSULTA DERIVADOS:
   - findByNitCliente(): Busca por NIT exacto
   - findByNombreObraContainingIgnoreCase(): Búsqueda parcial por nombre de obra
   - findByNombreClienteContainingIgnoreCase(): Búsqueda parcial por cliente
   - findByEstado(): Filtra por estado del proyecto
   - findAllByOrderByFechaCreacionDesc(): Lista ordenada por fecha

3. CONSULTAS PERSONALIZADAS CON @Query:
   - existsByNitClienteAndIdNot(): Valida NIT único al editar
   - countByEstado(): Cuenta proyectos por estado
   - findProyectosActivos(): Lista solo proyectos activos

VENTAJAS SOBRE JDBC TRADICIONAL:
-------------------------------
- Eliminación de código boilerplate (conexiones, PreparedStatement, ResultSet)
- Type-safety en tiempo de compilación
- Generación automática de SQL optimizado
- Manejo automático de transacciones
- Integración nativa con el contexto de Spring
- Facilidad para testing con @DataJpaTest
- Paginación y sorting automáticos disponibles

CONFIGURACIÓN REQUERIDA:
-----------------------
- application.properties debe contener configuración de datasource
- Entidad Proyecto debe estar anotada con @Entity
- Esta interfaz debe estar en un paquete escaneado por @ComponentScan

EJEMPLOS DE USO EN EL SERVICIO:
------------------------------
```java
@Autowired
private ProyectoRepository proyectoRepository;

// Guardar proyecto
Proyecto proyecto = new Proyecto();
proyectoRepository.save(proyecto);

// Buscar por NIT
Optional<Proyecto> proyecto = proyectoRepository.findByNitCliente("123456789");

// Listar todos ordenados
List<Proyecto> proyectos = proyectoRepository.findAllByOrderByFechaCreacionDesc();
```

CONVENCIONES DE NAMING:
----------------------
Spring Data JPA utiliza convenciones de nombres para generar automáticamente
las consultas SQL. Los métodos que comienzan con:
- findBy: Genera SELECT
- countBy: Genera SELECT COUNT
- deleteBy: Genera DELETE
- existsBy: Genera SELECT con EXISTS

EVIDENCIA ACADÉMICA:
-------------------
- Demuestra dominio de Spring Data JPA
- Implementación del patrón Repository
- Uso de anotaciones Spring (@Repository, @Query, @Param)
- Consultas derivadas y personalizadas
- Migración exitosa de JDBC a JPA

AUTOR: Nelson Rodriguez
EVIDENCIA: GA7-220501096-AA3-EV01
FECHA: 2025-07-07
VERSIÓN: 2.0 (Migrado de JDBC a Spring Data JPA)

================================================================================
*/