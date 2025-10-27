# ToDo App - Gestor de Tareas

Una aplicación móvil moderna para Android desarrollada en Kotlin que permite gestionar tareas diarias de manera eficiente.

## 🎯 Características Principales

- **Gestión Completa de Tareas**: Crear, editar, eliminar y marcar tareas como completadas
- **Prioridades**: Sistema de prioridades (Alta, Media, Baja) con códigos de color
- **Fechas Límite**: Establecer fechas límite opcionales para las tareas
- **Filtros**: Filtrar tareas por estado (Todas, Pendientes, Completadas)
- **Interfaz Moderna**: Diseño Material Design 3 con tema claro/oscuro
- **Persistencia Local**: Base de datos Room (SQLite) para almacenamiento local
- **Arquitectura MVVM**: Patrón de arquitectura moderno y mantenible

## 🏗️ Arquitectura

La aplicación sigue el patrón **MVVM (Model-View-ViewModel)** con las siguientes capas:

### Capa de Datos
- **TaskEntity**: Entidad de base de datos Room
- **TaskDao**: Interfaz de acceso a datos con operaciones CRUD
- **TaskDatabase**: Configuración de la base de datos Room
- **TaskRepository**: Abstracción de la capa de datos

### Capa de Presentación
- **MainActivity**: Pantalla principal con lista de tareas
- **AddEditTaskActivity**: Pantalla para crear/editar tareas
- **TaskListAdapter**: Adaptador para RecyclerView
- **TaskViewModel**: ViewModel con lógica de negocio

### Capa de Utilidades
- **DateUtils**: Utilidades para manejo de fechas
- **ToDoApplication**: Clase Application personalizada

## 🛠️ Tecnologías Utilizadas

- **Kotlin**: Lenguaje de programación principal
- **Android Jetpack**: Componentes modernos de Android
  - Room Database para persistencia local
  - ViewModel y LiveData para arquitectura MVVM
  - Material Design 3 para UI moderna
- **RecyclerView**: Lista eficiente de tareas
- **ViewBinding**: Binding de vistas tipo-safe
- **Coroutines**: Programación asíncrona

## 📱 Funcionalidades

### Pantalla Principal
- Lista de tareas con RecyclerView
- Filtros por estado (Todas, Pendientes, Completadas)
- Botón flotante para agregar nuevas tareas
- Menú con opciones adicionales
- Estado vacío cuando no hay tareas

### Gestión de Tareas
- **Crear**: Formulario completo con validación
- **Editar**: Modificar tareas existentes
- **Eliminar**: Confirmación antes de eliminar
- **Marcar como completada**: Toggle con checkbox
- **Prioridades**: Sistema visual con colores
- **Fechas límite**: Selector de fecha opcional

### Características de UI
- Material Design 3 con tema personalizado
- Colores de prioridad (Rojo: Alta, Naranja: Media, Verde: Baja)
- Animaciones sutiles
- Interfaz responsive y accesible
- Indicadores de estado (completada, vencida)

## 🚀 Instalación y Uso

1. **Clonar el repositorio**
   ```bash
   git clone [url-del-repositorio]
   cd GestorDeTareas
   ```

2. **Abrir en Android Studio**
   - Importar el proyecto
   - Sincronizar dependencias Gradle
   - Ejecutar en dispositivo o emulador

3. **Usar la aplicación**
   - Toca el botón "+" para crear una tarea
   - Usa los filtros para organizar las tareas
   - Marca tareas como completadas
   - Edita o elimina tareas según necesites

## 📋 Estructura del Proyecto

```
app/src/main/java/com/example/gestordetareas/
├── data/
│   ├── TaskEntity.kt          # Entidad de base de datos
│   ├── TaskDao.kt             # Interfaz de acceso a datos
│   └── TaskDatabase.kt        # Configuración de Room
├── repository/
│   └── TaskRepository.kt      # Repositorio de datos
├── ui/
│   ├── MainActivity.kt        # Actividad principal
│   ├── AddEditTaskActivity.kt # Actividad de edición
│   └── TaskListAdapter.kt     # Adaptador de lista
├── viewmodel/
│   └── TaskViewModel.kt       # ViewModel principal
├── utils/
│   └── DateUtils.kt           # Utilidades de fecha
└── ToDoApplication.kt         # Clase Application
```

## 🎨 Personalización

### Colores
Los colores se pueden personalizar en `res/values/colors.xml`:
- `priority_high`: Color para prioridad alta
- `priority_medium`: Color para prioridad media  
- `priority_low`: Color para prioridad baja

### Tema
El tema Material Design 3 se configura en `res/values/themes.xml` con colores personalizados.

## 🔧 Dependencias Principales

```kotlin
// Room Database
implementation "androidx.room:room-runtime:2.6.1"
kapt "androidx.room:room-compiler:2.6.1"
implementation "androidx.room:room-ktx:2.6.1"

// ViewModel y LiveData
implementation "androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.2"
implementation "androidx.lifecycle:lifecycle-livedata-ktx:2.8.2"

// Material Design
implementation "com.google.android.material:material:1.12.0"

// RecyclerView
implementation "androidx.recyclerview:recyclerview:1.3.2"
```

## 📝 Notas de Desarrollo

- La aplicación utiliza Room Database para persistencia local
- Implementa el patrón Repository para abstracción de datos
- Usa ViewBinding para acceso tipo-safe a las vistas
- Sigue las mejores prácticas de Android moderno
- Código bien documentado y comentado en español

## 🚀 Próximas Mejoras

- [ ] Notificaciones para tareas próximas a vencer
- [ ] Sincronización en la nube
- [ ] Categorías de tareas
- [ ] Búsqueda de tareas
- [ ] Exportar/importar tareas
- [ ] Tema oscuro automático
- [ ] Widgets de escritorio

## 📄 Licencia

Este proyecto está bajo la Licencia MIT. Ver el archivo LICENSE para más detalles.

---

**Desarrollado con ❤️ en Kotlin para Android**
