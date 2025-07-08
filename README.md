# 🚀 Cartillas Acero Web - Evidencia GA7-220501096-AA3-EV01

Este repositorio contiene el código fuente de la evidencia **GA7-220501096-AA3-EV01: "Codificación de módulos del software Stand alone, web y móvil"**, correspondiente a la fase de Ejecución del programa **Análisis y Desarrollo de Software (ADSO)** del **SENA**.

El proyecto consiste en la migración de un módulo de gestión de proyectos, originalmente desarrollado con Servlets y JSP, a una aplicación web moderna y robusta utilizando el framework **Spring Boot**.

- **Autor:** Nelson Rodriguez
- **Programa:** Análisis y Desarrollo de Software (ADSO)
- **Ficha:** 2977467
- **Fecha:** Julio 2025

---

## 📋 1. Descripción del Proyecto

**Cartillas Acero Web** es una aplicación web diseñada para la gestión integral de proyectos de construcción. Permite a los usuarios realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre los proyectos, manteniendo un registro detallado de la información de cada obra y su cliente asociado.

Este módulo demuestra la aplicación de tecnologías y patrones de desarrollo modernos, cumpliendo con los criterios de evaluación de la evidencia, que se centran en la selección de un framework, la integración de almacenamiento de datos y el uso de estándares de codificación.

### Funcionalidades Implementadas

El módulo de gestión de proyectos cuenta con las siguientes funcionalidades:

-   ✅ **Creación de Proyectos:** Formulario con validaciones en tiempo real y del lado del servidor.
-   ✅ **Listado de Proyectos:** Vista principal con un dashboard de estadísticas y una tabla detallada de todos los proyectos.
-   ✅ **Detalles de Proyecto:** Vista de solo lectura con toda la información de un proyecto específico.
-   ✅ **Edición de Proyectos:** Formulario pre-llenado para modificar la información de proyectos existentes (solo si su estado lo permite).
-   ✅ **Eliminación Segura de Proyectos:** Flujo de confirmación en dos pasos para prevenir eliminaciones accidentales.
-   ✅ **Validación de Datos:** Uso de Bean Validation para garantizar la integridad de los datos.
-   ✅ **Navegación Intuitiva:** Página de inicio y barra de navegación consistente en toda la aplicación.

---

## 🛠️ 2. Cumplimiento de la Lista de Chequeo

A continuación, se detalla cómo el proyecto cumple con cada uno de los indicadores de logro del instrumento de evaluación:

| No. | INDICADOR DE LOGRO                                                | CUMPLIMIENTO (30%)                                                                                                                                                                                                                                                                                                                                                                                                                          |
| :-- | :---------------------------------------------------------------- | :------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------ |
| 1.  | **Selecciona y aplica un Framework para la codificación del proyecto.** | **Sí cumple.** Se seleccionó y aplicó **Spring Boot** como framework principal. La estructura del proyecto sigue el patrón **Modelo-Vista-Controlador (MVC)**, utilizando componentes clave como `@RestController`, `@Service` y `@Repository` para una arquitectura en capas bien definida, lo que facilita la mantenibilidad y escalabilidad.                                                                                             |
| 2.  | **Integra herramientas para almacenamiento de datos.**              | **Sí cumple.** Se integró una base de datos **MySQL** a través de **Spring Data JPA** y **Hibernate**. Esta integración moderna reemplaza el código JDBC manual, automatizando las operaciones CRUD, gestionando transacciones y mejorando la eficiencia con un pool de conexiones (HikariCP). La configuración se encuentra en `application.properties`.                                                                                   |
| 3.  | **Utiliza un estándar de codificación definido y usa comentarios en el código.** | **Sí cumple.** Todo el código Java sigue las convenciones estándar (camelCase para métodos/variables, PascalCase para clases). Además, cada clase y método importante está documentado con bloques de comentarios (estilo JavaDoc y bloques `/* ... */`), explicando su propósito, parámetros y lógica. Los archivos HTML también incluyen docstrings que explican su función.                                                     |
| 4.  | **Utiliza un estándar de codificación definido.**                   | **Sí cumple.** (10%) Este punto, que refuerza el anterior, se evidencia en la nomenclatura consistente, la indentación, la organización en paquetes (`controlador`, `modelo`, `servicio`, `repositorio`) y la estructura clara de las plantillas Thymeleaf. Se utilizó **Lombok** para reducir el código boilerplate, una práctica estándar en la industria.                                                                                              |

