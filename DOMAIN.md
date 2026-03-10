# Modelo de Dominio — App "Turnos"

> Documentación definitiva de agregados, entidades, objetos de valor, eventos de dominio y reglas de negocio.

---

## Índice

1. [Contexto del Dominio](#1-contexto-del-dominio)
2. [Agregados y Entidades](#2-agregados-y-entidades)
3. [Objetos de Valor (Value Objects)](#3-objetos-de-valor-value-objects)
4. [Eventos de Dominio](#4-eventos-de-dominio)
5. [Comandos](#5-comandos)
6. [Reglas de Negocio / Invariantes](#6-reglas-de-negocio--invariantes)
7. [Event Storming](#7-event-storming)
8. [Preguntas Resueltas](#8-preguntas-resueltas)

---

## 1. Contexto del Dominio

**Turnos** gestiona la **asignación y validación de tareas compartidas** entre los miembros de un **grupo**. El flujo principal es:

1. Un **usuario** se registra con email/contraseña y crea o se une a un **grupo** mediante un código de invitación.
2. Un usuario puede pertenecer a **varios grupos** simultáneamente.
3. Dentro del grupo existen **tareas** compartidas que todos los miembros pueden ver.
4. Un usuario **registra una ejecución** sobre una tarea, adjuntando una **prueba fotográfica** obligatoria (exactamente una imagen).
5. **Otro usuario** del grupo revisa la prueba y debe **confirmar** o **rechazar** la ejecución (validación cruzada con evidencia).
6. Si la ejecución es rechazada, el ejecutor puede **reintentar** registrando una nueva ejecución sobre la misma tarea.

### Bounded Contexts

| Bounded Context | Responsabilidad |
|---|---|
| **Identidad y Acceso** | Registro, autenticación, perfil de usuario |
| **Grupos** | Creación de grupos, membresía, códigos de invitación, aislamiento de datos, eliminación por vaciado |
| **Gestión de Tareas** | CRUD de tareas compartidas dentro de un grupo |
| **Ejecuciones y Validación** | Registro de ejecuciones con prueba fotográfica, confirmación/rechazo cruzado, historial |

---

## 2. Agregados y Entidades

### 2.1. Agregado: **Group**

```
┌──────────────────────────────────────┐
│          «Aggregate Root»            │
│              Group                   │
├──────────────────────────────────────┤
│  id: GroupId                         │
│  name: String                        │
│  members: List<UserId>               │
│  inviteCode: InviteCode              │
│  createdAt: LocalDateTime            │
└──────────────────────────────────────┘
```

**Comportamiento:**
- Se crea con un nombre; el creador es automáticamente miembro.
- Genera un **código de invitación** (`InviteCode`) que otros usuarios pueden usar para unirse.
- El código de invitación puede **renovarse** por decisión de cualquier miembro (se invalida el anterior y se genera uno nuevo).
- Un miembro puede **salir** del grupo voluntariamente.
- Un usuario puede pertenecer a **varios grupos** simultáneamente.
- No existen roles de administrador: todos los miembros tienen los mismos permisos.
- Aísla tareas y ejecuciones: un usuario solo ve datos de sus grupos.
- **Si el último miembro sale, el grupo se elimina** junto con todo su histórico (tareas, ejecuciones, pruebas).

---

### 2.2. Agregado: **User**

```
┌──────────────────────────────────────┐
│          «Aggregate Root»            │
│              User                    │
├──────────────────────────────────────┤
│  id: UserId (GUID)                   │
│  email: Email                        │
│  imageUrl: String?                   │
└──────────────────────────────────────┘
```

**Decisiones de diseño:**
- Se identifica por un **GUID inmutable** (`UserId`), no por email.
- El **email es único** y puede cambiar (el GUID no cambia).
- No existe username: el usuario se identifica visualmente por su **email**.
- No se pueden eliminar cuentas (por ahora).

---

### 2.3. Agregado: **Task**

```
┌──────────────────────────────────────┐
│          «Aggregate Root»            │
│              Task                    │
├──────────────────────────────────────┤
│  id: TaskId                          │
│  name: TaskName                      │
│  groupId: GroupId                    │
│  createdBy: UserId                   │
│  createdAt: LocalDateTime            │
└──────────────────────────────────────┘
```

**Comportamiento:**
- CRUD simple: crear, leer, editar nombre.
- Pertenece a un grupo.
- **No se puede eliminar** (decisión de negocio), salvo cuando se elimina el grupo entero.
- Cualquier miembro del grupo puede crear y editar tareas.

---

### 2.4. Agregado: **Execution**

```
┌──────────────────────────────────────────┐
│           «Aggregate Root»               │
│              Execution                   │
├──────────────────────────────────────────┤
│  id: ExecutionId                         │
│  taskId: TaskId                          │
│  executedBy: UserId                      │
│  executedAt: LocalDateTime               │
│  proof: ExecutionProof                   │
│  status: ExecutionStatus                 │
│  confirmedBy: UserId?                    │
│  confirmedAt: LocalDateTime?             │
│  rejectedBy: UserId?                     │
│  rejectedAt: LocalDateTime?              │
│  rejectionReason: String?                │
└──────────────────────────────────────────┘
         │
         │ contiene (exactamente 1)
         ▼
┌──────────────────────────────────────────┐
│          «Value Object»                  │
│          ExecutionProof                   │
├──────────────────────────────────────────┤
│  imageUrl: String                        │
│  uploadedAt: LocalDateTime               │
└──────────────────────────────────────────┘
```

**Ciclo de vida:**

```
              RegisterExecution
              (con exactamente 1 foto)
                    │
                    ▼
              ┌──────────┐
              │ PENDING  │
              └────┬─────┘
                   │
         ┌────────┴────────┐
         │                 │
    ConfirmExecution   RejectExecution
    (acepta prueba)    (razón obligatoria)
         │                 │
         ▼                 ▼
   ┌───────────┐    ┌───────────┐
   │ CONFIRMED │    │ REJECTED  │
   └───────────┘    └───────────┘
                          │
                          │ El ejecutor puede reintentar
                          │ registrando una nueva ejecución
                          ▼
                    ┌──────────┐
                    │ PENDING  │  (nueva Execution, nueva foto)
                    └──────────┘
```

**Reglas:**
- Se crea siempre con `status = PENDING` y **exactamente una prueba fotográfica**.
- Solo puede ser confirmado/rechazado por un usuario **distinto** al ejecutor.
- El confirmador/rechazador **revisa la prueba** (imagen) antes de decidir.
- Al rechazar se requiere una **razón** (`rejectionReason`).
- Al confirmar se registra **quién** (`confirmedBy`) y **cuándo** (`confirmedAt`).
- Una vez confirmado o rechazado, el estado es **definitivo** (no se revoca).
- Tras un rechazo, el ejecutor puede **registrar una nueva ejecución** sobre la misma tarea (reintento con nueva prueba).
- No se pueden eliminar ejecuciones, salvo cuando se elimina el grupo entero.

---

## 3. Objetos de Valor (Value Objects)

| Value Object | Tipo interno | Validación |
|---|---|---|
| `UserId` | `String` (GUID) | No vacío, formato UUID |
| `GroupId` | `String` (GUID) | No vacío, formato UUID |
| `TaskId` | `String` (GUID) | No vacío, formato UUID |
| `ExecutionId` | `String` (GUID) | No vacío, formato UUID |
| `Email` | `String` | No vacío, formato email válido, **único en el sistema** |
| `TaskName` | `String` | No vacío |
| `ExecutionStatus` | `Enum` | `PENDING`, `CONFIRMED`, `REJECTED` |
| `InviteCode` | `String` | Código alfanumérico generado, no vacío |
| `ExecutionProof` | Compuesto | `imageUrl` (no vacío) + `uploadedAt` |

### `ExecutionStatus`

```
┌──────────────────────────────┐
│      «Enumeration»           │
│      ExecutionStatus         │
├──────────────────────────────┤
│  PENDING                     │
│  CONFIRMED                   │
│  REJECTED                    │
└──────────────────────────────┘
```

### `InviteCode`

```
┌──────────────────────────────┐
│      «Value Object»          │
│       InviteCode             │
├──────────────────────────────┤
│  code: String                │
│  generatedAt: LocalDateTime  │
└──────────────────────────────┘
```

- Se genera automáticamente al crear un grupo.
- Puede **renovarse** por decisión de cualquier miembro: se invalida el código anterior y se genera uno nuevo.
- No tiene fecha de expiración automática; solo se invalida al regenerar.

### `ExecutionProof`

```
┌──────────────────────────────┐
│      «Value Object»          │
│      ExecutionProof          │
├──────────────────────────────┤
│  imageUrl: String            │
│  uploadedAt: LocalDateTime   │
└──────────────────────────────┘
```

- **Exactamente una imagen** por ejecución (no múltiples).
- **Obligatoria** al registrar una ejecución.
- Contiene la URL de la imagen subida (ej: foto del cubo de basura vacío).
- El confirmador/rechazador revisa esta imagen para decidir.

---

## 4. Eventos de Dominio

### Contexto: Identidad y Acceso

| Evento | Datos |
|---|---|
| `UserRegistered` | userId, email |
| `UserLoggedIn` | userId, email |
| `UserEmailChanged` | userId, oldEmail, newEmail |

### Contexto: Grupos

| Evento | Datos |
|---|---|
| `GroupCreated` | groupId, name, createdBy, inviteCode |
| `MemberJoinedGroup` | groupId, userId |
| `MemberLeftGroup` | groupId, userId |
| `InviteCodeRenewed` | groupId, oldCode, newCode |
| `GroupDissolved` | groupId, lastMemberUserId |

### Contexto: Gestión de Tareas

| Evento | Datos |
|---|---|
| `TaskCreated` | taskId, groupId, name, createdBy, createdAt |
| `TaskUpdated` | taskId, newName |

### Contexto: Ejecuciones y Validación

| Evento | Datos |
|---|---|
| `ExecutionRegistered` | executionId, taskId, executedBy, executedAt, proofImageUrl |
| `ExecutionConfirmed` | executionId, confirmedBy, confirmedAt |
| `ExecutionRejected` | executionId, rejectedBy, rejectedAt, rejectionReason |

---

## 5. Comandos

### Contexto: Identidad y Acceso

| Comando | Actor | Datos |
|---|---|---|
| `RegisterWithEmail` | Usuario anónimo | email, password |
| `LoginWithEmail` | Usuario anónimo | email, password |
| `ChangeEmail` | Usuario autenticado | userId, newEmail |

### Contexto: Grupos

| Comando | Actor | Datos |
|---|---|---|
| `CreateGroup` | Usuario autenticado | name |
| `JoinGroupByCode` | Usuario autenticado | inviteCode |
| `LeaveGroup` | Miembro del grupo | groupId |
| `RenewInviteCode` | Miembro del grupo | groupId |

### Contexto: Gestión de Tareas

| Comando | Actor | Datos |
|---|---|---|
| `CreateTask` | Miembro del grupo | groupId, name |
| `UpdateTask` | Miembro del grupo | taskId, newName |

### Contexto: Ejecuciones y Validación

| Comando | Actor | Datos |
|---|---|---|
| `RegisterExecution` | Miembro del grupo (ejecutor) | taskId, proofImage (exactamente 1) |
| `ConfirmExecution` | Miembro del grupo (≠ ejecutor) | executionId |
| `RejectExecution` | Miembro del grupo (≠ ejecutor) | executionId, reason |

---

## 6. Reglas de Negocio / Invariantes

### Identidad y Acceso

| # | Regla |
|---|---|
| R1 | El **email es único** en todo el sistema. |
| R2 | Un usuario se identifica por un **GUID inmutable**; el email puede cambiar. |
| R3 | Un usuario debe estar **autenticado** para realizar cualquier operación. |

### Grupos

| # | Regla |
|---|---|
| R4 | Un usuario debe pertenecer a al menos un **grupo** para ver tareas y ejecuciones. |
| R5 | Las tareas y ejecuciones están **aisladas por grupo** (multitenancy). |
| R6 | Un usuario puede pertenecer a **varios grupos** simultáneamente. |
| R7 | Un grupo se une mediante un **código de invitación** (`InviteCode`). |
| R8 | El código de invitación puede **renovarse** por decisión de cualquier miembro; al renovarse el anterior queda **invalidado**. El código **no expira** por tiempo. |
| R9 | Cualquier miembro puede renovar el código (no hay roles de administrador). |
| R10 | Un miembro puede **salir** voluntariamente de un grupo. |
| R11 | **No puede haber grupos sin miembros.** Si el último miembro sale, el grupo se **disuelve**: se eliminan el grupo, todas sus tareas, todas sus ejecuciones y todas las pruebas fotográficas asociadas. |

### Tareas

| # | Regla |
|---|---|
| R12 | Cualquier **miembro del grupo** puede crear y editar tareas. |
| R13 | Las tareas **no se pueden eliminar** individualmente (solo se eliminan si el grupo se disuelve). |
| R14 | El nombre de la tarea **no puede estar vacío**. |

### Ejecuciones

| # | Regla |
|---|---|
| R15 | Una ejecución se crea siempre con `status = PENDING` y `executedAt = now()`. |
| R16 | Al registrar una ejecución es **obligatorio adjuntar exactamente una prueba fotográfica** (`ExecutionProof`). |
| R17 | Solo un usuario **distinto al ejecutor** puede confirmar o rechazar una ejecución. |
| R18 | El confirmador/rechazador debe **revisar la prueba** (imagen) antes de decidir. |
| R19 | Al **rechazar** una ejecución es **obligatorio** proporcionar una razón (`rejectionReason`). |
| R20 | Al **confirmar** se registra quién confirmó (`confirmedBy`) y cuándo (`confirmedAt`). |
| R21 | Una ejecución confirmada o rechazada **no se puede revocar** (estado definitivo). |
| R22 | Tras un **rechazo**, el ejecutor puede registrar una **nueva ejecución** sobre la misma tarea (reintento con nueva prueba). |
| R23 | Las ejecuciones **no se pueden eliminar** individualmente (solo se eliminan si el grupo se disuelve). |
| R24 | No hay límite de ejecuciones por usuario/tarea/día. |

---

## 7. Event Storming

### Leyenda

```
🟧 Evento de Dominio (algo que ocurrió)
🟦 Comando (intención del usuario)
🟪 Agregado (quién procesa el comando)
🟨 Política / Regla de Negocio
👤 Actor
📖 Read Model (proyección de lectura)
```

---

### Flujo 1: Registro e Ingreso al Sistema

```
👤 Usuario anónimo
    │
    ├── 🟦 RegisterWithEmail(email, password)
    │       └── 🟪 User
    │               └── 🟧 UserRegistered { userId, email }
    │                       └── 🟨 R1: email debe ser único
    │
    ├── 🟦 LoginWithEmail(email, password)
    │       └── 🟪 [Sistema Auth Externo]
    │               └── 🟧 UserLoggedIn { userId, email }
    │
    └── 📖 CurrentUser → User? (identificado por GUID, no por email)
```

---

### Flujo 2: Grupos, Invitación y Membresía

```
👤 Usuario autenticado
    │
    ├── 🟦 CreateGroup(name)
    │       └── 🟪 Group
    │               ├── 🟧 GroupCreated { groupId, name, createdBy, inviteCode }
    │               └── 🟧 MemberJoinedGroup { groupId, userId }
    │                       └── 🟨 El creador es automáticamente miembro
    │
    ├── 🟦 JoinGroupByCode(inviteCode)
    │       └── 🟪 Group
    │               └── 🟧 MemberJoinedGroup { groupId, userId }
    │                       ├── 🟨 R7: se une mediante código de invitación
    │                       └── 🟨 R6: puede estar en varios grupos
    │
    ├── 🟦 RenewInviteCode(groupId)
    │       └── 🟪 Group
    │               └── 🟧 InviteCodeRenewed { groupId, oldCode, newCode }
    │                       └── 🟨 R8: código anterior invalidado, sin expiración por tiempo
    │
    ├── 🟦 LeaveGroup(groupId)
    │       └── 🟪 Group
    │               ├── 🟧 MemberLeftGroup { groupId, userId }
    │               │       └── 🟨 R10: salida voluntaria
    │               │
    │               └── 🟨 R11: si era el último miembro:
    │                       └── 🟧 GroupDissolved { groupId, lastMemberUserId }
    │                               └── Se eliminan: grupo + tareas + ejecuciones + fotos
    │
    └── 📖 MyGroups → List<Group> (con inviteCode visible)
```

---

### Flujo 3: Gestión de Tareas

```
👤 Miembro del grupo
    │
    ├── 🟦 CreateTask(groupId, name)
    │       └── 🟪 Task
    │               └── 🟧 TaskCreated { taskId, groupId, name, createdBy, createdAt }
    │                       └── 🟨 R14: nombre no vacío
    │
    ├── 🟦 UpdateTask(taskId, newName)
    │       └── 🟪 Task
    │               └── 🟧 TaskUpdated { taskId, newName }
    │                       └── 🟨 R14: nombre no vacío
    │
    └── 📖 TaskList(groupId) → List<Task> (filtrable por nombre)
            └── 🟨 R5: solo tareas del grupo del usuario
```

---

### Flujo 4: Registro de Ejecuciones (con prueba)

```
👤 Miembro del grupo (ejecutor)
    │
    ├── 🟦 RegisterExecution(taskId, proofImage)
    │       └── 🟪 Execution
    │               └── 🟧 ExecutionRegistered { executionId, taskId, executedBy, executedAt, proofImageUrl }
    │                       ├── 🟨 R15: status = PENDING, executedAt = now()
    │                       └── 🟨 R16: exactamente 1 prueba fotográfica obligatoria
    │
    │       Ejemplo: tarea "Tirar la basura"
    │       → El ejecutor saca una foto del cubo con las bolsas vacías
    │       → La foto se sube y queda vinculada a la ejecución
    │
    └── 📖 TaskExecutions(taskId) → List<ExecutionWithUser>
            filtrable por usuario (chips)
            muestra: quién ejecutó, cuándo, estado, miniatura de la prueba
```

---

### Flujo 5: Confirmación / Rechazo con Revisión de Prueba

```
👤 Miembro del grupo (confirmador ≠ ejecutor)
    │
    ├── 📖 OpenExecutions → List<ExecutionWithUserAndTask>
    │       └── 🟨 Solo muestra ejecuciones donde:
    │              - executedBy ≠ currentUserId (de otros)
    │              - status = PENDING
    │              Agrupadas por fecha
    │              Cada ejecución muestra la prueba (imagen) para revisión
    │
    ├── 🟦 ConfirmExecution(executionId)
    │       └── 🟪 Execution
    │               ├── 🟨 R17: confirmedBy ≠ executedBy
    │               ├── 🟨 R18: el confirmador revisó la prueba
    │               ├── 🟨 R21: solo si status = PENDING
    │               └── 🟧 ExecutionConfirmed { executionId, confirmedBy, confirmedAt }
    │                       └── 🟨 R20: se registra quién y cuándo
    │
    ├── 🟦 RejectExecution(executionId, reason)
    │       └── 🟪 Execution
    │               ├── 🟨 R17: rejectedBy ≠ executedBy
    │               ├── 🟨 R18: el rechazador revisó la prueba
    │               ├── 🟨 R19: reason es obligatorio
    │               ├── 🟨 R21: solo si status = PENDING
    │               └── 🟧 ExecutionRejected { executionId, rejectedBy, rejectedAt, reason }
    │                       │
    │                       └── 🟨 R22: el ejecutor puede reintentar
    │                              registrando una nueva ejecución
    │                              con nueva prueba sobre la misma tarea
    │
    └── 📖 ExecutionsHistory → List<Execution>
            muestra: fecha/hora, quién ejecutó, prueba (imagen),
                     quién confirmó/rechazó, cuándo, razón de rechazo
```

---

### Flujo 6: Disolución de Grupo

```
👤 Último miembro del grupo
    │
    └── 🟦 LeaveGroup(groupId)
            └── 🟪 Group
                    ├── 🟧 MemberLeftGroup { groupId, userId }
                    │
                    └── 🟨 R11: members.size == 0 tras la salida
                            └── 🟧 GroupDissolved { groupId, lastMemberUserId }
                                    │
                                    ├── Se eliminan todas las Task del grupo
                                    ├── Se eliminan todas las Execution del grupo
                                    ├── Se eliminan todas las ExecutionProof (imágenes)
                                    └── Se elimina el Group
```

---

### Diagrama Temporal Completo

```
Tiempo ──────────────────────────────────────────────────────────────────────────────────────►

👤 Ana                                            👤 Bob
 │                                                 │
 │ RegisterWithEmail(ana@mail.com)                 │
 │──────────────►                                  │
 │  🟧 UserRegistered                             │
 │                                                 │
 │ CreateGroup("Piso 3B")                         │
 │──────────────►                                  │
 │  🟧 GroupCreated (inviteCode="ABC123")          │
 │  🟧 MemberJoinedGroup(Ana)                     │
 │                                                 │
 │  Ana comparte el código "ABC123" con Bob        │
 │                                                 │
 │                                                 │ RegisterWithEmail(bob@mail.com)
 │                                                 │──────────────►
 │                                                 │  🟧 UserRegistered
 │                                                 │
 │                                                 │ JoinGroupByCode("ABC123")
 │                                                 │──────────────►
 │                                                 │  🟧 MemberJoinedGroup(Bob)
 │                                                 │
 │ RenewInviteCode("Piso 3B")                     │
 │──────────────►                                  │
 │  🟧 InviteCodeRenewed (old="ABC123" new="XYZ") │
 │                                                 │
 │ CreateTask("Tirar la basura")                   │
 │──────────────►                                  │
 │  🟧 TaskCreated                                 │
 │                                                 │
 │ RegisterExecution(task1, 📸 foto cubo vacío)    │
 │──────────────►                                  │
 │  🟧 ExecutionRegistered (PENDING + proof)       │
 │                                                 │
 │                                                 │ Ve ejecución pendiente de Ana
 │                                                 │ Revisa la foto del cubo vacío ✓
 │                                                 │
 │                                                 │ ConfirmExecution(exec1)
 │                                                 │──────────────►
 │                                                 │  🟧 ExecutionConfirmed
 │                                                 │      confirmedBy=Bob, confirmedAt=now()
 │                                                 │
 │ Ve ejecución confirmada ✓                      │
 │                                                 │
 │ RegisterExecution(task1, 📸 foto cubo lleno)    │
 │──────────────►                                  │
 │  🟧 ExecutionRegistered (PENDING + proof)       │
 │                                                 │
 │                                                 │ Ve ejecución pendiente de Ana
 │                                                 │ Revisa la foto: el cubo está lleno ✗
 │                                                 │
 │                                                 │ RejectExecution(exec2, "El cubo sigue lleno")
 │                                                 │──────────────►
 │                                                 │  🟧 ExecutionRejected
 │                                                 │      rejectedBy=Bob
 │                                                 │      reason="El cubo sigue lleno"
 │                                                 │
 │ Ve ejecución rechazada ✗                       │
 │ Decide reintentar                               │
 │                                                 │
 │ RegisterExecution(task1, 📸 foto cubo VACÍO)    │
 │──────────────►                                  │
 │  🟧 ExecutionRegistered (PENDING + nueva proof) │
 │                                                 │
 │                                                 │ Revisa nueva foto: cubo vacío ✓
 │                                                 │ ConfirmExecution(exec3)
 │                                                 │──────────────►
 │                                                 │  🟧 ExecutionConfirmed
 │                                                 │
 │ Ve ejecución confirmada ✓                      │
 │                                                 │
 │ ─ ─ ─ ─ Más adelante ─ ─ ─ ─                   │
 │                                                 │
 │ LeaveGroup("Piso 3B")                          │
 │──────────────►                                  │
 │  🟧 MemberLeftGroup(Ana)                       │
 │                                                 │
 │                                                 │ LeaveGroup("Piso 3B")
 │                                                 │──────────────►
 │                                                 │  🟧 MemberLeftGroup(Bob)
 │                                                 │  🟧 GroupDissolved("Piso 3B")
 │                                                 │      → grupo + tareas + ejecuciones + fotos eliminados
```

---

## 8. Preguntas Resueltas

| # | Pregunta | Respuesta |
|---|---|---|
| P1 | ¿Existen grupos? | **Sí.** Los datos se aíslan por grupo. |
| P2 | ¿Username o email? ¿Unicidad? | **Email único**, no username. El usuario se identifica por GUID (inmutable), el email puede cambiar. |
| P3 | ¿Cambiar username? | N/A — no existe username. El email sí se puede cambiar. |
| P4 | ¿Eliminar cuentas / logout? | **No** por ahora. |
| P5 | ¿Quién crea tareas? | Cualquier miembro del grupo. |
| P6 | ¿Editar tareas? | Sí, editar nombre (CRUD simple). |
| P7 | ¿Eliminar tareas? | **No** individualmente. Se eliminan solo al disolver el grupo. |
| P8 | ¿Más atributos en tarea? | **No** por ahora. Solo nombre. |
| P9 | ¿Eliminar ejecuciones? | **No** individualmente. Se eliminan solo al disolver el grupo. |
| P10 | ¿Auto-confirmación? | **No.** Debe validarse en backend (R17). |
| P11 | ¿Registrar quién/cuándo confirmó? | **Sí.** `confirmedBy` + `confirmedAt`. |
| P12 | ¿Rechazar ejecuciones? | **Sí.** Con razón obligatoria (`rejectionReason`). |
| P13 | ¿Límite de ejecuciones? | **No.** Sin límites. |
| P14 | ¿Revocar confirmación? | **No.** Estado definitivo. |
| P15 | ¿Qué muestra el historial? | Fecha/hora, quién ejecutó, prueba (imagen), quién confirmó/rechazó, cuándo, razón de rechazo. |
| P16 | ¿Notificaciones? | **No** por ahora. |
| P17 | ¿Recordatorios? | **No** por ahora. |
| P18 | ¿Varios grupos por usuario? | **Sí.** Un usuario puede pertenecer a varios grupos simultáneamente (R6). |
| P19 | ¿Cómo se une a un grupo? | Mediante un **código de invitación** renovable (R7, R8). |
| P20 | ¿Puede un miembro salir del grupo? | **Sí.** Salida voluntaria (R10). |
| P21 | ¿Roles de administrador? | **No.** Todos los miembros tienen los mismos permisos (R9). |
| P22 | ¿Reintento tras rechazo? | **Sí.** Se puede registrar una nueva ejecución sobre la misma tarea con nueva prueba (R22). |
| P23 | ¿Pruebas en ejecuciones? | **Sí.** Prueba fotográfica obligatoria (`ExecutionProof`) al registrar. El confirmador la revisa para decidir (R16, R18). |
| P24 | ¿Dónde se almacenan las imágenes? | En **blob storage** (nube) o **sistema de archivos del servidor** (on-premise), dependiendo del despliegue. Es decisión de infraestructura, no de dominio. |
| P25 | ¿Tamaño/resolución de imágenes? | Las imágenes deben ser **reescaladas** antes de subir (los móviles actuales generan fotos muy grandes). Es decisión de infraestructura/aplicación, no de dominio. |
| P26 | ¿Una o varias imágenes por prueba? | **Exactamente una** imagen por ejecución (R16). |
| P27 | Cuando un grupo queda **sin miembros** (todos salieron), ¿qué pasa con el grupo y sus datos? ¿Se elimina o queda huérfano? | **Se elimina.** Si el último miembro sale, el grupo se **disuelve** y se eliminan todas sus tareas, ejecuciones y pruebas (R11). |
| P28 | ¿El código de invitación tiene **fecha de expiración** o solo se invalida al renovar? | **No expira** por tiempo. Solo se invalida cuando un miembro decide **regenerarlo** (R8). |
