<p align="center">
  <img src="app/src/main/ic_launcher-playstore.png" alt="WireChef Logo" width="120" />
</p>

<h1 align="center">WireChef</h1>

<p align="center">
  Sistema de gestión de pedidos en tiempo real para restaurantes.<br/>
  Comunicación instantánea entre meseros y cocineros mediante WebSockets.
</p>

---

## Descripcion general

WireChef es una aplicacion movil Android que digitaliza el flujo de pedidos dentro de un restaurante. Un mesero toma la orden desde su dispositivo, la envia a cocina y el cocinero la recibe al instante. Todo el ciclo de vida del pedido (pendiente, en preparacion, listo para entregar) se actualiza en tiempo real sin necesidad de recargar manualmente.

### Flujo del pedido

```
Mesero crea orden  -->  WebSocket notifica  -->  Cocinero ve "En espera"
                                                        |
                                            Cocinero marca "Preparar"
                                                        |
                                            Estado cambia a "Preparando"
                                                        |
                                          Cocinero marca "Listo para entregar"
                                                        |
                                  WebSocket notifica  -->  Mesero ve "Listo"
```

Cada transicion se refleja en ambas pantallas de forma inmediata gracias a la conexion persistente por WebSocket.

---

## Arquitectura

El proyecto sigue **Clean Architecture** con tres capas bien separadas, combinada con el patron **MVVM** (Model-View-ViewModel) para la capa de presentacion.

```
app/src/main/java/com/example/wirechef/
|
|-- core/                          # Infraestructura compartida
|   |-- di/                        # Modulos de Hilt (NetworkModule, WebSocket)
|   |-- navigation/                # NavHost y definicion de rutas
|   |-- session/                   # SessionManager (SharedPreferences)
|   |-- ui/theme/                  # Colores, tipografia, tema Material 3
|
|-- features/
|   |-- order/
|   |   |-- data/                  # Retrofit API, DTOs, mappers, repositorio impl
|   |   |-- domain/                # Entidades, interfaz de repositorio, casos de uso
|   |   |-- presentation/          # Screens, ViewModels, componentes UI
|   |
|   |-- product/
|   |   |-- data/                  # API de productos, DTOs, mapper
|   |   |-- domain/                # Entidad Product, repositorio, caso de uso
|   |
|   |-- user/
|       |-- data/                  # API de usuarios, DTOs, mapper
|       |-- domain/                # Entidad User, repositorio, casos de uso
|       |-- presentation/          # LoginScreen, LoginViewModel
```

### Por que Clean Architecture

