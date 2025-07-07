package com.sena.GA7_AA3_EV01_springboot.controlador;

import com.sena.GA7_AA3_EV01_springboot.modelo.Proyecto;
import com.sena.GA7_AA3_EV01_springboot.servicio.ProyectoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/proyectos")
@RequiredArgsConstructor
@Slf4j
public class ProyectoController {

    // Inyección del servicio para lógica de negocio
    private final ProyectoService proyectoService;

    // =================== ENDPOINTS PRINCIPALES ===================

    // Página principal - Lista todos los proyectos
    @GetMapping
    public String listarProyectos(Model model) {
        log.debug("📋 Mostrando lista de proyectos");

        var proyectos = proyectoService.listarTodosLosProyectos();
        var estadisticas = proyectoService.obtenerEstadisticas();

        model.addAttribute("proyectos", proyectos);
        model.addAttribute("estadisticas", estadisticas);

        return "proyectos/lista";
    }

    // Formulario para crear nuevo proyecto
    @GetMapping("/nuevo")
    public String mostrarFormularioNuevo(Model model) {
        log.debug("📝 Mostrando formulario para nuevo proyecto");

        model.addAttribute("proyecto", new Proyecto());
        model.addAttribute("titulo", "Crear Nuevo Proyecto");
        model.addAttribute("esNuevo", true);

        return "proyectos/formulario";
    }

    // Formulario para editar proyecto existente
    @GetMapping("/editar/{id}")
    public String mostrarFormularioEditar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.debug("✏️ Mostrando formulario para editar proyecto ID: {}", id);

