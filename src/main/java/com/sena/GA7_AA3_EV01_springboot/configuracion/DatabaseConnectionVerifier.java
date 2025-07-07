package com.sena.GA7_AA3_EV01_springboot.configuracion;

import com.sena.GA7_AA3_EV01_springboot.modelo.Proyecto;
import com.sena.GA7_AA3_EV01_springboot.servicio.ProyectoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseConnectionVerifier implements CommandLineRunner {

    // Inyección de dependencias para verificación
    private final DataSource dataSource;
    private final ProyectoService proyectoService;

    @Override
    public void run(String... args) throws Exception {
        log.info("\n" + "=".repeat(70));
        log.info("🔍 VERIFICANDO CONEXIÓN A BASE DE DATOS");
        log.info("=".repeat(70));

        try {
            // Verificar conexión directa a la base de datos
            verificarConexionDirecta();

            // Verificar funcionalidad de JPA/Hibernate
            verificarFuncionalidadJPA();

            // Crear datos de prueba si es necesario
            crearDatosDePruebaSiEsNecesario();

            // Mostrar estadísticas actuales
            mostrarEstadisticasBaseDatos();

            log.info("=".repeat(70));
            log.info("✅ VERIFICACIÓN DE BASE DE DATOS COMPLETADA EXITOSAMENTE");
            log.info("=".repeat(70));

        } catch (Exception e) {
            log.error("=".repeat(70));
            log.error("❌ ERROR EN VERIFICACIÓN DE BASE DE DATOS");
            log.error("=".repeat(70));
            log.error("Error: {}", e.getMessage(), e);
            throw e;
        }
    }

    // Verificar conexión directa usando DataSource
    private void verificarConexionDirecta() throws Exception {
        log.info("📡 Verificando conexión directa a MySQL...");

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();

            log.info("✅ Conexión establecida exitosamente");
            log.info("   ├── 🗄️  Base de datos: {}", metaData.getDatabaseProductName());
            log.info("   ├── 📊 Versión: {}", metaData.getDatabaseProductVersion());
            log.info("   ├── 🔗 URL: {}", metaData.getURL());
            log.info("   ├── 👤 Usuario: {}", metaData.getUserName());
            log.info("   ├── 🚀 Driver: {}", metaData.getDriverName());
            log.info("   └── 📈 Versión Driver: {}", metaData.getDriverVersion());

        } catch (Exception e) {
            log.error("❌ Error al conectar con la base de datos: {}", e.getMessage());
            throw e;
        }
    }

    // Verificar que JPA/Hibernate funciona correctamente
    private void verificarFuncionalidadJPA() {
        log.info("🏗️ Verificando funcionalidad de JPA/Hibernate...");

        try {
            // Intentar contar registros (esto activará la creación de tablas si no existen)
            long totalProyectos = proyectoService.contarTotalProyectos();
            log.info("✅ JPA/Hibernate funcionando correctamente");
            log.info("   └── 📊 Proyectos en base de datos: {}", totalProyectos);

        } catch (Exception e) {
            log.error("❌ Error en funcionalidad JPA: {}", e.getMessage());
            throw e;
        }
    }

    // Crear datos de prueba si la base de datos está vacía
    private void crearDatosDePruebaSiEsNecesario() {
        log.info("🧪 Verificando necesidad de datos de prueba...");

        long totalProyectos = proyectoService.contarTotalProyectos();

        if (totalProyectos == 0) {
            log.info("📝 Base de datos vacía. Creando datos de prueba...");
            crearProyectosDePrueba();
        } else {
            log.info("✅ Base de datos contiene {} proyecto(s) existente(s)", totalProyectos);
        }
    }

    // Crear proyectos de ejemplo para demostración académica
    private void crearProyectosDePrueba() {
        try {
            // Proyecto 1: Torre Empresarial
            Proyecto proyecto1 = Proyecto.builder()
                    .nombreObra("Torre Empresarial Centro")
                    .direccionObra("Carrera 15 # 93-47, Bogotá, Colombia")
                    .nitCliente("900123456-7")
                    .nombreCliente("Constructora Bogotá S.A.S")
                    .emailContacto("proyectos@constructorabogota.com")
                    .telefonoContacto("3201234567")
                    .estado("ACTIVO")
                    .build();

            proyectoService.crearProyecto(proyecto1);
            log.info("   ✅ Creado: {}", proyecto1.getNombreObra());

            // Proyecto 2: Centro Comercial
            Proyecto proyecto2 = Proyecto.builder()
                    .nombreObra("Centro Comercial Plaza Norte")
                    .direccionObra("Autopista Norte Km 18, Chía, Cundinamarca")
                    .nitCliente("800987654-3")
                    .nombreCliente("Desarrollos Inmobiliarios del Norte Ltda")
                    .emailContacto("info@desarrollosnorte.co")
                    .telefonoContacto("3107654321")
                    .estado("ACTIVO")
                    .build();

            proyectoService.crearProyecto(proyecto2);
            log.info("   ✅ Creado: {}", proyecto2.getNombreObra());

            // Proyecto 3: Conjunto Residencial (Pausado)
            Proyecto proyecto3 = Proyecto.builder()
                    .nombreObra("Conjunto Residencial Los Cerezos")
                    .direccionObra("Calle 127 # 52-14, Bogotá, Colombia")
                    .nitCliente("700456789-1")
                    .nombreCliente("Vivienda y Desarrollo S.A.")
                    .emailContacto("construccion@viviendaydesarrollo.com")
                    .telefonoContacto("3159876543")
                    .estado("PAUSADO")
                    .build();

            proyectoService.crearProyecto(proyecto3);
            log.info("   ✅ Creado: {}", proyecto3.getNombreObra());

            // Proyecto 4: Proyecto Finalizado
            Proyecto proyecto4 = Proyecto.builder()
                    .nombreObra("Edificio de Oficinas Zona Rosa")
                    .direccionObra("Calle 85 # 12-34, Bogotá, Colombia")
                    .nitCliente("600321987-5")
                    .nombreCliente("Inversiones Zona Rosa S.A.S")
                    .emailContacto("obras@inversioneszonarosa.com")
                    .telefonoContacto("3142345678")
                    .estado("FINALIZADO")
                    .build();

            proyectoService.crearProyecto(proyecto4);
            log.info("   ✅ Creado: {}", proyecto4.getNombreObra());

            // Proyecto 5: Proyecto Cancelado
            Proyecto proyecto5 = Proyecto.builder()
                    .nombreObra("Hotel Boutique La Candelaria")
                    .direccionObra("Carrera 4 # 12-15, Bogotá, Colombia")
                    .nitCliente("500654321-9")
                    .nombreCliente("Turismo y Hotelería Colombia S.A.")
                    .emailContacto("desarrollo@turismohoteleria.co")
                    .telefonoContacto("3113456789")
                    .estado("CANCELADO")
                    .build();

            proyectoService.crearProyecto(proyecto5);
            log.info("   ✅ Creado: {}", proyecto5.getNombreObra());

            log.info("🎯 Datos de prueba creados exitosamente (5 proyectos)");

        } catch (Exception e) {
            log.error("❌ Error creando datos de prueba: {}", e.getMessage());
            // No lanzamos excepción aquí para no interrumpir el inicio de la aplicación
        }
    }

    // Mostrar estadísticas actuales de la base de datos
    private void mostrarEstadisticasBaseDatos() {
        log.info("📊 Estadísticas actuales de la base de datos:");

        try {
            var estadisticas = proyectoService.obtenerEstadisticas();

            log.info("   ├── 📈 Total proyectos: {}", estadisticas.totalProyectos());
            log.info("   ├── 🟢 Proyectos activos: {}", estadisticas.proyectosActivos());
            log.info("   ├── 🟡 Proyectos pausados: {}", estadisticas.proyectosPausados());
            log.info("   ├── ✅ Proyectos finalizados: {}", estadisticas.proyectosFinalizados());
            log.info("   └── ❌ Proyectos cancelados: {}", estadisticas.proyectosCancelados());

            // Mostrar información de tiempo
            log.info("📅 Información de verificación:");
            log.info("   └── ⏰ Verificación realizada: {}", LocalDateTime.now());

        } catch (Exception e) {
            log.error("❌ Error obteniendo estadísticas: {}", e.getMessage());
        }
    }

}