- **domain/** no depende de ningun framework. Las entidades (`Order`, `Product`, `User`) y las interfaces de repositorio son Kotlin puro.
- **data/** implementa esas interfaces usando Retrofit, mappers DTO-a-dominio y modulos de inyeccion.
- **presentation/** contiene ViewModels con `StateFlow` y Composables que solo observan estado.

Esto permite cambiar la fuente de datos (por ejemplo, migrar de REST a gRPC) sin tocar la logica de negocio ni la UI.

---

## Inyeccion de dependencias con Hilt

Hilt se encarga de todo el grafo de dependencias de forma automatizada. La configuracion se distribuye en modulos:

| Modulo | Responsabilidad |
|---|---|
| `NetworkModule` | Provee `OkHttpClient` y `Retrofit` como singletons globales |
| `OrderNetworkModule` | Provee `OrderApi` a partir del Retrofit compartido |
| `OrderRepositoryModule` | Vincula `OrderRepositoryImpl` a la interfaz `OrderRepository` |
| `ProductNetworkModule` | Provee `ProductApi` |
| `ProductRepositoryModule` | Vincula `ProductRepositoryImpl` a `ProductRepository` |
| `UserNetworkModule` | Provee `UserApi` |
| `UserRepositoryModule` | Vincula `UserRepositoryImpl` a `UserRepository` |

### Anotaciones clave

- `@HiltAndroidApp` en `WireChefHiltApp` — inicializa el contenedor de Hilt a nivel de aplicacion.
- `@AndroidEntryPoint` en `MainActivity` — permite la inyeccion en la actividad.
- `@HiltViewModel` + `@Inject constructor(...)` en cada ViewModel — Hilt resuelve automaticamente todas las dependencias.
- `@Module` + `@InstallIn(SingletonComponent::class)` — los modulos viven durante toda la vida de la app.
- `@Singleton` — garantiza una unica instancia de `OkHttpClient`, `Retrofit` y `WireChefWebSocketListener`.
- `@Provides` — para clases de terceros que no se pueden anotar directamente (Retrofit, OkHttp).
- `@Binds` — para vincular implementaciones a sus interfaces (repositorios).

### Calificadores (Qualifiers)

El archivo `Qualifiers.kt` esta preparado para definir anotaciones `@Qualifier` personalizadas cuando se necesiten multiples instancias del mismo tipo (por ejemplo, diferentes `OkHttpClient` para distintos backends).

---

## WebSocket — Comunicacion en tiempo real

La clase `WireChefWebSocketListener` es un singleton inyectado por Hilt que gestiona la conexion WebSocket.

```
ws://50.16.170.148/ws?role={chef|waiter}&user_id={id}
```

### Como funciona

1. Al iniciar sesion, el ViewModel correspondiente llama a `webSocketListener.connect(role, userId)`.
2. El servidor mantiene la conexion abierta. Cuando ocurre un evento relevante, envia un mensaje JSON.
3. Los ViewModels escuchan el `SharedFlow<String>` de mensajes y reaccionan:
   - **Chef** escucha `"new_order"` y `"order_status_update"` para recargar la lista de ordenes.
   - **Waiter** escucha `"order_status_update"` para actualizar el estado de sus pedidos.

### SharedFlow como canal de mensajes

Se usa `MutableSharedFlow<String>(extraBufferCapacity = 10)` en lugar de `StateFlow` porque:

- Multiples suscriptores pueden observar el mismo flujo.
- `extraBufferCapacity = 10` evita que se pierdan mensajes si el colector esta brevemente ocupado.
- `tryEmit()` permite emitir desde el callback del WebSocket (hilo de OkHttp) sin suspender.

### Actualizaciones optimistas

Cuando el cocinero presiona "Preparar" o "Listo para entregar", la UI se actualiza instantaneamente sin esperar la respuesta del servidor. Si la llamada API falla, se recargan los datos del servidor para revertir.

---

## Concurrencia

### Corrutinas y ViewModelScope

Toda operacion asincrona se ejecuta dentro de `viewModelScope.launch { ... }`. Esto garantiza:

- **Cancelacion automatica**: cuando el ViewModel se destruye (por ejemplo, al navegar fuera de la pantalla), todas las corrutinas pendientes se cancelan.
- **Hilo principal seguro**: Retrofit con `suspend fun` ejecuta la red en un dispatcher de IO internamente, y la actualizacion de `StateFlow` ocurre en el hilo principal.

### StateFlow para estado reactivo

Cada ViewModel expone un `StateFlow<UiState>` inmutable. Los Composables lo observan con `collectAsState()`:

```kotlin
private val _uiState = MutableStateFlow(ChefState())
val uiState: StateFlow<ChefState> = _uiState.asStateFlow()
```

- `_uiState.update { ... }` es thread-safe (usa CAS internamente).
- El Composable se recompone automaticamente cuando el estado cambia.
- No hay callbacks manuales ni `LiveData` — todo es un flujo unidireccional.

### WebSocket + Corrutinas

El listener del WebSocket emite en un `SharedFlow` desde el hilo de OkHttp. El ViewModel recolecta ese flujo dentro de `viewModelScope`, lo que significa que los mensajes se procesan secuencialmente y las recargas de datos no se superponen desordenadamente.

---

## Gestion de sesion

`SessionManager` almacena el `USER_ID` en `SharedPreferences`. Es un singleton inyectado por Hilt que permite:

- Identificar al usuario actual para filtrar sus pedidos.
- Conectar el WebSocket con el ID correcto.
- Limpiar la sesion al cerrar sesion (`clearSession()`).

No se almacenan tokens ni contrasenas — el sistema actual usa autenticacion por nombre y rol.

---

## Navegacion

Se usa Jetpack Navigation Compose con tres rutas:

| Ruta | Pantalla | Rol |
|---|---|---|
| `login_screen` | Login | Ambos |
| `waiter_menu_screen` | Menu del mesero | Mesero |
| `chef_dashboard_screen` | Dashboard del cocinero | Cocinero |

Al hacer login, se navega a la ruta correspondiente segun el rol, y se elimina la pantalla de login del backstack con `popUpTo(inclusive = true)` para que el boton de atras no regrese al login.

---

## Stack tecnologico

| Tecnologia | Version | Uso |
|---|---|---|
| Kotlin | 2.0.21 | Lenguaje principal |
| Jetpack Compose | BOM 2025.01.00 | UI declarativa (Material 3) |
| Compose Navigation | 2.8.5 | Navegacion entre pantallas |
| Dagger Hilt | 2.59.1 | Inyeccion de dependencias |
| KSP | 2.0.21-1.0.26 | Procesador de anotaciones para Hilt |
| Retrofit | 2.11.0 | Cliente HTTP para la API REST |
| OkHttp WebSocket | (incluido en OkHttp) | Comunicacion en tiempo real |
| Gson | 2.11.0 | Serializacion/deserializacion JSON |
| Coil | 2.7.0 | Carga de imagenes (preparado) |
| SharedPreferences | Android SDK | Persistencia de sesion local |

### Configuracion del proyecto

- **compileSdk**: 36
- **minSdk**: 24 (Android 7.0)
- **targetSdk**: 36

---

## Pantallas

### Login
El usuario ingresa su nombre y selecciona su rol (Mesero o Cocinero). Si ya existe un usuario con ese nombre y rol, reutiliza la cuenta. Si no, crea uno nuevo via la API.

### Vista del Mesero
- **Nuevo Pedido**: navega por categorias (Comidas, Bebidas, Postres), agrega productos al carrito con notas opcionales, indica el numero de mesa y envia a cocina.
- **Mis Pedidos**: lista los pedidos creados por el mesero con su estado actualizado en tiempo real.

### Vista del Cocinero
Muestra todas las ordenes pendientes y en preparacion. Cada tarjeta tiene:
- Numero de mesa y badge de estado con colores diferenciados.
- Lista de productos con cantidades y notas.
- Botones contextuales: "Preparar" (solo para pendientes) y "Listo para entregar" (solo para ordenes en preparacion).

---

## Como ejecutar

1. Clonar el repositorio.
2. Abrir el proyecto en Android Studio.
3. Sincronizar Gradle.
4. Conectar un dispositivo o iniciar un emulador (minSdk 24).
5. Ejecutar la app.

El backend ya esta desplegado en `http://50.16.170.148/` y el WebSocket en `ws://50.16.170.148/ws`.

---

## Estructura de la API

| Metodo | Endpoint | Descripcion |
|---|---|---|
| GET | `/api/orders` | Listar ordenes (filtrar por `?status=` y `?table=`) |
| GET | `/api/orders/{id}` | Obtener una orden por ID |
| POST | `/api/orders` | Crear una nueva orden |
| PUT | `/api/orders/{id}/status` | Actualizar estado de una orden |
| GET | `/api/products` | Listar productos (filtrar por `?category=`) |
| GET | `/api/users` | Listar usuarios |
| GET | `/api/users/{id}` | Obtener usuario por ID |
| POST | `/api/users` | Crear usuario |

---

## Ciclo de vida de un pedido

| Estado | Significado | Quien lo cambia |
|---|---|---|
| `pending` | Orden recien creada, esperando al cocinero | Mesero (al enviar) |
| `preparing` | El cocinero comenzo a preparar la orden | Cocinero |
| `ready` | La orden esta lista para ser entregada | Cocinero |
| `delivered` | La orden fue entregada al cliente | (Futuro) |