        try {
            Proyecto proyecto = proyectoService.obtenerPorId(id);

            // Verificar si el proyecto puede ser editado
            if (!proyecto.isEditable()) {
                redirectAttributes.addFlashAttribute("error",
                        "No se puede editar un proyecto en estado: " + proyecto.getEstado());
                return "redirect:/proyectos";
            }

            model.addAttribute("proyecto", proyecto);
            model.addAttribute("titulo", "Editar Proyecto");
            model.addAttribute("esNuevo", false);

            return "proyectos/formulario";

        } catch (Exception e) {
            log.error("❌ Error al cargar proyecto para editar: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Proyecto no encontrado");
            return "redirect:/proyectos";
        }
    }

    // Procesar formulario (crear o actualizar)
    @PostMapping("/guardar")
    public String guardarProyecto(@Valid @ModelAttribute Proyecto proyecto,
                                  BindingResult bindingResult,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        log.debug("💾 Guardando proyecto: {}", proyecto.getNombreObra());

        // Si hay errores de validación, volver al formulario
        if (bindingResult.hasErrors()) {
            log.warn("⚠️ Errores de validación en formulario");

            model.addAttribute("titulo", proyecto.getId() == null ? "Crear Nuevo Proyecto" : "Editar Proyecto");
            model.addAttribute("esNuevo", proyecto.getId() == null);

            return "proyectos/formulario";
        }

        try {
            // Determinar si es creación o actualización
            if (proyecto.getId() == null) {
                // Crear nuevo proyecto
                proyectoService.crearProyecto(proyecto);
                redirectAttributes.addFlashAttribute("exito",
                        "Proyecto '" + proyecto.getNombreObra() + "' creado exitosamente");

            } else {
                // Actualizar proyecto existente
                proyectoService.actualizarProyecto(proyecto);
                redirectAttributes.addFlashAttribute("exito",
                        "Proyecto '" + proyecto.getNombreObra() + "' actualizado exitosamente");
            }

            return "redirect:/proyectos";

        } catch (Exception e) {
            log.error("❌ Error al guardar proyecto: {}", e.getMessage());

            model.addAttribute("error", e.getMessage());
            model.addAttribute("titulo", proyecto.getId() == null ? "Crear Nuevo Proyecto" : "Editar Proyecto");
            model.addAttribute("esNuevo", proyecto.getId() == null);

            return "proyectos/formulario";
        }
    }

    // Mostrar confirmación antes de eliminar
    @GetMapping("/eliminar/{id}")
    public String mostrarConfirmacionEliminar(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.debug("🗑️ Mostrando confirmación para eliminar proyecto ID: {}", id);

        try {
            Proyecto proyecto = proyectoService.obtenerPorId(id);
            model.addAttribute("proyecto", proyecto);

            return "proyectos/confirmar-eliminar";

        } catch (Exception e) {
            log.error("❌ Error al cargar proyecto para eliminar: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Proyecto no encontrado");
            return "redirect:/proyectos";
        }
    }

    // Procesar eliminación del proyecto
    @PostMapping("/eliminar")
    public String eliminarProyecto(@RequestParam Long id, RedirectAttributes redirectAttributes) {
        log.warn("🗑️ Eliminando proyecto ID: {}", id);

        try {
            Proyecto proyecto = proyectoService.obtenerPorId(id);
            String nombreObra = proyecto.getNombreObra();

            proyectoService.eliminarProyecto(id);

            redirectAttributes.addFlashAttribute("exito",
                    "Proyecto '" + nombreObra + "' eliminado exitosamente");

        } catch (Exception e) {
            log.error("❌ Error al eliminar proyecto: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Error al eliminar el proyecto: " + e.getMessage());
        }

        return "redirect:/proyectos";
    }

    // =================== ENDPOINTS ADICIONALES ===================

    // Ver detalles de un proyecto específico
    @GetMapping("/ver/{id}")
    public String verDetalles(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.debug("👁️ Mostrando detalles del proyecto ID: {}", id);

        try {
            Proyecto proyecto = proyectoService.obtenerPorId(id);
            model.addAttribute("proyecto", proyecto);

            return "proyectos/detalles";

        } catch (Exception e) {
            log.error("❌ Error al cargar detalles del proyecto: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Proyecto no encontrado");
            return "redirect:/proyectos";
        }
    }

    // Cambiar estado de un proyecto
    @PostMapping("/cambiar-estado")
    public String cambiarEstado(@RequestParam Long id,
                                @RequestParam String nuevoEstado,
                                RedirectAttributes redirectAttributes) {
        log.info("🔄 Cambiando estado del proyecto ID: {} a: {}", id, nuevoEstado);

        try {
            Proyecto proyecto = proyectoService.cambiarEstado(id, nuevoEstado);

            redirectAttributes.addFlashAttribute("exito",
                    "Estado del proyecto '" + proyecto.getNombreObra() + "' cambiado a: " + nuevoEstado);

        } catch (Exception e) {
            log.error("❌ Error al cambiar estado: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error",
                    "Error al cambiar estado: " + e.getMessage());
        }

        return "redirect:/proyectos";
    }

    // Buscar proyectos (para implementar en futuras versiones)
    @GetMapping("/buscar")
    public String buscarProyectos(@RequestParam(required = false) String termino,
                                  @RequestParam(required = false) String estado,
                                  Model model) {
        log.debug("🔍 Buscando proyectos - Término: '{}', Estado: '{}'", termino, estado);

        var proyectos = proyectoService.listarTodosLosProyectos();

        // Filtrar por término de búsqueda si se proporciona
        if (termino != null && !termino.trim().isEmpty()) {
            proyectos = proyectos.stream()
                    .filter(p -> p.getNombreObra().toLowerCase().contains(termino.toLowerCase()) ||
                            p.getNombreCliente().toLowerCase().contains(termino.toLowerCase()) ||
                            p.getNitCliente().contains(termino))
                    .toList();
        }

        // Filtrar por estado si se proporciona
        if (estado != null && !estado.trim().isEmpty() && !"TODOS".equals(estado)) {
            proyectos = proyectos.stream()
                    .filter(p -> p.getEstado().equals(estado))
                    .toList();
        }

        var estadisticas = proyectoService.obtenerEstadisticas();

        model.addAttribute("proyectos", proyectos);
        model.addAttribute("estadisticas", estadisticas);
        model.addAttribute("terminoBusqueda", termino);
        model.addAttribute("estadoFiltro", estado);

        return "proyectos/lista";
    }

    // Redirección desde la raíz de la aplicación
    @GetMapping("/")
    public String redirigirAProyectos() {
        return "redirect:/proyectos";
    }

}

