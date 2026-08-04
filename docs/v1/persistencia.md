# Persistencia (Room)

`AppDatabase` — `rooms/appDatabase/AppDatabase.kt`, versión **11**,
`exportSchema = false`. Nombre del fichero: `app_database`.

## Entidades

### `user`
```kotlin
@Entity(tableName = "user")
data class User(
    @PrimaryKey val id: Int,
    val username: String,
    val email: String,
    val token: String,
    val expirationDate: Long
)
```
Fila única (los DAO usan `LIMIT 1`). En modo offline la construye
`AuthRepository.buildLocalUser`: `id` derivado de
`System.currentTimeMillis() % Int.MAX_VALUE`, `token = ""`,
`expirationDate = Long.MAX_VALUE`.

### `note`
```kotlin
@Entity(tableName = "note")
data class Note(
    @PrimaryKey val id: Int,
    val score: Int,
    val attempt: Int,
    val date: String,
    val subjectId: Int,
    val subjectName: String
)
```
Resultado de un quiz. `date` es `String`, no timestamp — no se puede
ordenar cronológicamente de forma fiable.

### `materia`
```kotlin
@Entity(tableName = "materia")
data class Materia(@PrimaryKey val id: Int, val name: String)
```
Catálogo fijo de 5 filas, sembrado al arrancar.

### `pending_requests`
```kotlin
@Entity(tableName = "pending_requests")
data class PendingRequest(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val endpoint: String,
    val payload: String,
    val method: String,
    val userId: Int,
    val timestamp: Long = System.currentTimeMillis()
)
```
Cola de reintentos para peticiones hechas sin conexión.

## DAOs

### `UserDao`
`insertUser` (REPLACE) · `getUser(): User?` · `deleteUser()` ·
`getCurrentUserId(): Int`

`getCurrentUserId` devuelve `Int` no nulable sobre
`SELECT id FROM User LIMIT 1`. Sin fila, Room devuelve 0 en lugar de
fallar.

### `NoteDao`
`getAllNotes` · `getNoteById` · `getNotes(quizName)` · `insertNote` ·
`insertNotes` · `deleteAllNotes` · `hasReachedMaxAttempts(subjectId)`

`hasReachedMaxAttempts` es la consulta que gobierna el bloqueo de
quizzes.

### `MateriaDao`
`getMateriaById` · `getAllMaterias` · `insertAll`

`insertAll` usa `@Insert` sin `onConflict`, así que una segunda siembra
lanza `SQLiteConstraintException`. Está protegido por la doble guarda de
`loadInitialMaterias`.

### `PendingRequestDao`
`insertRequest` · `getAllRequests` · `deleteRequestById`

## Provisión e inicialización

`DatabaseProvider` (`rooms/appDatabase/DatabaseProvider.kt`) es un
singleton con doble comprobación (`@Volatile` + `synchronized`).

```kotlin
Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app_database")
    .fallbackToDestructiveMigration()
    .build()
```

> **`fallbackToDestructiveMigration()` borra toda la base en cada subida
> de versión.** Con la app ya en modo offline, la BD local es la única
> copia de los resultados de quizzes del usuario. Cualquier cambio de
> esquema los destruye sin aviso. Ver [deuda-tecnica.md](deuda-tecnica.md).

### Siembra de materias

`loadInitialMaterias(context)` inserta el catálogo una sola vez:

| id | name |
|---|---|
| 1 | TECNICAS |
| 2 | PROCEDIMIENTOS |
| 3 | ADMINISTRACION |
| 4 | URGENCIAS |
| 5 | PROCEDIMIENTOS2 |

Doble guarda: la bandera `materias_loaded` en el `SharedPreferences`
`app_preferences` **y** una comprobación de que la tabla esté vacía. La
bandera solo se marca tras verificar que las filas están realmente en
Room.

Se lanza en un `CoroutineScope(Dispatchers.IO)` creado ad hoc, sin
cancelación ligada a ningún ciclo de vida (`DatabaseProvider.kt:34`).

## Mapeo DTO ↔ entidad

`utils/NoteMapper.kt` — `ApiNote.toRoomEntity(context)`. Resuelve el
`subjectName` consultando `MateriaDao` por `subjectId`, lo que hace del
mapper una función `suspend` con acceso a BD.
