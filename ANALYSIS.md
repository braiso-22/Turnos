# Análisis Técnico Completo — App "Turnos"

> Documento generado automáticamente a partir del código fuente del proyecto.

---

## Índice

1. [Descripción General](#1-descripción-general)
2. [Arquitectura y Estructura del Proyecto](#2-arquitectura-y-estructura-del-proyecto)
3. [Dependencias y Librerías](#3-dependencias-y-librerías)
4. [Modelos de Dominio](#4-modelos-de-dominio)
5. [Base de Datos Local (Room)](#5-base-de-datos-local-room)
6. [Firebase (Backend)](#6-firebase-backend)
7. [Repositorios](#7-repositorios)
8. [Casos de Uso (Use Cases)](#8-casos-de-uso-use-cases)
9. [Pantallas y UI](#9-pantallas-y-ui)
10. [Navegación](#10-navegación)
11. [Inyección de Dependencias (Hilt)](#11-inyección-de-dependencias-hilt)
12. [Componentes UI Reutilizables](#12-componentes-ui-reutilizables)
13. [Patrones y Decisiones de Diseño](#13-patrones-y-decisiones-de-diseño)
14. [Funcionalidades No Implementadas (Stubs)](#14-funcionalidades-no-implementadas-stubs)

---

## 1. Descripción General

**Turnos** es una app Android nativa escrita en **Kotlin** que permite gestionar **tareas** (tasks) y sus **ejecuciones** (executions) entre los usuarios de un equipo. La aplicación permite:

- **Autenticación** por email/contraseña con Firebase Auth.
- **Creación y listado de tareas** compartidas.
- **Registro de ejecuciones** sobre tareas (quién hizo qué y cuándo).
- **Confirmación de ejecuciones** de otros usuarios (sistema de validación cruzada).
- **Filtrado** de ejecuciones por usuario y búsqueda de tareas.

| Propiedad | Valor |
|---|---|
| **Package** | `com.braiso22.turnos` |
| **minSdk** | 29 |
| **targetSdk** | 34 |
| **compileSdk** | 34 |
| **jvmTarget** | 1.8 |
| **Kotlin** | 1.9.20 |
| **Compose Compiler** | 1.5.5 |

---

## 2. Arquitectura y Estructura del Proyecto

### Patrón Arquitectónico

- **Clean Architecture** con separación en tres capas por feature: `domain`, `data`, `presentation`.
- **MVVM** en la capa de presentación (ViewModel + StateFlow + Compose).
- **Modularización por feature** (paquetes, no módulos Gradle): `tasks`, `executions`, `users`.
- **Inyección de dependencias** con **Hilt** (Dagger 2).

### Árbol de Paquetes

```
com.braiso22.turnos/
├── App.kt                              # @HiltAndroidApp Application
├── MainActivity.kt                     # NavHost raíz, punto de entrada
├── TasksScreenTab                      # @Serializable route (data object)
├── ReceiptsScreenTab                   # @Serializable route (data object)
│
├── common/
│   ├── Constants.kt                    # Nombres de colecciones Firestore + clave DataStore
│   ├── Navigation.kt                   # Extensión NavController.navigateWithoutBack()
│   └── Resource.kt                     # Sealed class: Success / Error / Loading
│
├── components/
│   ├── LoginComponent.kt              # Componente UI legacy (no usado activamente)
│   └── ProfilePicture.kt             # Avatar circular con inicial del usuario
│
├── di/
│   ├── AppDB.kt                       # @Database Room (TaskEntity, UserEntity, ExecutionEntity)
│   ├── AppDBModule.kt                 # Hilt: provee AppDB singleton
│   ├── DBConverters.kt               # TypeConverters Room: LocalDate/LocalDateTime ↔ String
│   └── FirebaseModule.kt             # Hilt: provee FirebaseFirestore + FirebaseAuth
│
├── tasks/
│   ├── TasksModule.kt                # Hilt DI module
│   ├── data/
│   │   ├── TaskDto.kt                # DTO Firestore → Domain
│   │   ├── TaskRepositoryImpl.kt     # Implementación del repositorio
│   │   └── local/
│   │       ├── TaskDao.kt            # Room DAO
│   │       └── TaskEntity.kt         # Room Entity
│   ├── domain/
│   │   ├── Task.kt                   # Modelo de dominio
│   │   ├── TasksRepository.kt        # Interfaz del repositorio
│   │   ├── GetTasks.kt               # Use case
│   │   ├── GetTaskById.kt            # Use case
│   │   └── SyncTasks.kt              # Use case
│   └── presentation/
│       ├── add/                       # Pantalla "Añadir Tarea"
│       ├── edit/                      # Pantalla "Editar Tarea" (stub)
│       └── list/                      # Pantalla "Lista de Tareas"
│           └── components/            # TaskComponent, TaskListComponent
│
├── executions/
│   ├── ExecutionModule.kt            # Hilt DI module
│   ├── data/
│   │   ├── ExecutionDto.kt           # DTO Firestore → Domain (con @ServerTimestamp)
│   │   ├── ExecutionsRepositoryImpl.kt # Implementación del repositorio
│   │   ├── FirebaseApi.kt            # Interfaz API Firebase
│   │   ├── FirebaseApiImpl.kt        # Implementación API Firebase
│   │   └── local/
│   │       ├── ExecutionDao.kt       # Room DAO
│   │       └── ExecutionEntity.kt    # Room Entity
│   ├── domain/
│   │   ├── Execution.kt              # Modelo de dominio
│   │   ├── ExecutionWithUser.kt      # Modelo compuesto
│   │   ├── ExecutionWithUserAndTask.kt # Modelo compuesto
│   │   ├── ExecutionsRepository.kt   # Interfaz del repositorio
│   │   └── use_cases/
│   │       ├── SaveExecution.kt
│   │       ├── GetExecutionsByTaskId.kt
│   │       ├── GetOpenExecutions.kt
│   │       ├── SyncExecutions.kt
│   │       └── ConfirmExecution.kt
│   └── presentation/
│       ├── task_executions/           # Pantalla "Ejecuciones de una Tarea"
│       │   ├── components/            # ExecutionComponent, ExecutionListComponent
│       │   └── state/                 # UserExecutions
│       ├── open_executions/           # Pantalla "Ejecuciones Pendientes"
│       │   └── components/            # ReceiptComponent, ReceiptListComponent
│       └── executions_history/        # Pantalla "Historial" (stub)
│
└── users/
    ├── UsersModule.kt                # Hilt DI module
    ├── data/
    │   ├── UserRepositoryImpl.kt     # Implementación del repositorio
    │   ├── local/room/
    │   │   ├── UserDao.kt            # Room DAO
    │   │   └── UserEntity.kt         # Room Entity
    │   └── net/
    │       ├── api/
    │       │   ├── AuthApi.kt        # Interfaz auth
    │       │   └── UserApi.kt        # Interfaz user CRUD
    │       └── firebase/
    │           ├── AuthApiFirebase.kt # Implementación auth con Firebase Auth
    │           ├── UserApiFirebase.kt # Implementación user CRUD con Firestore
    │           └── UserFirebase.kt    # DTO Firestore → Domain
    ├── domain/
    │   ├── User.kt                   # Modelo de dominio
    │   ├── UserRepository.kt         # Interfaz del repositorio
    │   ├── CreateUser.kt             # Use case
    │   ├── GetCurrentUser.kt         # Use case
    │   ├── GetUserById.kt            # Use case
    │   └── SyncUsers.kt              # Use case
    └── presentation/
        ├── login/                     # Pantalla de Login
        │   └── components/            # EmailComponent, PasswordComponent, EmailPasswordComponent
        └── select_user/               # Pantalla de selección de username
```

---

## 3. Dependencias y Librerías

| Librería | Versión | Propósito |
|---|---|---|
| **Kotlin** | 1.9.20 | Lenguaje |
| **AGP** | 8.6.1 | Android Gradle Plugin |
| **KSP** | 1.9.20-1.0.13 | Procesador de anotaciones (Room, Hilt) |
| **Jetpack Compose BOM** | 2024.09.02 | UI declarativa |
| `activity-compose` | 1.9.2 | `setContent` en Activity |
| `material3` | (vía BOM) | Componentes Material Design 3 |
| `material-icons-extended` | 1.7.1 | Iconos adicionales (Edit, Search, History, etc.) |
| `navigation-compose` | 2.8.1 | Navegación type-safe con Compose |
| `kotlinx-serialization-json` | 1.6.3 | Serialización de rutas de navegación |
| **Hilt** (Dagger) | 2.51.1 | Inyección de dependencias |
| `hilt-navigation-compose` | 1.2.0 | `hiltViewModel()` en composables |
| **Room** | 2.6.1 | Base de datos local (SQLite) |
| `datastore-preferences` | 1.1.1 | Persistencia clave-valor (userId cache) |
| **Firebase BOM** | 33.3.0 | Suite de Firebase |
| `firebase-auth` | (vía BOM) | Autenticación email/password |
| `firebase-firestore` | (vía BOM) | Base de datos en la nube (snapshots reactivos) |
| `firebase-analytics` | (vía BOM) | Analytics |
| `lifecycle-runtime-ktx` | 2.8.6 | viewModelScope, lifecycle |
| `google-services` plugin | 4.4.2 | Configuración Firebase |

### Plugins Gradle

```kotlin
plugins {
    com.android.application
    org.jetbrains.kotlin.android
    org.jetbrains.kotlin.plugin.serialization
    com.google.devtools.ksp
    com.google.dagger.hilt.android
    androidx.room
    com.google.gms.google-services
}
```

---

## 4. Modelos de Dominio

### `User`
```kotlin
data class User(
    val id: String,
    val username: String,
    val email: String,
    val imageUrl: String? = null,
)
```

### `Task`
```kotlin
data class Task(
    val id: String,
    val name: String,
)
```

### `Execution`
```kotlin
data class Execution(
    val id: String = "",
    val dateTime: LocalDateTime,
    val isConfirmed: Boolean,
    val taskId: String,
    val userId: String = "",
)
```

### Modelos Compuestos (solo dominio, no persistidos)

```kotlin
data class ExecutionWithUser(
    val execution: Execution,
    val user: User?,
)

data class ExecutionWithUserAndTask(
    val execution: Execution,
    val user: User?,
    val task: Task?,
)
```

### DTOs / Modelos de Red

| Clase | Uso |
|---|---|
| `TaskDto(id, name)` | Firestore ↔ Task |
| `ExecutionDto(id, userId, taskId, date: Timestamp, confirmed)` | Firestore ↔ Execution (usa `@ServerTimestamp`) |
| `UserFirebase(id, email, username)` | Firestore ↔ User |

### UI States

| Clase | Pantalla | Campos |
|---|---|---|
| `TaskUiState` | TaskListScreen | `id`, `name` |
| `ExecutionUiState` | TaskExecutionsScreen | `id`, `imageUrl?`, `userId`, `userName`, `date`, `time`, `isConfirmed` |
| `ReceiptUiState` | OpenReceiptsScreen | `id`, `imageUrl?`, `taskName`, `userName`, `time` |
| `UserExecutions` | TaskExecutionsScreen (chips) | `userId`, `username`, `executions: Int`, `selected: Boolean` |

---

## 5. Base de Datos Local (Room)

### Configuración

- **Nombre:** `app_db`
- **Versión:** 1
- **Esquemas exportados a:** `app/schemas/`
- **TypeConverters:** `DBConverters` → `LocalDate? ↔ String?` y `LocalDateTime ↔ String` (formato ISO)

### Tablas

#### `TaskEntity`

| Campo | Tipo | Nota |
|---|---|---|
| `id` | `String` | `@PrimaryKey` |
| `name` | `String` | |

**DAO (`TaskDao`):**
- `getAll(): Flow<List<TaskEntity>>` — todas las tareas
- `insertAll(tasks: List<TaskEntity>)` — upsert (REPLACE)
- `getById(taskId: String): TaskEntity?` — por ID

#### `UserEntity`

| Campo | Tipo | Nota |
|---|---|---|
| `id` | `String` | `@PrimaryKey` |
| `email` | `String` | |
| `username` | `String` | |

**DAO (`UserDao`):**
- `insertAll(users: List<UserEntity>)` — upsert (REPLACE)
- `getAll(): Flow<List<UserEntity>>`
- `getUserById(id: String): UserEntity?`
- `getUserByEmail(email: String): UserEntity?`

#### `ExecutionEntity`

| Campo | Tipo | Nota |
|---|---|---|
| `id` | `String` | `@PrimaryKey` |
| `userId` | `String` | FK lógica a UserEntity |
| `taskId` | `String` | FK lógica a TaskEntity |
| `date` | `LocalDateTime` | TypeConverter ↔ String ISO |
| `confirmed` | `Boolean` | |

**DAO (`ExecutionDao`):**
- `getAll(): Flow<List<ExecutionEntity>>`
- `getByTaskId(taskId: String): Flow<List<ExecutionEntity>>`
- `insertAll(executions: List<ExecutionEntity>)` — upsert (REPLACE)
- `deleteAll()` — limpia toda la tabla
- `getExecutionsByOtherUsers(excludeUserId: String): Flow<List<ExecutionEntity>>` — ejecuciones de otros

---

## 6. Firebase (Backend)

### Firebase Auth
- Registro con `createUserWithEmailAndPassword(email, password)`
- Login con `signInWithEmailAndPassword(email, password)`
- Obtención del email actual con `auth.currentUser?.email`

### Firestore — Colecciones

#### `tasks`
| Campo | Tipo |
|---|---|
| `id` | String |
| `name` | String |

#### `users`
| Campo | Tipo |
|---|---|
| `id` | String |
| `email` | String |
| `username` | String |

#### `executions`
| Campo | Tipo |
|---|---|
| `id` | String |
| `userId` | String |
| `taskId` | String |
| `date` | Timestamp (`@ServerTimestamp`) |
| `confirmed` | Boolean |

### Constantes de Colección
```kotlin
const val TASKS_COLLECTION = "tasks"
const val EXECUTIONS_COLLECTION = "executions"
const val USERS_COLLECTION = "users"
```

### DataStore (Preferences)
- **Clave:** `USER_ID_PREFERENCE` = `"userId"`
- **Propósito:** Cachear el `userId` del usuario autenticado para evitar consultas repetidas a Firestore al guardar ejecuciones.
- **DataStore name:** `"settings"`

---

## 7. Repositorios

### `TasksRepository` → `TaskRepositoryImpl`

| Método | Fuente | Descripción |
|---|---|---|
| `saveTask(task)` | Firestore | Crea documento en `tasks`, devuelve `Flow<Resource<Task>>` |
| `listenOnlineTasks()` | Firestore | `snapshots()` reactivos → `Flow<List<Task>>` |
| `syncTasks(tasks)` | Room | `insertAll` (replace) |
| `getTasks()` | Room | `getAll()` como Flow |
| `getTaskById(taskId)` | Room | consulta por ID |

### `UserRepository` → `UserRepositoryImpl`

| Método | Fuente | Descripción |
|---|---|---|
| `emailSignIn(email, password)` | Firebase Auth | `createUserWithEmailAndPassword` |
| `emailLogin(email, password)` | Firebase Auth | `signInWithEmailAndPassword` |
| `registerUser(email, username)` | Firestore | Crea documento en `users` |
| `getCurrentUser()` | Room | Busca por email del usuario Firebase Auth |
| `getCurrentEmail()` | Firebase Auth | `auth.currentUser?.email` |
| `listenOnlineUsers()` | Firestore | `snapshots()` reactivos |
| `syncUsers(users)` | Room | `insertAll` (replace) |
| `getUserById(id)` | Room | consulta por ID |

**APIs internas:**
- `AuthApi` / `AuthApiFirebase` — abstracción sobre Firebase Auth
- `UserApi` / `UserApiFirebase` — abstracción sobre Firestore (colección `users`)

### `ExecutionsRepository` → `ExecutionsRepositoryImpl`

| Método | Fuente | Descripción |
|---|---|---|
| `saveExecution(execution)` | Firestore | Obtiene `userId` (DataStore/Firestore), crea documento en `executions` |
| `getExecutions()` | Firestore | `snapshots()` directos, ordenados por `dateTime` desc |
| `getExecutionsByTaskIds(taskId)` | Room | Filtrado por `taskId` |
| `listenOnlineExecutions()` | Firestore | `snapshots()` reactivos, ordenados por `dateTime` desc |
| `syncExecutions(executions)` | Room | `insertAll` (replace), o `deleteAll` si vacío |
| `getExecutionsByOtherUsers(excludeUserId)` | Room | Ejecuciones donde `userId != excludeUserId` |
| `confirmExecution(executionId)` | Firestore | Actualiza campo `confirmed = true` vía `FirebaseApi` |

**API interna:**
- `FirebaseApi` / `FirebaseApiImpl`
  - `getUserByEmail(email): Flow<UserFirebase?>` — consulta Firestore por email
  - `confirmExecution(executionId): Flow<Resource<Unit>>` — `document.update("confirmed", true)`

---

## 8. Casos de Uso (Use Cases)

### Módulo `users`

| Use Case | Firma | Lógica |
|---|---|---|
| `CreateUser` | `invoke(username): Flow<Resource<Unit>>` | Obtiene email del usuario autenticado → llama a `registerUser(email, username)` en Firestore. Emite `Loading → Success/Error`. |
| `GetCurrentUser` | `suspend invoke(): User?` | Busca en Room por email del usuario Firebase Auth activo. |
| `GetUserById` | `suspend invoke(id): User?` | Busca un usuario por ID en Room. |
| `SyncUsers` | `suspend invoke()` | Escucha snapshots de Firestore (`users`) y actualiza Room con `insertAll`. |

### Módulo `tasks`

| Use Case | Firma | Lógica |
|---|---|---|
| `GetTasks` | `invoke(): Flow<List<Task>>` | Lee tareas desde Room como Flow reactivo. |
| `GetTaskById` | `suspend invoke(taskId): Task?` | Consulta Room por `taskId`. |
| `SyncTasks` | `suspend invoke()` | Escucha snapshots de Firestore (`tasks`) y sincroniza Room. |

### Módulo `executions`

| Use Case | Firma | Lógica |
|---|---|---|
| `SaveExecution` | `invoke(execution): Flow<Resource<Execution>>` | Delega en el repositorio para guardar en Firestore. |
| `GetExecutionsByTaskId` | `invoke(taskId): Flow<List<ExecutionWithUser>>` | Lee ejecuciones de Room por `taskId`, enriquece con `User` vía `GetUserById`. |
| `GetOpenExecutions` | `invoke(): Flow<List<ExecutionWithUserAndTask>>` | Obtiene `userId` actual → lee ejecuciones de otros → filtra no confirmadas → enriquece con User y Task. |
| `SyncExecutions` | `suspend invoke()` | Escucha snapshots de Firestore (`executions`) y sincroniza Room. |
| `ConfirmExecution` | `invoke(executionId): Flow<Resource<Unit>>` | Actualiza `confirmed = true` en Firestore. |

---

## 9. Pantallas y UI

### 9.1. `LoginScreen`

| Propiedad | Valor |
|---|---|
| **ViewModel** | `LoginViewModel` |
| **Route** | `Login` (data object) |
| **Componentes** | `EmailPasswordComponent` (contiene `EmailComponent` + `PasswordComponent`) |

**Eventos:**
- `OnLogin` → Firebase Auth `signInWithEmail` → éxito → navega a `TasksScreenTab`
- `OnRegister` → Firebase Auth `createUser` → éxito → navega a `SelectUser`
- `OnEmailChange(email)` → actualiza estado
- `OnPasswordChange(password)` → actualiza estado

**UI Events (one-shot):**
- `OnBadLogin` → Snackbar "credenciales incorrectas"
- `OnBadRegister` → Snackbar "no se puede registrar con este email"
- `NoInternet` → Snackbar "sin conexión"
- `OnLogin` → navegación
- `OnRegister` → navegación

**Inicialización:** El ViewModel lanza `syncUsers()` en `init`.

---

### 9.2. `SelectUserScreen`

| Propiedad | Valor |
|---|---|
| **ViewModel** | `SelectUserViewModel` |
| **Route** | `SelectUser` (data object) |

**Propósito:** Tras registrarse en Firebase Auth, el usuario elige su `username`.

**UI:** `OutlinedTextField` para username + `Button` "Save".

**Lógica:** Llama al use case `CreateUser(username)` → éxito → navega a `TasksScreenTab`.

---

### 9.3. `TaskListScreen`

| Propiedad | Valor |
|---|---|
| **ViewModel** | `TaskListViewModel` |
| **Route** | `TasksList` (data object) |
| **Componentes** | `TaskListComponent` → `TaskComponent` |

**Features:**
- Lista de tareas en `LazyColumn` con divisores.
- Barra de búsqueda (`OutlinedTextField` con icono Search) que filtra por nombre (case-insensitive).
- Cada tarea tiene nombre + botón Edit.
- FAB para añadir tarea (comentado/deshabilitado).
- `BottomNavigationBar` con 2 tabs: **Tasks** (seleccionado) y **Open Executions**.

**Eventos:**
- `OnClickTask(id)` → navega a `TaskExecutions(id)`
- `OnClickEdit(id)` → navega a `EditTask(id)` (stub)
- `OnClickNew` → navega a `AddTask`
- `OnChangedSearch(text)` → filtra tareas
- `OnClickReceipts` → navega a `ReceiptsScreenTab`

**Inicialización:** Lanza `syncTasks()` + `getTasks()` en `init`.

**Filtrado:** `getFilteredTasks()` compara nombre en lowercase con `searchText` (contiene).

---

### 9.4. `AddTaskScreen`

| Propiedad | Valor |
|---|---|
| **ViewModel** | `AddTaskViewModel` |
| **Route** | `AddTask` (data object) |

**UI:** `OutlinedTextField` para nombre + `Button` "Save" + `TopAppBar` con back.

**Lógica:** Llama directamente a `TasksRepository.saveTask(Task("", name))` (sin use case dedicado).

**Eventos:**
- `OnChangedName(name)` → actualiza estado
- `OnClickSave` → guarda en Firestore → éxito → `popBackStack()`
- `OnClickCancel` → `popBackStack()`

---

### 9.5. `EditTask` (STUB)

| Propiedad | Valor |
|---|---|
| **Route** | `EditTask(id: String)` (data class) |
| **ViewModel** | Ninguno |

**Estado:** Solo muestra `TopAppBar` con back y texto `"Edition of tasks not implemented"`. **No implementado.**

---

### 9.6. `TaskExecutionsScreen`

| Propiedad | Valor |
|---|---|
| **ViewModel** | `TaskExecutionsViewModel` |
| **Route** | `TaskExecutions(id: String)` (data class) |
| **Componentes** | `SameTypeExecutionListComponent` → `SameTypeExecutionComponent`, `FilterChip` |

**Features:**
- **Chips filtrables** por usuario (`FilterChip` en `LazyRow`) mostrando `"username: N"` donde N = nº ejecuciones confirmadas.
- **Lista de ejecuciones** filtrada por usuarios seleccionados.
- Cada ejecución muestra: avatar + nombre usuario + fecha + hora + icono (✓ confirmado / ⏳ pendiente).
- **Botón "New Execution"** que se deshabilita tras el primer click.

**Lógica del filtro:**
- `combine(_executions, _selectedUserIds)` para calcular lista filtrada y chips reactivamente.
- Los chips solo cuentan ejecuciones confirmadas.
- Si no hay filtro seleccionado, se muestran todas.

**Inicialización:** `onInit(id)` lanza `syncUsers()`, `syncExecutions()`, `getExecutionsByTaskId(id)`.

---

### 9.7. `OpenReceiptsScreen`

| Propiedad | Valor |
|---|---|
| **ViewModel** | `OpenReceiptsViewModel` |
| **Route** | `OpenReceipts` (data object) |
| **Componentes** | `ReceiptListComponent` → `ReceiptComponent` |

**Features:**
- Lista de ejecuciones pendientes de **otros usuarios** (no confirmadas), agrupadas por fecha.
- Cada item muestra: avatar + nombre tarea + nombre usuario + hora + botón **"Confirm"**.
- `TopAppBar` con acción **History** (icono).
- `BottomNavigationBar` con Tasks y Open Executions (seleccionado).

**Lógica:** `GetOpenExecutions` obtiene el userId del usuario actual → consulta ejecuciones de otros → filtra `!isConfirmed` → enriquece con User y Task.

**Confirmar:** Llama a `ConfirmExecution(id)` → actualiza campo `confirmed = true` en Firestore.

---

### 9.8. `ReceiptsHistory` (STUB)

| Propiedad | Valor |
|---|---|
| **Route** | `ReceiptsHistory` (data object) |
| **ViewModel** | Ninguno |

**Estado:** Solo muestra `TopAppBar` con título "History" y botón back. **No implementado.**

---

## 10. Navegación

### Rutas (Type-Safe Navigation)

Todas las rutas son `@Serializable` usando Navigation Compose 2.8+:

| Ruta | Tipo | Parámetros |
|---|---|---|
| `Login` | `data object` | — |
| `SelectUser` | `data object` | — |
| `TasksScreenTab` | `data object` | — (nested graph) |
| `TasksList` | `data object` | — |
| `AddTask` | `data object` | — |
| `EditTask` | `data class` | `id: String` |
| `TaskExecutions` | `data class` | `id: String` |
| `ReceiptsScreenTab` | `data object` | — (nested graph) |
| `OpenReceipts` | `data object` | — |
| `ReceiptsHistory` | `data object` | — |

### Flujo de Navegación

```
[Arranque]
  ├── Firebase.auth.currentUser != null → startDestination = TasksScreenTab
  └── No autenticado                   → startDestination = Login

[Login]
  ├── login exitoso          → TasksScreenTab (navigateWithoutBack)
  └── registro exitoso       → SelectUser (navigateWithoutBack)

[SelectUser]
  └── username guardado      → TasksScreenTab (navigateWithoutBack)

[TasksScreenTab] (nested graph, startDestination = TasksList)
  ├── TasksList
  │   ├── click tarea        → TaskExecutions(id)
  │   ├── click edit         → EditTask(id) [stub]
  │   ├── click FAB          → AddTask [comentado]
  │   └── BottomNav "Open"   → ReceiptsScreenTab (navigateWithoutBack)
  ├── TaskExecutions(id)
  │   └── back               → popBackStack
  ├── EditTask(id)
  │   └── back               → popBackStack
  └── AddTask
      └── back/save          → popBackStack

[ReceiptsScreenTab] (nested graph, startDestination = OpenReceipts)
  ├── OpenReceipts
  │   ├── History action     → ReceiptsHistory
  │   └── BottomNav "Tasks"  → TasksScreenTab (navigateWithoutBack)
  └── ReceiptsHistory
      └── back               → popBackStack
```

### `navigateWithoutBack()`

```kotlin
fun NavController.navigateWithoutBack(destination: Any) {
    navigate(destination) {
        popUpTo(graph.id) { inclusive = true }
    }
}
```

Limpia todo el back stack al cambiar de tab/flujo, evitando que el usuario regrese con el botón Atrás.

---

## 11. Inyección de Dependencias (Hilt)

### Módulos

#### `FirebaseModule`
- `provideFirestoreDb(): FirebaseFirestore`
- `provideAuth(): FirebaseAuth`

#### `AppDBModule`
- `provideTaskDB(context): AppDB` — Room database singleton (`"app_db"`)

#### `TasksModule`
- `provideTaskDao(appDB): TaskDao`
- `provideTasksRepository(firestore, taskDao): TasksRepository`
- `provideGetTaskById(repo): GetTaskById`
- `provideSyncTasks(repo): SyncTasks`
- `provideGetTasks(repo): GetTasks`

#### `UsersModule`
- `provideUserDao(appDB): UserDao`
- `provideAuthApi(auth): AuthApi` → `AuthApiFirebase`
- `provideUserApi(firestore): UserApi` → `UserApiFirebase`
- `provideUserRepository(authApi, userApi, userDao): UserRepository`
- `provideCreateUserUseCase(repo): CreateUser`
- `provideSyncUsersUseCase(repo): SyncUsers`
- `provideGetUserByIdUseCase(repo): GetUserById`
- `provideGetCurrentUser(repo): GetCurrentUser`

#### `ExecutionModule`
- `provideExecutionDao(appDB): ExecutionDao`
- `provideExecutionsRepository(firestore, firebaseApi, auth, executionDao, context): ExecutionsRepository`
- `provideFirebaseApi(firestore): FirebaseApi` → `FirebaseApiImpl`
- `provideGetExecutionsByTaskId(repo, getUserById): GetExecutionsByTaskId`
- `provideSaveExecution(repo): SaveExecution`
- `provideSyncExecutions(repo): SyncExecutions`
- `provideGetOpenExecutions(repo, getCurrentUser, getUserById, getTaskById): GetOpenExecutions`
- `provideConfirmExecution(repo): ConfirmExecution`

---

## 12. Componentes UI Reutilizables

### `ProfilePictureComponent`
- **Ubicación:** `components/ProfilePicture.kt`
- **Propósito:** Avatar circular con borde primary, muestra la inicial del username en mayúscula.
- **Props:** `user: String`, `imageUrl: String?` (imagen no implementada, preparado para `AsyncImage`).

### `EmailComponent`
- `OutlinedTextField` con label "Email", trailing icon Clear, keyboard action Next.

### `PasswordComponent`
- `OutlinedTextField` con label "Password", toggle visibilidad, trailing icon Clear, keyboard action Done.

### `EmailPasswordComponent`
- Combina `EmailComponent` + `PasswordComponent` + botones Register/Login + `CircularProgressIndicator`.

### `TaskComponent`
- Row clickable: nombre de tarea + botón Edit.

### `TaskListComponent`
- `LazyColumn` de `TaskComponent` con divisores. Mensaje "No tasks" si vacío.

### `SameTypeExecutionComponent`
- Row: avatar + nombre usuario + fecha + hora + icono confirmado/pendiente.

### `SameTypeExecutionListComponent`
- `LazyColumn` de `SameTypeExecutionComponent`. Mensaje "No pending receipts" si vacío.

### `ReceiptComponent`
- Row: avatar + nombre tarea + nombre usuario + hora + botón "Confirm".

### `ReceiptListComponent`
- `LazyColumn` agrupado por fecha (headers de fecha en `titleLarge`). Mensaje si vacío.

### `LoginComponent` (legacy)
- **Ubicación:** `components/LoginComponent.kt`
- Componente de login antiguo/sketch, **no usado activamente** en las pantallas actuales.

---

## 13. Patrones y Decisiones de Diseño

### Sync Pattern (Offline-First)
1. Al entrar a cada pantalla, el ViewModel lanza `syncX()` en una corrutina separada.
2. `syncX()` escucha snapshots de Firestore y actualiza Room.
3. Las pantallas leen **siempre de Room** (reactive `Flow`).
4. Las escrituras van directo a Firestore.

### `Resource<T>` Sealed Class
```kotlin
sealed class Resource<T>(val data: T? = null, val exception: Exception) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(exception: Exception, data: T? = null) : Resource<T>(data, exception)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}
```
Modela estados asíncronos para operaciones de red.

### `callbackFlow` para Firebase
Convierte callbacks de Firebase (Firestore/Auth) en `Flow` de Coroutines.

### Event-Driven UI
- **Estado observable:** `MutableStateFlow` → `asStateFlow()` para estado de UI.
- **Eventos one-shot:** `MutableSharedFlow<UiEvent>` → `asSharedFlow()` para navegación y snackbars.
- Los `LaunchedEffect` en composables recogen los eventos.

### Separación Screen/Component
Cada pantalla tiene:
- `XScreen` — conecta el ViewModel, recoge estado, lanza efectos.
- `XScreenComponent` — composable **puro** (sin ViewModel), recibe datos y lambdas → facilita previews y tests.

### Filtrado Reactivo con `combine`
En `TaskExecutionsViewModel`:
- `combine(_executions, _selectedUserIds)` → lista filtrada
- `combine(_executions, _selectedUserIds)` → chips de usuarios
- `.stateIn(WhileSubscribed(5000))` para compartir estado eficientemente.

### DataStore como Cache de UserId
`ExecutionsRepositoryImpl` almacena el `userId` en DataStore tras la primera resolución (lookup Firestore por email), evitando consultas repetidas.

---

## 14. Funcionalidades No Implementadas (Stubs)

| Funcionalidad | Estado | Ubicación |
|---|---|---|
| **Edición de tareas** | Stub — muestra "Edition of tasks not implemented" | `tasks/presentation/edit/EditTask.kt` |
| **Historial de ejecuciones** | Stub — solo TopAppBar con "History" | `executions/presentation/executions_history/ReceiptsHistoryNavigation.kt` |
| **FAB para añadir tarea** | Código comentado en `TaskListScreen` | `tasks/presentation/list/TaskListScreen.kt` |
| **Imagen de perfil (AsyncImage)** | Código comentado en `ProfilePictureComponent` | `components/ProfilePicture.kt` |
| **Google Login** | Evento definido en `LoginComponent` legacy pero no conectado | `components/LoginComponent.kt` |

---

## Resumen para Recrear la App

Para recrear esta app desde cero necesitas:

1. **Crear proyecto Android** con Compose, minSdk 29, targetSdk 34.
2. **Configurar Firebase:** proyecto en Firebase Console con Auth (email/password) y Firestore.
3. **Añadir dependencias:** Compose + Material3, Navigation Compose 2.8+, Hilt, Room, DataStore, Firebase (Auth + Firestore + Analytics), kotlinx-serialization.
4. **Crear 3 colecciones en Firestore:** `tasks`, `users`, `executions`.
5. **Implementar Clean Architecture** con la estructura de paquetes descrita:
   - Dominio: modelos, interfaces de repositorio, use cases.
   - Data: Room (entities + DAOs), DTOs de Firestore, implementaciones de repositorios.
   - Presentación: Screens + ViewModels + componentes reutilizables.
6. **Implementar el patrón Sync:** cada ViewModel lanza sincronización Firestore→Room en `init`/`onInit`, las pantallas leen de Room.
7. **Configurar Hilt** con módulos por feature + módulos globales (Firebase, Room).
8. **Configurar Navigation** con type-safe routes (`@Serializable`) y nested graphs.