/*
================================================================================
DOCUMENTACIÓN COMPLETA DE ProyectoController.java
================================================================================

PROPÓSITO:
----------
Controlador Spring MVC que maneja todas las peticiones HTTP relacionadas con
la gestión de proyectos. Actúa como punto de entrada web para las operaciones
CRUD, coordinando entre las vistas Thymeleaf y la lógica de negocio del servicio.

INFORMACIÓN ACADÉMICA:
----------------------
Programa:     Análisis y Desarrollo de Software - SENA
Evidencia:    GA7-220501096-AA3-EV01
Capa:         Controlador (Presentación Web)
Framework:    Spring MVC + Thymeleaf
Autor:        Nelson Rodriguez

PATRÓN ARQUITECTÓNICO:
---------------------
- Implementa el patrón MVC (Model-View-Controller)
- Controlador: Maneja peticiones HTTP y coordina flujo
- Vista: Templates Thymeleaf para renderizado HTML
- Modelo: Datos transferidos entre controlador y vista

ANOTACIONES SPRING UTILIZADAS:
-----------------------------

1. @Controller:
   - Marca la clase como controlador Spring MVC
   - Permite manejo de peticiones HTTP
   - Retorna nombres de vistas para renderizado

2. @RequestMapping("/proyectos"):
   - Define la ruta base para todos los endpoints
   - Centraliza el mapeo de URLs del controlador

3. @GetMapping / @PostMapping:
   - Mapean métodos HTTP específicos a métodos Java
   - GET: Para mostrar vistas y obtener datos
   - POST: Para procesar formularios y modificar datos

4. @PathVariable:
   - Extrae valores de la URL (ej: /editar/{id})
   - Vincula parámetros de ruta a parámetros de método

5. @RequestParam:
   - Extrae parámetros de query string o formularios
   - Permite parámetros opcionales con required = false

6. @ModelAttribute:
   - Vincula datos de formulario a objetos Java
   - Facilita el binding automático de campos

7. @Valid:
   - Activa validaciones Bean Validation
   - Llena automáticamente BindingResult con errores

ENDPOINTS IMPLEMENTADOS:
-----------------------

1. ENDPOINTS PRINCIPALES CRUD:

   GET /proyectos
   - Lista todos los proyectos ordenados por fecha
   - Incluye estadísticas para el dashboard
   - Vista: proyectos/lista.html

   GET /proyectos/nuevo
   - Muestra formulario para crear proyecto
   - Inicializa objeto Proyecto vacío
   - Vista: proyectos/formulario.html

   GET /proyectos/editar/{id}
   - Muestra formulario pre-llenado para editar
   - Valida que el proyecto sea editable
   - Vista: proyectos/formulario.html

   POST /proyectos/guardar
   - Procesa formulario de creación/actualización
   - Maneja validaciones y errores
   - Redirect: /proyectos con mensaje de éxito/error

   GET /proyectos/eliminar/{id}
   - Muestra confirmación antes de eliminar
   - Carga datos del proyecto para mostrar
   - Vista: proyectos/confirmar-eliminar.html

   POST /proyectos/eliminar
   - Procesa eliminación definitiva
   - Redirect: /proyectos con mensaje de confirmación

2. ENDPOINTS ADICIONALES:

   GET /proyectos/ver/{id}
   - Muestra detalles completos de un proyecto
   - Vista: proyectos/detalles.html

   POST /proyectos/cambiar-estado
   - Cambia estado de proyecto (ACTIVO, PAUSADO, etc.)
   - Redirect: /proyectos con confirmación

   GET /proyectos/buscar
   - Búsqueda y filtrado de proyectos
   - Filtros: término de búsqueda y estado
   - Vista: proyectos/lista.html (reutilizada)

   GET /
   - Redirección desde raíz a /proyectos

MANEJO DE FORMULARIOS:
---------------------

1. Vinculación de Datos (Data Binding):
   - @ModelAttribute vincula campos HTML a propiedades Java
   - Conversión automática de tipos (String → Long, etc.)
   - Binding bidireccional para edición

2. Validaciones:
   - @Valid activa Bean Validation en el modelo
   - BindingResult captura errores de validación
   - Errores se muestran automáticamente en la vista

3. Flujo POST-REDIRECT-GET:
   - POST procesa datos y redirige
   - GET muestra resultado final
   - Previene reenvío accidental de formularios

MANEJO DE MENSAJES:
------------------

1. RedirectAttributes:
   - addFlashAttribute(): Mensajes temporales entre requests
   - Tipos: "exito", "error", "info", "advertencia"
   - Se muestran una sola vez y desaparecen

2. Model:
   - Datos permanentes durante el request actual
   - Disponibles en la vista para renderizado
   - Para errores de validación y datos de formulario

GESTIÓN DE ERRORES:
------------------

1. Try-Catch en Cada Endpoint:
   - Captura excepciones del servicio
   - Convierte excepciones técnicas a mensajes de usuario
   - Logs detallados para debugging

2. Validación de Estados:
   - Verifica que proyectos sean editables antes de editar
   - Valida existencia antes de mostrar detalles
   - Manejo graceful de proyectos no encontrados

3. Fallback a Lista Principal:
   - En caso de error, siempre redirige a /proyectos
   - Muestra mensaje de error apropiado
   - Mantiene la aplicación funcional

LOGGING Y AUDITORÍA:
-------------------

1. Logs por Nivel:
   - DEBUG: Operaciones normales (mostrar vistas)
   - INFO: Cambios de estado importantes
   - WARN: Errores de validación
   - ERROR: Excepciones y errores técnicos

2. Información de Logs:
   - ID de proyectos para trazabilidad
   - Nombres de operaciones realizadas
   - Detalles de errores para debugging

INTEGRACIÓN CON VISTAS THYMELEAF:
--------------------------------

1. Modelos de Datos:
   - "proyecto": Objeto para formularios y detalles
   - "proyectos": Lista para tabla principal
   - "estadisticas": Métricas para dashboard
   - "titulo": Título dinámico de páginas

2. Variables de Control:
   - "esNuevo": Boolean para distinguir crear vs editar
   - "terminoBusqueda": Término actual de búsqueda
   - "estadoFiltro": Filtro actual de estado

3. Mensajes Flash:
   - ${exito}: Mensajes de éxito (verde)
   - ${error}: Mensajes de error (rojo)
   - ${info}: Mensajes informativos (azul)

SEGURIDAD Y VALIDACIONES:
------------------------

1. Validación de Entrada:
   - @Valid en formularios
   - Verificación de existencia de proyectos
   - Validación de estados editables

2. Prevención de Errores:
   - Verificación de null antes de operaciones
   - Manejo de excepciones en cada endpoint
   - Validación de parámetros requeridos

PATRONES DE DISEÑO IMPLEMENTADOS:
--------------------------------

1. Front Controller:
   - Un controlador maneja todas las URLs de un dominio
   - Centraliza la lógica de presentación

2. Model-View-Controller:
   - Separación clara de responsabilidades
   - Modelo: Datos (Proyecto)
   - Vista: Templates Thymeleaf
   - Controlador: Esta clase

3. Post-Redirect-Get:
   - Previene reenvío accidental de formularios
   - Mejora experiencia de usuario

VENTAJAS SOBRE SERVLETS TRADICIONALES:
-------------------------------------
- Anotaciones vs configuración XML
- Binding automático vs parsing manual
- Validaciones declarativas vs código imperativo
- Templates modernos vs JSP
- Inyección de dependencias vs lookup manual
- Manejo de errores centralizado vs disperso

CONFIGURACIÓN REQUERIDA:
-----------------------
- ProyectoService debe estar disponible para inyección
- Templates Thymeleaf en src/main/resources/templates/proyectos/
- Configuración de Spring MVC en Spring Boot (automática)

EJEMPLOS DE USO:
---------------
URLs principales:
- http://localhost:8080/proyectos (lista)
- http://localhost:8080/proyectos/nuevo (crear)
- http://localhost:8080/proyectos/editar/1 (editar)
- http://localhost:8080/proyectos/ver/1 (detalles)

EVIDENCIA DE COMPETENCIAS:
-------------------------
1. 🎯 Implementación correcta del patrón MVC
2. 🏗️ Uso efectivo de Spring MVC annotations
3. 💾 Integración con capa de servicio
4. 📝 Manejo de formularios y validaciones
5. 🔧 Gestión de errores y mensajes de usuario
6. 🧪 Implementación de endpoints RESTful
7. 📊 Logging y auditoría apropiados

FUTURAS MEJORAS:
---------------
- Paginación para listas grandes
- Búsqueda avanzada con múltiples criterios
- Export a PDF/Excel
- API REST adicional
- Validaciones AJAX en tiempo real

AUTOR: Nelson Rodriguez
EVIDENCIA: GA7-220501096-AA3-EV01
FECHA: 2025-07-07
VERSIÓN: 3.0 (Spring MVC Controller)

================================================================================
*/