---

## 💻 3. Stack Tecnológico

Este proyecto fue construido utilizando un stack tecnológico moderno y estándar en la industria:

-   **Backend:**
    -   **Lenguaje:** Java 22
    -   **Framework:** Spring Boot 3.5.3
    -   **Persistencia:** Spring Data JPA / Hibernate
    -   **Base de Datos:** MySQL 8.0
    -   **Servidor:** Apache Tomcat (embebido)
-   **Frontend:**
    -   **Motor de Plantillas:** Thymeleaf
    -   **Estilos y Layout:** Bootstrap 5
    -   **Interactividad:** JavaScript
-   **Herramientas de Desarrollo:**
    -   **Gestor de Dependencias:** Maven
    -   **Control de Versiones:** Git
    -   **Utilidades:** Lombok (para reducción de código)

---

## 🚀 4. Guía de Ejecución Local

Para ejecutar este proyecto en un entorno local, sigue estos pasos:

### Prerrequisitos

1.  **Java JDK 22** o superior instalado.
2.  **Maven** instalado o usar el Maven Wrapper (`mvnw`) incluido.
3.  Un servidor de base de datos **MySQL** en ejecución (ej. a través de XAMPP, WAMP, o Docker).

### Pasos

1.  **Clonar el Repositorio:**
    ```bash
    git clone [URL-DE-TU-REPOSITORIO]
    cd GA7_AA3_EV01_springboot
    ```

2.  **Configurar la Base de Datos:**
    -   Asegúrate de que tu servidor MySQL esté corriendo.
    -   El proyecto creará automáticamente la base de datos `cartillasacerowebga7aa3ev01` si no existe, gracias a la configuración en `src/main/resources/application.properties`.
    -   Si usas un usuario o contraseña diferente para MySQL, actualiza estas líneas en `application.properties`:
        ```properties
        spring.datasource.username=root
        spring.datasource.password=
        ```

3.  **Compilar y Ejecutar el Proyecto:**
    -   Abre una terminal en la raíz del proyecto.
    -   Ejecuta el siguiente comando para compilar y lanzar la aplicación:
        ```bash
        mvn spring-boot:run
        ```
    -   Alternativamente, puedes empaquetar la aplicación en un JAR ejecutable:
        ```bash
        mvn clean package
        java -jar target/GA7_AA3_EV01_springboot-0.0.1-SNAPSHOT.jar
        ```

4.  **Acceder a la Aplicación:**
    -   Una vez que la aplicación se haya iniciado, abre tu navegador web y ve a la siguiente URL:
        -   **Página de Inicio:** `http://localhost:8080/`
        -   **Módulo de Proyectos:** `http://localhost:8080/proyectos`

### Inicialización de Datos

Al primer inicio, la aplicación verificará si la base de datos está vacía. Si lo está, insertará automáticamente 5 proyectos de prueba con diferentes estados para facilitar la demostración y las pruebas del CRUD.

---

## 📂 5. Estructura del Proyecto

La estructura de paquetes sigue las convenciones de una aplicación Spring Boot basada en capas:

-   `src/main/java/com/sena/GA7_AA3_EV01_springboot/`
    -   `configuracion/`: Clases de configuración, como el verificador de la base de datos.
    -   `controlador/`: Controladores Spring MVC que manejan las peticiones web.
    -   `modelo/`: Entidades JPA que representan los datos (`Proyecto.java`).
    -   `repositorio/`: Interfaces de Spring Data JPA para el acceso a datos.
    -   `servicio/`: Clases de servicio que contienen la lógica de negocio.
-   `src/main/resources/`
    -   `templates/`: Plantillas HTML de Thymeleaf para las vistas.
    -   `application.properties`: Archivo de configuración principal.