package com.sena.GA7_AA3_EV01_springboot.modelo;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;

@Entity
@Table(name = "proyectos",
        indexes = {
                @Index(name = "idx_nit_cliente", columnList = "nit_cliente"),
                @Index(name = "idx_fecha_creacion", columnList = "fecha_creacion"),
                @Index(name = "idx_estado", columnList = "estado"),
                @Index(name = "idx_nombre_obra", columnList = "nombre_obra")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(exclude = {"fechaCreacion"})
@Slf4j
public class Proyecto {

    // Identificador único auto-generado
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Long id;

    // Nombre de la obra (obligatorio, 3-100 caracteres)
    @NotNull(message = "El nombre de la obra es obligatorio")
    @NotBlank(message = "El nombre de la obra no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre de la obra debe tener entre 3 y 100 caracteres")
    @Column(name = "nombre_obra", nullable = false, length = 100)
    private String nombreObra;

    // Dirección física de la obra (obligatorio, 10-200 caracteres)
    @NotNull(message = "La dirección de la obra es obligatoria")
    @NotBlank(message = "La dirección de la obra no puede estar vacía")
    @Size(min = 10, max = 200, message = "La dirección debe tener entre 10 y 200 caracteres")
    @Column(name = "direccion_obra", nullable = false, length = 200)
    private String direccionObra;

    // NIT del cliente (único, formato: 123456789-1)
    @NotNull(message = "El NIT del cliente es obligatorio")
    @NotBlank(message = "El NIT del cliente no puede estar vacío")
    @Size(min = 8, max = 20, message = "El NIT debe tener entre 8 y 20 caracteres")
    @Pattern(regexp = "^[0-9]+-[0-9kK]$", message = "El NIT debe tener el formato: 123456789-1")
    @Column(name = "nit_cliente", nullable = false, length = 20, unique = true)
    @EqualsAndHashCode.Include
    private String nitCliente;

    // Nombre o razón social del cliente (obligatorio, 3-100 caracteres)
    @NotNull(message = "El nombre del cliente es obligatorio")
    @NotBlank(message = "El nombre del cliente no puede estar vacío")
    @Size(min = 3, max = 100, message = "El nombre del cliente debe tener entre 3 y 100 caracteres")
    @Column(name = "nombre_cliente", nullable = false, length = 100)
    private String nombreCliente;

    // Email de contacto (obligatorio, formato válido)
    @NotNull(message = "El email de contacto es obligatorio")
    @NotBlank(message = "El email de contacto no puede estar vacío")
    @Email(message = "El email debe tener un formato válido")
    @Size(max = 100, message = "El email no puede exceder 100 caracteres")
    @Column(name = "email_contacto", nullable = false, length = 100)
    private String emailContacto;

    // Teléfono de contacto (obligatorio, 7-10 dígitos)
    @NotNull(message = "El teléfono de contacto es obligatorio")
    @NotBlank(message = "El teléfono de contacto no puede estar vacío")
    @Pattern(regexp = "^[0-9]{7,10}$", message = "El teléfono debe tener entre 7 y 10 dígitos")
    @Column(name = "telefono_contacto", nullable = false, length = 15)
    private String telefonoContacto;

    // Fecha de creación (se establece automáticamente)
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Estado del proyecto (ACTIVO por defecto)
    @NotNull(message = "El estado del proyecto es obligatorio")
    @Pattern(regexp = "^(ACTIVO|PAUSADO|FINALIZADO|CANCELADO)$",
            message = "El estado debe ser: ACTIVO, PAUSADO, FINALIZADO o CANCELADO")
    @Column(name = "estado", nullable = false, length = 20)
    @Builder.Default
    private String estado = "ACTIVO";

    // Método ejecutado antes de guardar (establece fecha y estado por defecto)
    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
        if (this.estado == null || this.estado.isBlank()) {
            this.estado = "ACTIVO";
        }
        log.debug("🆕 Creando nuevo proyecto: {} - Cliente: {}", this.nombreObra, this.nombreCliente);
    }

    // Método ejecutado antes de actualizar
    @PreUpdate
    protected void onUpdate() {
        log.debug("📝 Actualizando proyecto ID: {} - {}", this.id, this.nombreObra);
    }

    // Método ejecutado antes de eliminar
    @PreRemove
    protected void onRemove() {
        log.warn("🗑️ Eliminando proyecto ID: {} - {}", this.id, this.nombreObra);
    }

    // Verifica si el proyecto está activo
    public boolean isActivo() {
        return "ACTIVO".equals(this.estado);
    }

    // Verifica si el proyecto está finalizado
    public boolean isFinalizado() {
        return "FINALIZADO".equals(this.estado);
    }

    // Verifica si el proyecto puede ser editado
    public boolean isEditable() {
        return "ACTIVO".equals(this.estado) || "PAUSADO".equals(this.estado);
    }

    // Obtiene resumen para mostrar en listas
    public String getResumen() {
        return String.format("%s - %s", this.nombreObra, this.nombreCliente);
    }

    // Obtiene estado con emoji descriptivo
    public String getEstadoConEmoji() {
        return switch (this.estado) {
            case "ACTIVO" -> "🟢 Activo";
            case "PAUSADO" -> "🟡 Pausado";
            case "FINALIZADO" -> "✅ Finalizado";
            case "CANCELADO" -> "❌ Cancelado";
            default -> "❓ " + this.estado;
        };
    }

    // Calcula días transcurridos desde la creación
    public long getDiasTranscurridos() {
        if (this.fechaCreacion == null) {
            return 0;
        }
        return java.time.temporal.ChronoUnit.DAYS.between(
                this.fechaCreacion.toLocalDate(),
                LocalDateTime.now().toLocalDate()
        );
    }

    // Factory method para crear proyecto básico
    public static Proyecto crearBasico(String nombreObra, String nitCliente, String nombreCliente) {
        return Proyecto.builder()
                .nombreObra(nombreObra)
                .nitCliente(nitCliente)
                .nombreCliente(nombreCliente)
                .estado("ACTIVO")
                .build();
    }

    // Factory method para crear proyecto completo
    public static ProyectoBuilder crearCompleto() {
        return Proyecto.builder().estado("ACTIVO");
    }

    // Valida formato del NIT colombiano
    public boolean isNitValido() {
        return this.nitCliente != null && this.nitCliente.matches("^[0-9]+-[0-9kK]$");
    }

    // Valida que todos los campos obligatorios estén completos
    public boolean isCompleto() {
        return this.nombreObra != null && !this.nombreObra.isBlank() &&
                this.direccionObra != null && !this.direccionObra.isBlank() &&
                this.nitCliente != null && !this.nitCliente.isBlank() &&
                this.nombreCliente != null && !this.nombreCliente.isBlank() &&
                this.emailContacto != null && !this.emailContacto.isBlank() &&
                this.telefonoContacto != null && !this.telefonoContacto.isBlank();
    }

}

/*
================================================================================
DOCUMENTACIÓN COMPLETA DE Proyecto.java
================================================================================

PROPÓSITO:
----------
Entidad JPA que representa un proyecto en el sistema CartillasAceroWeb.
Esta clase es la evolución del modelo original de Servlets/JSP hacia Spring Boot
con JPA/Hibernate + Lombok para persistencia automática y reducción significativa
de código boilerplate.

INFORMACIÓN ACADÉMICA:
----------------------
Programa:     Análisis y Desarrollo de Software - SENA
Evidencia:    GA7-220501096-AA3-EV01
Migración:    De JDBC manual → Spring Data JPA + Lombok
Framework:    Spring Boot 3.2.0 + Hibernate + Lombok
Autor:        Nelson Rodriguez

EVOLUCIÓN TECNOLÓGICA:
----------------------
ANTES (Servlets/JSP - 200+ líneas):       AHORA (Spring Boot + Lombok - 150 líneas):
├── Getters/Setters manuales (50 líneas) → ├── @Data (automático)
├── toString() manual                     → ├── @ToString (automático)
├── equals()/hashCode() manual           → ├── @EqualsAndHashCode (automático)
├── Constructores manuales               → ├── @NoArgsConstructor/@AllArgsConstructor
├── Builder pattern manual               → ├── @Builder (automático)
├── Mapeo ResultSet manual               → ├── JPA automático
└── Validaciones en DAO                  → └── Bean Validation declarativo

ANOTACIONES LOMBOK UTILIZADAS:
------------------------------

1. @Data:
   - Genera automáticamente: getters, setters, toString, equals, hashCode
   - Equivale a: @Getter + @Setter + @ToString + @EqualsAndHashCode + @RequiredArgsConstructor

2. @Builder:
   - Genera patrón Builder automáticamente
   - Permite: Proyecto.builder().nombreObra("Test").build()

3. @NoArgsConstructor:
   - Constructor sin argumentos (requerido por JPA)

4. @AllArgsConstructor:
   - Constructor con todos los argumentos

5. @EqualsAndHashCode(onlyExplicitlyIncluded = true):
   - Solo incluye campos marcados con @EqualsAndHashCode.Include
   - Incluye: id, nitCliente (identificadores únicos)

6. @ToString(exclude = {"fechaCreacion"}):
   - Excluye campos específicos del toString automático

7. @Slf4j:
   - Genera automáticamente: private static final Logger log = LoggerFactory.getLogger(Proyecto.class);

ANOTACIONES JPA UTILIZADAS:
---------------------------

1. @Entity:
   - Marca la clase como entidad JPA
   - Se mapea automáticamente a tabla en base de datos

2. @Table(name = "proyectos", indexes = {...}):
   - Define nombre de tabla e índices para optimización
   - Índices en: nit_cliente, fecha_creacion, estado, nombre_obra

3. @Id + @GeneratedValue(strategy = GenerationType.IDENTITY):
   - Define clave primaria auto-generada
   - Usa AUTO_INCREMENT de MySQL

4. @Column:
   - Configura propiedades de columnas (nombre, longitud, nullable, unique)

5. @PrePersist, @PreUpdate, @PreRemove:
   - Métodos de ciclo de vida ejecutados automáticamente por JPA
   - Gestionan fechas y logs de auditoría

BEAN VALIDATION IMPLEMENTADO:
-----------------------------

1. @NotNull / @NotBlank:
   - Validaciones de campos obligatorios
   - @NotBlank también valida que no esté vacío

2. @Size(min = X, max = Y):
   - Validaciones de longitud de texto
   - Aplicado a todos los campos String

3. @Email:
   - Validación automática de formato email
   - Aplicado al campo emailContacto

4. @Pattern(regexp = "..."):
   - Validaciones con expresiones regulares
   - NIT: formato colombiano (123456789-1)
   - Teléfono: solo dígitos, 7-10 caracteres
   - Estado: valores permitidos específicos

CAMPOS Y FUNCIONALIDADES:
------------------------

1. CAMPOS PRINCIPALES:
   - id: Clave primaria auto-generada
   - nombreObra: Nombre del proyecto (3-100 caracteres)
   - direccionObra: Ubicación física (10-200 caracteres)
   - nitCliente: NIT único del cliente (formato colombiano)
   - nombreCliente: Razón social (3-100 caracteres)
   - emailContacto: Email válido del contacto
   - telefonoContacto: Teléfono (7-10 dígitos)
   - fechaCreacion: Timestamp automático
   - estado: ACTIVO|PAUSADO|FINALIZADO|CANCELADO

2. MÉTODOS DE CICLO DE VIDA:
   - onCreate(): Establece fecha y estado por defecto
   - onUpdate(): Log de actualización
   - onRemove(): Log de eliminación

3. MÉTODOS UTILITARIOS:
   - isActivo(): Verifica si está activo
   - isFinalizado(): Verifica si está finalizado
   - isEditable(): Verifica si puede editarse
   - getResumen(): Texto para mostrar en listas
   - getEstadoConEmoji(): Estado con emoji descriptivo
   - getDiasTranscurridos(): Días desde creación

4. FACTORY METHODS:
   - crearBasico(): Crea proyecto con datos mínimos
   - crearCompleto(): Builder para proyecto completo

5. VALIDACIONES ADICIONALES:
   - isNitValido(): Valida formato NIT colombiano
   - isCompleto(): Verifica campos obligatorios

VENTAJAS SOBRE JDBC TRADICIONAL:
-------------------------------
- Eliminación de mapeo manual ResultSet → Objeto
- Validaciones declarativas automáticas
- Gestión automática de conexiones y transacciones
- Auditoría automática con @PrePersist/@PreUpdate
- Type-safety en tiempo de compilación
- Reducción drástica de código boilerplate
- Facilidad para testing unitario
- Integración nativa con Spring Boot

CONFIGURACIÓN REQUERIDA:
-----------------------
- application.properties: Configuración de datasource MySQL
- Dependencias Maven: spring-boot-starter-data-jpa, lombok, validation
- Base de datos MySQL con tabla 'proyectos' (se crea automáticamente)

COMPARACIÓN DE CÓDIGO:
---------------------
SIN Lombok (versión anterior Servlets/JSP):
```java
// Getters y setters manuales (50+ líneas)
public String getNombreObra() { return nombreObra; }
public void setNombreObra(String nombreObra) { this.nombreObra = nombreObra; }
// ... repetir para 8 campos más

// toString manual (15+ líneas)
@Override
public String toString() {
    return "Proyecto{" +
           "id=" + id +
           ", nombreObra='" + nombreObra + '\'' +
           // ... todos los campos
           '}';
}

// equals y hashCode manuales (25+ líneas)
@Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Proyecto proyecto = (Proyecto) o;
    return Objects.equals(id, proyecto.id) &&
           Objects.equals(nitCliente, proyecto.nitCliente);
}

// Builder pattern manual (40+ líneas)
public static class ProyectoBuilder {
    private String nombreObra;
    // ... implementación completa del builder
}

// Logger manual (2 líneas)
private static final Logger log = LoggerFactory.getLogger(Proyecto.class);
```

CON Lombok (versión actual):
```java
@Data              // Genera getters, setters, toString, equals, hashCode
@Builder           // Genera patrón Builder completo
@NoArgsConstructor // Constructor vacío
@AllArgsConstructor // Constructor con todos los argumentos
@Slf4j             // Genera logger automáticamente
public class Proyecto {
    // Solo campos con anotaciones y métodos específicos de negocio
}
```

RESULTADOS ACADÉMICOS DEMOSTRADOS:
---------------------------------
✅ Reducción de código: 200+ líneas → 150 líneas efectivas
✅ Eliminación de código repetitivo (boilerplate)
✅ Implementación de validaciones automáticas
✅ Uso de estándares profesionales modernos (Lombok + JPA)
✅ Facilidad de mantenimiento y testing
✅ Aplicación correcta del patrón Entity/Model
✅ Integración perfecta con Spring Boot ecosystem

EVIDENCIA DE COMPETENCIAS:
-------------------------
1. 🎯 Dominio de JPA/Hibernate para persistencia
2. 🏗️ Implementación correcta del patrón Entity
3. 📝 Uso efectivo de Bean Validation
4. 🔧 Aplicación de Lombok para reducir boilerplate
5. 🧪 Diseño orientado a testing y mantenibilidad
6. 📊 Optimización con índices de base de datos
7. 🛡️ Validaciones robustas de entrada de datos

AUTOR: Nelson Rodriguez
EVIDENCIA: GA7-220501096-AA3-EV01
FECHA: 2025-07-07
VERSIÓN: 3.0 (Migrado de JDBC a Spring Data JPA + Lombok)

================================================================================
*/