/*
================================================================================
DOCUMENTACIÓN COMPLETA DE DatabaseConnectionVerifier.java
================================================================================

PROPÓSITO:
----------
Componente que verifica automáticamente la conexión a la base de datos al iniciar
la aplicación Spring Boot. Proporciona información detallada sobre el estado de
la conexión, crea datos de prueba si es necesario y muestra estadísticas para
validación académica.

INFORMACIÓN ACADÉMICA:
----------------------
Programa:     Análisis y Desarrollo de Software - SENA
Evidencia:    GA7-220501096-AA3-EV01
Funcionalidad: Verificación de conexión y inicialización de datos
Framework:    Spring Boot 3.2.0 + JPA/Hibernate
Autor:        Nelson Rodriguez

PATRÓN IMPLEMENTADO:
-------------------
- CommandLineRunner: Se ejecuta automáticamente después del inicio de Spring Boot
- Dependency Injection: Inyección de DataSource y ProyectoService
- Fail-Fast: Detecta problemas de conexión inmediatamente al inicio

ANOTACIONES UTILIZADAS:
----------------------

1. @Component:
   - Registra la clase como bean de Spring
   - Permite detección automática en el escaneo de componentes
   - Se ejecuta como parte del ciclo de vida de la aplicación

2. @RequiredArgsConstructor (Lombok):
   - Genera constructor con campos final automáticamente
   - Inyección de dependencias sin @Autowired explícito
   - Inmutabilidad de dependencias inyectadas

3. @Slf4j (Lombok):
   - Genera logger automáticamente
   - Facilita logging detallado para verificación académica

FUNCIONALIDADES IMPLEMENTADAS:
-----------------------------

1. VERIFICACIÓN DE CONEXIÓN DIRECTA:
   - Obtiene conexión directa desde DataSource
   - Extrae metadatos de la base de datos
   - Muestra información técnica: BD, versión, driver, usuario

2. VERIFICACIÓN DE JPA/HIBERNATE:
   - Prueba funcionalidad de Spring Data JPA
   - Verifica que las tablas se crean automáticamente
   - Confirma que las consultas funcionan correctamente

3. CREACIÓN DE DATOS DE PRUEBA:
   - Detecta si la base de datos está vacía
   - Crea 5 proyectos de ejemplo para demostración
   - Incluye diferentes estados: ACTIVO, PAUSADO, FINALIZADO, CANCELADO

4. ESTADÍSTICAS Y MÉTRICAS:
   - Muestra conteos por estado
   - Información de tiempo de verificación
   - Logs detallados para seguimiento académico

DATOS DE PRUEBA CREADOS:
-----------------------

1. Torre Empresarial Centro (ACTIVO):
   - Cliente: Constructora Bogotá S.A.S
   - NIT: 900123456-7
   - Ubicación: Bogotá Centro

2. Centro Comercial Plaza Norte (ACTIVO):
   - Cliente: Desarrollos Inmobiliarios del Norte Ltda
   - NIT: 800987654-3
   - Ubicación: Chía, Cundinamarca

3. Conjunto Residencial Los Cerezos (PAUSADO):
   - Cliente: Vivienda y Desarrollo S.A.
   - NIT: 700456789-1
   - Ubicación: Bogotá Norte

4. Edificio de Oficinas Zona Rosa (FINALIZADO):
   - Cliente: Inversiones Zona Rosa S.A.S
   - NIT: 600321987-5
   - Ubicación: Zona Rosa, Bogotá

5. Hotel Boutique La Candelaria (CANCELADO):
   - Cliente: Turismo y Hotelería Colombia S.A.
   - NIT: 500654321-9
   - Ubicación: La Candelaria, Bogotá

INFORMACIÓN EXTRAÍDA DE LA BASE DE DATOS:
----------------------------------------

1. Metadatos de Conexión:
   - Nombre y versión de la base de datos (MySQL)
   - URL de conexión completa
   - Usuario conectado
   - Driver JDBC utilizado y versión

2. Estado de JPA/Hibernate:
   - Confirmación de creación automática de tablas
   - Verificación de consultas básicas
   - Conteo inicial de registros

3. Estadísticas Operacionales:
   - Total de proyectos por estado
   - Timestamp de verificación
   - Logs de operaciones realizadas

LOGS GENERADOS:
--------------

1. Logs Informativos:
   - Inicio y fin de verificación
   - Información de conexión exitosa
   - Estadísticas de datos creados

2. Logs de Debug:
   - Metadatos técnicos de la base de datos
   - Detalles de creación de proyectos

3. Logs de Error:
   - Problemas de conexión
   - Errores en creación de datos
   - Excepciones con stack trace

MANEJO DE ERRORES:
-----------------

1. Errores Críticos (interrumpen inicio):
   - Falla de conexión a base de datos
   - Error en verificación de JPA

2. Errores No Críticos (no interrumpen inicio):
   - Error en creación de datos de prueba
   - Error en obtención de estadísticas

INTEGRACIÓN CON SPRING BOOT:
---------------------------

1. Ciclo de Vida:
   - Se ejecuta después de que Spring Boot complete la inicialización
   - Antes de que la aplicación esté lista para recibir requests
   - Permite detectar problemas antes de uso en producción

2. Dependencias:
   - DataSource: Para verificación directa de conexión
   - ProyectoService: Para verificación de funcionalidad de negocio

VENTAJAS PARA DESARROLLO Y TESTING:
----------------------------------

1. Detección Temprana de Problemas:
   - Identifica problemas de configuración inmediatamente
   - Evita errores silenciosos durante ejecución

2. Datos Consistentes para Testing:
   - Siempre hay datos de prueba disponibles
   - Estados variados para probar diferentes escenarios

3. Información Técnica Visible:
   - Los desarrolladores ven estado de conexión en logs
   - Facilita debugging y verificación

CONFIGURACIÓN REQUERIDA:
-----------------------

1. application.properties:
   - Configuración correcta de datasource MySQL
   - Credenciales de base de datos válidas
   - Configuración de JPA/Hibernate

2. Base de Datos:
   - MySQL server corriendo (XAMPP)
   - Base de datos cartillasacerowebga7aa3ev01 accesible
   - Permisos adecuados para el usuario

EJEMPLOS DE SALIDA DE LOGS:
--------------------------
```
======================================================================
🔍 VERIFICANDO CONEXIÓN A BASE DE DATOS
======================================================================
📡 Verificando conexión directa a MySQL...
✅ Conexión establecida exitosamente
   ├── 🗄️  Base de datos: MySQL
   ├── 📊 Versión: 8.0.30
   ├── 🔗 URL: jdbc:mysql://localhost:3306/cartillasacerowebga7aa3ev01
   └── 👤 Usuario: root

🏗️ Verificando funcionalidad de JPA/Hibernate...
✅ JPA/Hibernate funcionando correctamente
   └── 📊 Proyectos en base de datos: 5

📊 Estadísticas actuales de la base de datos:
   ├── 📈 Total proyectos: 5
   ├── 🟢 Proyectos activos: 2
   ├── 🟡 Proyectos pausados: 1
   ├── ✅ Proyectos finalizados: 1
   └── ❌ Proyectos cancelados: 1
======================================================================
✅ VERIFICACIÓN DE BASE DE DATOS COMPLETADA EXITOSAMENTE
======================================================================
```

EVIDENCIA DE COMPETENCIAS:
-------------------------
1. 🎯 Implementación de CommandLineRunner
2. 🏗️ Verificación de configuración de DataSource
3. 💾 Validación de funcionalidad JPA/Hibernate
4. 📝 Creación programática de datos de prueba
5. 🔧 Manejo de excepciones y logging
6. 🧪 Preparación de entorno para testing
7. 📊 Generación de métricas y estadísticas

UTILIDAD ACADÉMICA:
------------------
- Demuestra comprensión de configuración de base de datos
- Muestra dominio de Spring Boot lifecycle
- Evidencia capacidad de crear datos de prueba
- Proporciona información técnica detallada para evaluación
- Facilita verificación inmediata de funcionalidad

AUTOR: Nelson Rodriguez
EVIDENCIA: GA7-220501096-AA3-EV01
FECHA: 2025-07-07
VERSIÓN: 3.0 (Spring Boot Database Verification)

================================================================================
*/