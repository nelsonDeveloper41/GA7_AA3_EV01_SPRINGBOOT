package com.sena.GA7_AA3_EV01_springboot.controlador;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para las rutas de acceso principal y de inicio.
 */
@Controller
public class InicioController {

    // página de inicio
    @GetMapping("/")
    public String redirigirAProyectos() {
        return "inicio";
    }

    // Muestra la página de inicio informativa del sistema.
    @GetMapping("/inicio")
    public String mostrarInicio() {
        return "inicio";
    }
}

/*
================================================================================
DOCUMENTACIÓN DETALLADA - InicioController.java
================================================================================

PROPÓSITO DEL CONTROLADOR:
--------------------------
Este controlador tiene la responsabilidad exclusiva de manejar las rutas de
navegación principales y de acceso inicial a la aplicación. Su función es
dirigir al usuario al punto de entrada correcto y ofrecer una página de
bienvenida o informativa.

INFORMACIÓN ACADÉMICA:
----------------------
- Evidencia:  GA7-220501096-AA3-EV01
- Autor:      Nelson Rodriguez
- Programa:   Análisis y Desarrollo de Software - SENA

ENDPOINTS IMPLEMENTADOS:
------------------------

1. @GetMapping("/")
   - Propósito:    Manejar la petición a la URL raíz del servidor (ej: http://localhost:8080/).
   - Lógica:       Realiza una redirección del lado del servidor hacia "/proyectos".
   - ¿Por qué?:    Es una buena práctica no dejar la URL raíz sin un destino. Al
                   redirigir, se asegura que el usuario siempre aterrice en la
                   funcionalidad principal de la aplicación (la lista de proyectos)
                   sin tener que conocer la URL específica. Esto mejora la
                   experiencia de usuario.
   - Retorno:      Una cadena de texto con el prefijo "redirect:", que instruye a
                   Spring MVC para que realice una redirección HTTP 302 a la URL
                   especificada.

2. @GetMapping("/inicio")
   - Propósito:    Mostrar una página de bienvenida o de información general.
   - Lógica:       Renderiza la plantilla Thymeleaf ubicada en `src/main/resources/templates/inicio.html`.
   - ¿Por qué?:    Tener una página de inicio dedicada es útil para presentar el
                   proyecto, al desarrollador, la evidencia académica o incluso
                   mostrar un dashboard general en futuras versiones. Separa la
                   "portada" de la funcionalidad principal.
   - Retorno:      El nombre del template ("inicio") que Thymeleaf debe procesar y
                   enviar al navegador.

BUENAS PRÁCTICAS APLICADAS:
----------------------------
- Single Responsibility Principle (SRP): Este controlador solo se ocupa de la
  navegación inicial, separando esta responsabilidad del `ProyectoController`,
  que se enfoca exclusivamente en la lógica CRUD de los proyectos.
- Nomenclatura Clara: El nombre `InicioController` y los nombres de los métodos
  (`redirigirAProyectos`, `mostrarInicio`) son auto-descriptivos.
- URL Amigables: Las rutas "/" y "/inicio" son estándar y fáciles de recordar.

================================================================================
*/