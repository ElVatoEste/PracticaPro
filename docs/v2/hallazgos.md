# Hallazgos: pérdida de datos

Tres defectos en `NotesRepository`. Los tres destruyen datos del usuario.
Los tres están activos hoy.

## H1 — Las puntuaciones de quiz nunca se guardan

**Severidad: crítica. Ocurre en el 100 % de los casos hoy.**

### Traza

```
AsepsiaQuiz.kt:47            notesViewModel.addNote(context, idMateria = 1, puntaje = score)
ProcQuiz.kt:49               notesViewModel.addNote(context, idMateria = 2, puntaje = score)
TrueFalseQuizScreen.kt:46    notesViewModel.addNote(context, idMateria = 5, puntaje = score)
      ↓
NotesViewModel.kt:32         addNote → NotesRepository.createNote(context, request)
      ↓
NotesRepository.kt:74        createNote
```

### El código

```kotlin
suspend fun createNote(context: Context, request: CreateNoteRequest): Result<ApiNote> {
    return runCatching {
        val isNetworkAvailable = NetworkObserver.isNetworkAvailable.first()
        if (isNetworkAvailable) {
            val note = notesService.createNote(request)   // ← A
            val localNote = note.toRoomEntity(context)
            saveNoteLocally(context, localNote)           // ← única escritura en Room
            note
        } else {
            savePendingRequest(context, "notas", request) // ← B
            throw Exception("Nota guardada localmente. Se enviará cuando haya conexión.")
        }
    }.recoverCatching { throwable ->
        throw Exception("Error al crear la nota: ${throwable.message}")
    }
}
```

**La tabla `note` solo se escribe en la rama A, después de que la API
responda.** Con el backend caído, esa línea nunca se alcanza:

| Situación | Recorrido | Se guarda en `note` |
|---|---|---|
| Sin red | rama B | **No.** Solo `pending_requests` |
| Con red, API caída | rama A, `notesService.createNote` lanza | **No.** Nada |
| Con red, API viva | rama A completa | Sí |

Como el backend está caído, hoy solo se dan los dos primeros casos.

### El mensaje miente

```kotlin
savePendingRequest(context, "notas", request)
throw Exception("Nota guardada localmente. Se enviará cuando haya conexión.")
```

Dice "guardada localmente". Lo que se guardó fue una fila en
`pending_requests`, no la nota. El usuario lee que su resultado está a
salvo cuando no lo está.

### Consecuencia en cadena

`NoteDao.hasReachedMaxAttempts(subjectId)` consulta la tabla `note`. Si
nunca se escribe, siempre devuelve `false` y **el límite de intentos de
los quizzes jamás se activa**. Lo que parecía una funcionalidad correcta
es un efecto colateral de la pérdida de datos.

---

## H2 — La cola offline se vacía aunque falle

**Severidad: alta.**

`NotesRepository.processPendingRequests`, `NotesRepository.kt:167`:

```kotlin
for (request in pendingRequests) {
    try {
        val payload = Json.decodeFromString<CreateOfflineNoteRequest>(request.payload)
        notesService.createOfflineNote(payload)
    } catch (e: Exception) {
        Log.e(...)   // se registra y se sigue
    } finally {
        pendingRequestDao.deleteRequestById(request.id)   // ← siempre
    }
}
```

El `finally` borra la petición **con éxito o sin él**. Con el backend
caído, la secuencia es:

1. El usuario termina un quiz sin conexión → se encola.
2. Vuelve la conexión → `MainScreen:45` dispara `processPendingRequests`.
3. `createOfflineNote` falla (servidor caído).
4. El `finally` borra la fila igual.

La cola se autodestruye en el primer evento de conectividad. El
comentario del código lo admite: *"la petición se elimina de todos
modos"*.

Una cola de reintentos que borra ante el fallo no es una cola de
reintentos.

---

## H3 — Toda petición sale con `Authorization` vacío

**Severidad: alta.** Documentado en [v1/backend.md](../v1/backend.md),
se repite aquí por su efecto sobre H1 y H2.

`AuthRepository.buildLocalUser` guarda `token = ""`. El interceptor de
`ApiClient.kt:37` filtra por nulidad:

```kotlin
token?.let { requestBuilder.addHeader("Authorization", "Bearer $it") }
```

`""` no es `null`, así que la cabecera se añade vacía y el servidor
responde 401. Aunque el backend volviera mañana sin tocar nada más, H1 y
H2 seguirían destruyendo datos: la API rechazaría cada petición.

**Arreglo:**
```kotlin
token?.takeIf { it.isNotBlank() }?.let { requestBuilder.addHeader("Authorization", "Bearer $it") }
```

---

## Efecto combinado

```
Quiz terminado
   │
   ├─ ¿Hay red?
   │     ├─ No  → encola en pending_requests
   │     │         └─ vuelve la red → POST falla (backend caído)
   │     │                            └─ finally borra la fila     ✗ perdido
   │     └─ Sí  → POST falla (backend caído o 401 por token vacío)
   │               └─ nada se escribe en Room                      ✗ perdido
   │
   └─ tabla `note` intacta, vacía
         └─ hasReachedMaxAttempts siempre false
               └─ intentos ilimitados
```

Hoy, ninguna puntuación sobrevive al cierre de la app.

---

## Verificación

Para confirmarlo en un dispositivo antes de tocar nada:

```bash
adb shell "run-as com.vatodev.practicapro cat databases/app_database" > /tmp/db.sqlite
sqlite3 /tmp/db.sqlite "SELECT COUNT(*) FROM note; SELECT COUNT(*) FROM pending_requests;"
```

Completar un quiz y repetir. `note` debe seguir en 0.

Alternativa por logcat: `NotesRepository` ya registra cada paso.

```bash
adb logcat -s NotesRepository:D GetNotes:D SaveNotes:D
```

Con el backend caído se verá el `Error al crear la nota` y nunca el
`Nota creada y guardada en Room`.
